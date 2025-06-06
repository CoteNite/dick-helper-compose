package cn.cotenite.dickhelper.viewModle.timing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cotenite.dickhelper.service.RecordService
import cn.cotenite.dickhelper.util.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TimingViewModel(
    private val recordService: RecordService
):ViewModel(){

    private val _state = MutableStateFlow<TimingState>(TimingState.Default)
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<TimingEffect>()
    val effect = _effect.asSharedFlow()

    private var timerJob: Job? = null
    private var timeMonitorJob:Job?=null

    private val _word = MutableStateFlow("")
    val word = _word.asStateFlow()

    init {
        timeMonitorJob = viewModelScope.launch {
            while (currentCoroutineContext().isActive) {
                var newWord = ""
                val morningStart = LocalTime(5, 0)
                val afternoonStart = LocalTime(12, 0)
                val eveningStart = LocalTime(18, 0)
                val eveningEnd = LocalTime(5, 0) // 次日凌晨 5:00

                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time

                when {
                    now >= morningStart && now < afternoonStart -> newWord = "一日之际在于晨，今天你准备好了吗？"
                    now >= afternoonStart && now < eveningStart -> newWord = "中午了，午休了吗？没有的话🦌一发睡吧！"
                    now >= eveningStart || now < eveningEnd -> newWord = "都这么晚了还在努力吗，实在累了可以🦌一发睡哦"
                }
                _word.value = newWord // 直接设置 value 也可以，但 update 提供了原子性保证
                delay(60_000L) // 每分钟检查一次
            }
        }
    }

    fun dispatch(intent: TimingIntent) {
        when (intent) {
            TimingIntent.SelectNormalMode -> handleSelectNormalMode()
            TimingIntent.StopTimer -> handleStopTimer()
            TimingIntent.EndTimer -> handleEndTimer()
            TimingIntent.SelectChallengeMode -> handleSelectChallengeMode()
            TimingIntent.ContinueTimer -> handleContinueTimer()
            is TimingIntent.SaveNotes -> handleSaveNotes(intent.notes, intent.sessionDuration, intent.sessionMode) // 处理保存感想
            TimingIntent.CancelNotesDialog -> handleCancelNotesDialog() // 处理取消弹窗
        }
    }

    private fun handleSaveNotes(notes: String, duration: Long, mode: String) {
        viewModelScope.launch {
            recordService.saveNewRecord(notes, duration, mode)
            _state.update { TimingState.Default } // 保存后重置状态
        }
        _state.update { TimingState.Default } // 保存后将状态重置为 Default
    }

    private fun handleCancelNotesDialog() {
        _state.update { TimingState.Default } // 取消后也将状态重置为 Default
    }

    private fun handleSelectNormalMode() {
        _state.update {
            TimingState.Normal(
                elapsedTime = 0L,
                isRunning = true,
            )
        }
        startTimer(System.currentTimeMillis(), RecordMode.NORMAL)
    }

    private fun handleSelectChallengeMode() {
        val challengeMinutes = 3
        val challengeSeconds = 0
        val customDurationMillis = (challengeMinutes * 60 + challengeSeconds) * 1000L
        val challengeEndTime = System.currentTimeMillis() + customDurationMillis

        _state.update {
            TimingState.Challenge(
                remainingTime = customDurationMillis,
                totalDuration = customDurationMillis,
                isRunning = true
            )
        }
        startTimer(challengeEndTime, RecordMode.CHALLENGE)
    }

    private fun handleStopTimer() {
        stopTimer()
        _state.update { currentState ->
            when (currentState) {
                is TimingState.Challenge -> currentState.copy(isRunning = false)
                is TimingState.Normal -> currentState.copy(isRunning = false)
                else -> currentState
            }
        }
    }

    private fun handleContinueTimer() {
        val currentState = _state.value // 先获取当前状态
        if (currentState is TimingState.Normal && !currentState.isRunning) {
            val resumedStartTime = System.currentTimeMillis() - currentState.elapsedTime
            _state.update { (it as TimingState.Normal).copy(isRunning = true) } // 确保状态变为 Running
            startTimer(resumedStartTime, RecordMode.NORMAL)
        }
    }

    // ***** 修改这里 *****
    private fun handleEndTimer() {
        stopTimer() // 停止计时协程

        val currentState = _state.value
        val sessionDuration: Long
        val sessionMode: RecordMode

        when (currentState) {
            is TimingState.Normal -> {
                sessionDuration = currentState.elapsedTime
                sessionMode = RecordMode.NORMAL
                _state.update { currentState.copy(isRunning = false) } // 确保状态为停止，但数据保留
            }
            is TimingState.Challenge -> {
                // 计算挑战实际完成的时长
                sessionDuration = currentState.totalDuration - currentState.remainingTime
                sessionMode = RecordMode.CHALLENGE
                _state.update { currentState.copy(isRunning = false) } // 确保状态为停止，但数据保留
            }
            else -> {
                // 如果在 Default 状态下触发 EndTimer (不应发生)，则直接结束并重置状态
                sessionDuration = 0L
                sessionMode = RecordMode.NONE
                _state.update { TimingState.Default }
                return // 直接返回，不显示弹窗
            }
        }

        // 发送副作用，通知 UI 显示感想弹窗
        viewModelScope.launch {
            _effect.emit(TimingEffect.ShowNotesDialog(sessionDuration, sessionMode))
        }
        // _state 不在这里直接重置为 Default，而是等待用户在弹窗中操作
    }


    private fun startTimer(referenceTimeMillis: Long, mode: RecordMode) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val initialTimingState = _state.value

            val isInitialStateCorrect = when (initialTimingState) {
                is TimingState.Normal -> mode == RecordMode.NORMAL
                is TimingState.Challenge -> mode == RecordMode.CHALLENGE
                else -> false
            }

            if (!isInitialStateCorrect) {
                return@launch
            }

            val actualStartTimeForNormalCalc = if (mode == RecordMode.NORMAL) referenceTimeMillis else 0L

            while (currentCoroutineContext().isActive) {
                val shouldContinue = when (val currentState = _state.value) {
                    is TimingState.Normal -> currentState.isRunning && mode == RecordMode.NORMAL
                    is TimingState.Challenge -> currentState.isRunning && mode == RecordMode.CHALLENGE
                    else -> false
                }
                if (!shouldContinue) {
                    break
                }

                delay(10)

                val newTimeValue = when (mode) {
                    RecordMode.NORMAL -> System.currentTimeMillis() - actualStartTimeForNormalCalc
                    RecordMode.CHALLENGE -> {
                        val remaining = referenceTimeMillis - System.currentTimeMillis()
                        if (remaining <= 0) {
                            _state.update { current ->
                                if (current is TimingState.Challenge) {
                                    current.copy(remainingTime = 0L, isRunning = false)
                                } else current
                            }
                            // 挑战结束时也应该触发弹窗，所以这里调用 handleEndTimer()
                            handleEndTimer() // <--- 注意这里，让 challenge 结束也走统一的结束流程
                            break // 退出循环
                        }
                        remaining
                    }
                    RecordMode.NONE -> 0L
                }

                _state.update { current ->
                    when (current) {
                        is TimingState.Normal -> current.copy(elapsedTime = newTimeValue)
                        is TimingState.Challenge -> current.copy(remainingTime = newTimeValue)
                        else -> current
                    }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        timeMonitorJob?.cancel() // 在 ViewModel 清理时取消 timeMonitorJob
        timeMonitorJob = null
    }
}