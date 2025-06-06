package cn.cotenite.dickhelper.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import cn.cotenite.dickhelper.database.entity.Record


@Dao
interface RecordDao {

    @Insert
    suspend fun insertRecord(record: Record)

    @Query("SELECT * FROM record ORDER BY date DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM record WHERE id = :id")
    suspend fun getRecordById(id: Long): Record?

    @Query("DELETE FROM record WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("SELECT * FROM record WHERE date >= :startDateMs AND date <= :endDateMs ORDER BY date ASC")
    fun getRecordsBetweenDates(startDateMs: Long, endDateMs: Long): Flow<List<Record>>

}