package cn.cotenite.dickhelper.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun desktopDatabaseBuilder(): RoomDatabase.Builder<ShareDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "record.db")
    return Room.databaseBuilder<ShareDatabase>(
        name = dbFile.absolutePath,
    )
}
