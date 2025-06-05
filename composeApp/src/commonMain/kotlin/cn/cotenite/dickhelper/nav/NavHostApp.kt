package cn.cotenite.dickhelper.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cn.cotenite.dickhelper.ui.screens.HistoryScreen
import cn.cotenite.dickhelper.ui.screens.MainScreen
import cn.cotenite.dickhelper.ui.screens.SettingScreen
import cn.cotenite.dickhelper.viewModle.MainAction
import cn.cotenite.dickhelper.viewModle.MainViewModel

@Composable
fun NavHostApp(topBar: @Composable () -> Unit){

    val navController=rememberNavController()

    Scaffold(
        topBar={topBar()},
        bottomBar = { BottomBar(navController) }
    ){
        NavHost(navController=navController, startDestination =Destination.Today.route ){
            composable(Destination.Today.route){
                MainScreen()
            }
            composable(Destination.History.route){
                HistoryScreen()
            }
            composable(Destination.Setting.route){
                SettingScreen()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavController){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(Destination.Today, Destination.History,Destination.Setting)

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(screen.icon, contentDescription = screen.route)
                },
                label = { Text(screen.route) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}



sealed class Destination(val route:String,val icon:ImageVector){
    data object Today:Destination("today",Icons.Rounded.WbSunny)
    data object History:Destination("history",Icons.Rounded.History)
    data object Setting:Destination("setting",Icons.Rounded.Settings)
}