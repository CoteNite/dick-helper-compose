package cn.cotenite.dickhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cn.cotenite.dickhelper.di.androidDatabaseModule
import cn.cotenite.dickhelper.di.shareModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity)
            modules(androidDatabaseModule,shareModule)
        }

        setContent {
            App(
                darkTheme = isSystemInDarkTheme(),
                dynamicColor = true,
            )
        }
    }
}
