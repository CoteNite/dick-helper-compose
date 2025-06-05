package cn.cotenite.dickhelper.viewModle

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainViewModel:ViewModel(){

    var uiState by mutableStateOf<MainUIState>(MainUIState.Default)
        private set

    fun dispatch(action:MainAction){
       when(action){
           is MainAction.ChangeTheme->{
               uiState = if(action.isDark){
                   MainUIState.Dark
               }else{
                   MainUIState.Light
               }
           }
       }
   }

}

sealed class MainUIState(val isDark: Boolean){
    data object Default:MainUIState(false)
    data object Dark:MainUIState(true)
    data object Light:MainUIState(false)
}

sealed class MainAction{
    data class ChangeTheme(val isDark:Boolean):MainAction()
}