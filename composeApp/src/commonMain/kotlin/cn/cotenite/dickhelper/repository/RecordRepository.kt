package cn.cotenite.dickhelper.repository

import cn.cotenite.dickhelper.database.ShareDatabase
import cn.cotenite.dickhelper.database.dao.RecordDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import cn.cotenite.dickhelper.database.entity.Record

class RecordRepository(
    private val recordDao: RecordDao
){
    private val dispatcher= Dispatchers.IO

    /**
     * 插入一条新的记录
     * @param record 要插入的 Record 对象
     */
    suspend fun insertRecord(record: Record) = withContext(dispatcher) {
        recordDao.insertRecord(record)
    }

    /**
     * 获取所有记录，返回一个 Flow 以便观察数据库变化
     * @return 包含所有 Record 列表的 Flow
     */
    fun getAllRecords(): Flow<List<Record>> {
        return recordDao.getAllRecords()
    }

    /**
     * 根据 ID 获取单个记录
     * @param id 记录的 ID
     * @return 匹配的 Record 对象，如果不存在则返回 null
     */
    suspend fun getRecordById(id: Long): Record? = withContext(dispatcher) {
        recordDao.getRecordById(id)
    }

    /**
     * 根据 ID 删除记录
     * @param id 要删除记录的 ID
     */
    suspend fun deleteRecordById(id: Long) = withContext(dispatcher) {
        recordDao.deleteRecordById(id)
    }

    fun getRecordsBetweenDates(startOfDay: Long, endOfDay: Long): Flow<List<Record>> {
        return recordDao.getRecordsBetweenDates(startOfDay, endOfDay)
    }
}