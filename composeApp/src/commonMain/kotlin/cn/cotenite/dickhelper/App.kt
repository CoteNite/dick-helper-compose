package cn.cotenite.dickhelper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.cotenite.dickhelper.nav.NavHostApp
import cn.cotenite.dickhelper.ui.theme.AppTheme
import cn.cotenite.dickhelper.viewModle.MainAction
import cn.cotenite.dickhelper.viewModle.MainUIState
import cn.cotenite.dickhelper.viewModle.MainViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview



@Composable
@Preview
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    viewModel: MainViewModel= viewModel()
) {
    val uiState=viewModel.uiState

    Surface {
        when (uiState) {
            MainUIState.Dark, MainUIState.Light -> Theme(uiState.isDark,dynamicColor)
            MainUIState.Default -> Theme(darkTheme,dynamicColor)
        }
    }
}

@Composable
fun Theme(
    theme:Boolean,
    dynamicColor: Boolean
){
    AppTheme(
        darkTheme = theme,
        dynamicColor = dynamicColor
    ){
        Surface(
            modifier = Modifier
                .captionBarPadding()
                .statusBarsPadding()
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
        ){
            NavHostApp{
                MyTopAppBar(viewModel())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(mainViewModel: MainViewModel) {
    val theme = mainViewModel.uiState.isDark
    TopAppBar(
        title = { Text("牛子助手🐂") },
        actions = {
            IconButton(onClick = {
                mainViewModel.dispatch(MainAction.ChangeTheme(!theme))
            }) {
                Icon(
                    imageVector = when (theme) {
                        false -> Icons.Rounded.WbSunny // 亮色模式显示太阳
                        true -> Icons.Rounded.DarkMode // 暗色模式显示月亮
                    },
                    contentDescription = "Toggle Theme"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary, // 顶栏背景色
            titleContentColor = MaterialTheme.colorScheme.onPrimary // 顶栏标题颜色
        )
    )
}
