package cn.cotenite.dickhelper.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun androidDatabaseBuilder(ctx: Context): RoomDatabase.Builder<ShareDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("record.db")
    return Room.databaseBuilder<ShareDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}