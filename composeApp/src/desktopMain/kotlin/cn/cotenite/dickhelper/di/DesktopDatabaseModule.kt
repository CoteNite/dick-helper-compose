package cn.cotenite.dickhelper.di

import androidx.room.RoomDatabase
import cn.cotenite.dickhelper.database.ShareDatabase
import cn.cotenite.dickhelper.database.desktopDatabaseBuilder
import org.koin.dsl.module

val desktopDatabaseModule= module {

    single<RoomDatabase.Builder<ShareDatabase>>{
        desktopDatabaseBuilder()
    }
}