package cn.cotenite.dickhelper.viewModle.setting

sealed class SettingIntent{
    data class ChangeTheme(val isDark:Boolean): SettingIntent()
}