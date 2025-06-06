package cn.cotenite.dickhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.cotenite.dickhelper.viewModle.timing.RecordMode
import cn.cotenite.dickhelper.viewModle.timing.TimingEffect
import cn.cotenite.dickhelper.viewModle.timing.TimingIntent
import cn.cotenite.dickhelper.viewModle.timing.TimingState
import cn.cotenite.dickhelper.viewModle.timing.TimingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimingScreen(
    timingViewModel: TimingViewModel = koinViewModel()
){

    val state by timingViewModel.state.collectAsState()
    val word by timingViewModel.word.collectAsState()

    var showNotesDialog by remember { mutableStateOf(false) }
    var dialogSessionDuration by remember { mutableStateOf(0L) }
    var dialogSessionMode by remember { mutableStateOf(RecordMode.NONE) }

    LaunchedEffect(Unit) {
        timingViewModel.effect.collect { effect ->
            when (effect) {
                is TimingEffect.ShowNotesDialog -> {
                    dialogSessionDuration = effect.sessionDuration
                    dialogSessionMode = effect.sessionMode
                    showNotesDialog = true
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(word)

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "记录新的手艺活",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                when (state) {
                    TimingState.Default -> {
                        Text(
                            text = "选择您喜欢的方式",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        //普通模式按钮
                        Button(
                            onClick = { timingViewModel.dispatch(TimingIntent.SelectNormalMode) },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .heightIn(min = 48.dp, max = 48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1677FF))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "开始普通模式", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("普通模式", fontSize = 18.sp, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        //挑战模式按钮
                        Button(
                            onClick = {
                                timingViewModel.dispatch(TimingIntent.SelectChallengeMode)
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .heightIn(min = 48.dp, max = 48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = "开始挑战模式", tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("挑战模式", fontSize = 18.sp, color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                    }
                    is TimingState.Normal -> {
                        StopwatchDisplay(
                            elapsedTime = (state as TimingState.Normal).elapsedTime,
                            isCountdown = false
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 智能类型推断，无需 'as TimingState.Normal'
                            if ((state as TimingState.Normal).isRunning) { // 直接访问 state.isRunning
                                Button(
                                    onClick = { timingViewModel.dispatch(TimingIntent.StopTimer) },
                                    enabled = (state as TimingState.Normal).isRunning, // 直接访问 state.isRunning
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Icon(Icons.Filled.Stop, contentDescription = "停止")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("停止")
                                }
                            } else {
                                Button(
                                    onClick = { timingViewModel.dispatch(TimingIntent.ContinueTimer) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, contentDescription = "继续")
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("继续")
                                }
                            }
                            Button(
                                onClick = { timingViewModel.dispatch(TimingIntent.EndTimer) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Done, contentDescription = "结束")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("结束")
                            }
                        }
                    }
                    is TimingState.Challenge -> {
                        StopwatchDisplay(
                            elapsedTime = (state as TimingState.Challenge).remainingTime,
                            isCountdown = true
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { timingViewModel.dispatch(TimingIntent.EndTimer) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Done, contentDescription = "结束")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("结束")
                            }
                        }
                    }
                }

            }
        }
    }

    if (showNotesDialog) {
        NotesInputDialog(
            sessionDuration = dialogSessionDuration,
            sessionMode = dialogSessionMode,
            onConfirm = { notes ->
                timingViewModel.dispatch(TimingIntent.SaveNotes(notes, dialogSessionDuration, dialogSessionMode.docs))
                showNotesDialog = false // 隐藏弹窗
            },
            onCancel = {
                timingViewModel.dispatch(TimingIntent.CancelNotesDialog)
                showNotesDialog = false // 隐藏弹窗
            }
        )
    }
}

@Composable
fun StopwatchDisplay(elapsedTime: Long, isCountdown: Boolean = false) {
    val actualElapsedTime = if (elapsedTime < 0) 0L else elapsedTime

    val totalSeconds = actualElapsedTime / 1000
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    val milliseconds = actualElapsedTime % 1000

    Text(
        text = String.format("%02d:%02d.%03d", minutes, seconds, milliseconds),
        style = MaterialTheme.typography.displayMedium.copy(fontFamily = FontFamily.Monospace),
        color = if (isCountdown && actualElapsedTime <= 10000 && actualElapsedTime > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    )
}

// 感想输入弹窗 Composable
@OptIn(ExperimentalMaterial3Api::class) // 标记为实验性 API
@Composable
fun NotesInputDialog(
    sessionDuration: Long,
    sessionMode: RecordMode,
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var notesText by remember { mutableStateOf("") }
    val durationFormatted = when (sessionMode) {
        RecordMode.NORMAL -> {
            val totalSeconds = sessionDuration / 1000
            val minutes = (totalSeconds / 60) % 60
            val seconds = totalSeconds % 60
            String.format("%02d分%02d秒", minutes, seconds)
        }
        RecordMode.CHALLENGE -> {
            // 挑战模式下，sessionDuration 已经是完成的时长
            val totalSeconds = sessionDuration / 1000
            val minutes = (totalSeconds / 60) % 60
            val seconds = totalSeconds % 60
            String.format("挑战完成%02d分%02d秒", minutes, seconds)
        }
        RecordMode.NONE -> "未知时长"
    }

    AlertDialog(
        onDismissRequest = onCancel, // 点击弹窗外部或按返回键时触发取消
        title = {
            Text(text = "记录本次感想")
        },
        text = {
            Column {
                Text("本次模式：${sessionMode.displayText()}，时长：${durationFormatted}")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("输入您的感想...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        onConfirm(notesText)
                    })
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(notesText) }) {
                Text("保存")
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text("取消")
            }
        }
    )
}

fun RecordMode.displayText(): String {
    return when (this) {
        RecordMode.NONE -> "未选择"
        RecordMode.NORMAL -> "普通"
        RecordMode.CHALLENGE -> "挑战"
    }
}