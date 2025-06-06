package cn.cotenite.dickhelper.viewModle.todady

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.cotenite.dickhelper.database.entity.Record
import cn.cotenite.dickhelper.service.RecordService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TodayViewModel(
    private val recordService: RecordService
) : ViewModel() {

    val todayRecords: StateFlow<List<Record>> =
        recordService.getTodayRecords()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // 当有订阅者时开始收集，并在最后一个订阅者消失后保持5秒
                initialValue = emptyList() // 初始值为空列表
            )

    // 今天的记录次数
    val todayRecordCount: StateFlow<Int> =
        todayRecords.map { records ->
            records.size // 直接获取列表大小作为次a数
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0 // 初始值为 0
        )
}