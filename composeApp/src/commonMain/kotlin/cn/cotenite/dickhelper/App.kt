package cn.cotenite.dickhelper

import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cn.cotenite.dickhelper.nav.NavHostApp
import cn.cotenite.dickhelper.ui.theme.AppTheme
import cn.cotenite.dickhelper.viewModle.setting.SettingIntent
import cn.cotenite.dickhelper.viewModle.setting.UIThemeState
import cn.cotenite.dickhelper.viewModle.setting.SettingViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
@Preview
fun App(
    darkTheme: Boolean,
    dynamicColor: Boolean,
) {
    val viewModel: SettingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Surface {
        when (uiState) {
            UIThemeState.Dark, UIThemeState.Light -> Theme(uiState.isDark,dynamicColor)
            UIThemeState.Default -> Theme(darkTheme,dynamicColor)
        }
    }
}

@Composable
fun Theme(
    theme: Boolean,
    dynamicColor: Boolean,
    viewModel: SettingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AppTheme(
        darkTheme = theme,
        dynamicColor = dynamicColor
    ) {
        Surface(
            modifier = Modifier
                .captionBarPadding()
                .statusBarsPadding()
                .systemBarsPadding()
                .navigationBarsPadding()
                .fillMaxSize(),
        ) {
            NavHostApp {
                MyTopAppBar(
                    isDark = uiState.isDark,
                    onThemeToggle = {
                        viewModel.dispatch(SettingIntent.ChangeTheme(!uiState.isDark))
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(
    isDark: Boolean,
    onThemeToggle: () -> Unit
) {
    TopAppBar(
        title = { Text("牛子助手🐂") },
        actions = {
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = when (isDark) {
                        false -> Icons.Rounded.WbSunny
                        true -> Icons.Rounded.DarkMode
                    },
                    tint = MaterialTheme.colorScheme.onPrimary,
                    contentDescription = "Toggle Theme"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}