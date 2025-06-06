package cn.cotenite.dickhelper.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

class CreateDatabase(
    private val builder:RoomDatabase.Builder<ShareDatabase>
){
    fun getDateBase(): ShareDatabase {
        return builder
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}