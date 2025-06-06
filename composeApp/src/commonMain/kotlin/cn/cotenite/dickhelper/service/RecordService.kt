package cn.cotenite.dickhelper.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map // 导入 map 操作符
import cn.cotenite.dickhelper.database.entity.Record
import cn.cotenite.dickhelper.repository.RecordRepository // 确保导入路径正确

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock // 导入 Clock
import kotlinx.datetime.LocalDate // 导入 LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class RecordService(
    private val recordRepository: RecordRepository
) {

    suspend fun saveNewRecord(content: String, duration: Long, mode: String) {
        if (duration <= 0) {
            throw IllegalArgumentException("记录内容和时长不能同时为空或无效")
        }

        val newRecord = Record(
            content = content.ifBlank { "你这天很累，甚至🦌完都没有时间记录一下" },
            date = System.currentTimeMillis(),
            mode = mode,
            duration = duration
        )
        recordRepository.insertRecord(newRecord)
    }
    /**
     * 获取所有手艺活记录
     * @return 包含所有 Record 列表的 Flow
     */
    fun getAllRecords(): Flow<List<Record>> {
        return recordRepository.getAllRecords()
    }

    /**
     * 获取今天的手艺活记录
     * @return 包含今天所有 Record 列表的 Flow
     */
    fun getTodayRecords(): Flow<List<Record>> {
        // 获取所有记录，然后过滤出今天的记录
        return recordRepository.getAllRecords().map { allRecords ->
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date // 获取今天的日期（不含时间）
            allRecords.filter { record ->
                val recordDate = Instant.fromEpochMilliseconds(record.date)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date // 获取记录的日期
                recordDate == today // 比较日期是否是今天
            }
        }
    }

    fun getRecordsByDate(date: LocalDate): Flow<List<Record>> {
        val startOfDay = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val endOfDay = date.plus(1, kotlinx.datetime.DateTimeUnit.DAY)
            .atStartOfDayIn(TimeZone.currentSystemDefault()).minus(1, kotlinx.datetime.DateTimeUnit.MILLISECOND)
            .toEpochMilliseconds()
        return recordRepository.getRecordsBetweenDates(startOfDay, endOfDay)
    }
}