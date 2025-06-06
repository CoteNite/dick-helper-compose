package cn.cotenite.dickhelper.viewModle.setting

sealed class UIThemeState(val isDark: Boolean){
    data object Default: UIThemeState(false)
    data object Dark: UIThemeState(true)
    data object Light: UIThemeState(false)
}