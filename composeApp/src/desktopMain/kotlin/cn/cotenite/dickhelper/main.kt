package cn.cotenite.dickhelper

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cn.cotenite.dickhelper.di.desktopDatabaseModule
import cn.cotenite.dickhelper.di.shareModule
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DickHelper-Compose",
    ) {
        startKoin {
            modules(desktopDatabaseModule,shareModule)
        }

        App(
            darkTheme = isSystemInDarkTheme(),
            dynamicColor = false,
        )
    }
}