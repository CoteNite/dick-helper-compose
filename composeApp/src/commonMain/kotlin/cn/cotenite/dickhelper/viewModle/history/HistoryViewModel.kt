package cn.cotenite.dickhelper.viewModle.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cotenite.dickhelper.database.entity.Record
import cn.cotenite.dickhelper.service.RecordService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class HistoryViewModel(
    private val recordService: RecordService
) : ViewModel() {

    private val _dailyContributions = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val dailyContributions: StateFlow<Map<LocalDate, Int>> = _dailyContributions.asStateFlow()

    private val _totalContributionsThisYear = MutableStateFlow(0)
    val totalContributionsThisYear: StateFlow<Int> = _totalContributionsThisYear.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate?>(null)
    val selectedDate: StateFlow<LocalDate?> = _selectedDate.asStateFlow()

    private val _selectedDateRecords = MutableStateFlow<List<Record>>(emptyList())
    val selectedDateRecords: StateFlow<List<Record>> = _selectedDateRecords.asStateFlow()

    init {
        viewModelScope.launch {
            recordService.getAllRecords().collect { records ->
                updateContributions(records)
                updateSelectedDateRecords(_selectedDate.value) // 确保这里在所有记录更新时也尝试更新当前选定日期的记录
            }
        }
    }

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        viewModelScope.launch {
            // ⬅️ 关键修改：添加 .first() 来从 Flow 中获取 List<Record>
            val recordsForDate = recordService.getRecordsByDate(date).first()
            _selectedDateRecords.value = recordsForDate
        }
    }

    private fun updateContributions(records: List<Record>) {
        val dailyMap = mutableMapOf<LocalDate, Int>()
        var totalThisYear = 0
        val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

        for (record in records) {
            val date = Instant.fromEpochMilliseconds(record.date)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date

            if (date.year == currentYear) {
                dailyMap[date] = (dailyMap[date] ?: 0) + 1
                totalThisYear++
            }
        }
        _dailyContributions.value = dailyMap
        _totalContributionsThisYear.value = totalThisYear
    }

    private fun updateSelectedDateRecords(date: LocalDate?) {
        if (date != null) {
            viewModelScope.launch {
                // ⬅️ 关键修改：添加 .first() 来从 Flow 中获取 List<Record>
                val recordsForDate = recordService.getRecordsByDate(date).first()
                _selectedDateRecords.value = recordsForDate
            }
        } else {
            _selectedDateRecords.value = emptyList()
        }
    }

    fun getDatesForContributionGraph(): List<LocalDate> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val startOfYear = LocalDate(today.year, 1, 1)
        val endOfYear = LocalDate(today.year, 12, 31)

        val dates = mutableListOf<LocalDate>()
        var currentDate = startOfYear
        while (currentDate <= endOfYear) {
            dates.add(currentDate)
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }
        return dates
    }

    fun getContributionLevel(count: Int): Int {
        return when {
            count == 0 -> 0
            count < 5 -> 1
            count < 10 -> 2
            count < 20 -> 3
            else -> 4
        }
    }}