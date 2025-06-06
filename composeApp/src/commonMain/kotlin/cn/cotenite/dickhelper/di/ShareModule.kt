package cn.cotenite.dickhelper.di

import cn.cotenite.dickhelper.database.CreateDatabase
import cn.cotenite.dickhelper.database.ShareDatabase
import cn.cotenite.dickhelper.repository.RecordRepository
import cn.cotenite.dickhelper.service.RecordService
import cn.cotenite.dickhelper.viewModle.timing.TimingViewModel
import cn.cotenite.dickhelper.viewModle.history.HistoryViewModel
import cn.cotenite.dickhelper.viewModle.setting.SettingViewModel
import cn.cotenite.dickhelper.viewModle.todady.TodayViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val shareModule = module {

    single{
        CreateDatabase(get()).getDateBase()
    }

    single{
        get<ShareDatabase>().recordDao()
    }

    singleOf(::RecordRepository)
    singleOf(::RecordService)

    viewModelOf(::TimingViewModel)
    viewModelOf(::SettingViewModel)
    viewModelOf(::TodayViewModel)
    viewModelOf(::HistoryViewModel)
}
