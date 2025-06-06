package cn.cotenite.dickhelper.viewModle.setting

import androidx.lifecycle.ViewModel
import cn.cotenite.dickhelper.util.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel:ViewModel(){

    private val _uiState = MutableStateFlow<UIThemeState>(UIThemeState.Default)

    val uiState=_uiState.asStateFlow()

    fun dispatch(action: SettingIntent){
       when(action){
           is SettingIntent.ChangeTheme ->{
               _uiState.update {
                   if(action.isDark){
                       UIThemeState.Dark
                   }else{
                       UIThemeState.Light
                   }
               }
           }
       }
   }

}
