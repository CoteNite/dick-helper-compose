package cn.cotenite.dickhelper.di

import androidx.room.RoomDatabase
import cn.cotenite.dickhelper.database.ShareDatabase
import cn.cotenite.dickhelper.database.androidDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidDatabaseModule= module{

    single<RoomDatabase.Builder<ShareDatabase>>{
        androidDatabaseBuilder(androidContext())
    }

}