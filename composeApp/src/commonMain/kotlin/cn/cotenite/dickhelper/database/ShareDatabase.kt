package cn.cotenite.dickhelper.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import cn.cotenite.dickhelper.database.dao.RecordDao
import cn.cotenite.dickhelper.database.entity.Record


@Database(entities = [Record::class], version = 2, exportSchema = true)
@ConstructedBy(ShareDatabaseConstructor::class)
abstract class ShareDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ShareDatabaseConstructor : RoomDatabaseConstructor<ShareDatabase> {
    override fun initialize(): ShareDatabase
}
