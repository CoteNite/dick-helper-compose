package cn.cotenite.dickhelper.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalTime

@Entity(tableName = "record")
data class Record(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var content: String = "",
    var date: Long = System.currentTimeMillis(),
    var mode: String = "NORMAL",
    var duration: Long = 0L // 存储本次记录的时长 (毫秒)
)