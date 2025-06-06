package cn.cotenite.dickhelper.nav

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import cn.cotenite.dickhelper.ui.screens.TodayScreen
import cn.cotenite.dickhelper.ui.screens.TimingScreen
import cn.cotenite.dickhelper.ui.screens.HistoryScreen

@Composable
fun NavHostApp(topBar: @Composable () -> Unit){

    val navController=rememberNavController()

    Scaffold(
        topBar={topBar()},
        bottomBar = { BottomBar(navController) }
    ){  paddingValues ->
        NavHost(navController=navController, startDestination =Destination.Today.route ){

            val slideIn = slideInHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth }
            val slideOut = slideOutHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth }
            val slideInBack = slideInHorizontally(animationSpec = tween(500)) { fullWidth -> -fullWidth }
            val slideOutBack = slideOutHorizontally(animationSpec = tween(500)) { fullWidth -> fullWidth }


            composable(
                Destination.Today.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Destination.History.route -> slideInBack // 从历史页面回来
                        Destination.Setting.route -> slideInBack // 从设置页面回来
                        else -> fadeIn() // 默认淡入
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Destination.History.route -> slideOut // 去往历史页面
                        Destination.Setting.route -> slideOut // 去往设置页面
                        else -> fadeOut() // 默认淡出
                    }
                }
            ){
                TimingScreen()
            }


            composable(
                Destination.History.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Destination.Today.route -> slideIn
                        Destination.Setting.route -> slideInBack
                        else -> fadeIn()
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Destination.Today.route -> slideOutBack
                        Destination.Setting.route -> slideOut
                        else -> fadeOut()
                    }
                }
            ){
                TodayScreen(paddingValues = paddingValues)
            }

            composable(
                Destination.Setting.route,
                enterTransition = {
                    when (initialState.destination.route) {
                        Destination.Today.route -> slideIn
                        Destination.History.route -> slideIn
                        else -> fadeIn()
                    }
                },
                exitTransition = {
                    when (targetState.destination.route) {
                        Destination.Today.route -> slideOutBack
                        Destination.History.route -> slideOutBack
                        else -> fadeOut()
                    }
                }
            ){
                HistoryScreen(paddingValues = paddingValues)
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
    data object Today:Destination("timing",Icons.Rounded.Timer)
    data object History:Destination("today",Icons.Rounded.WbSunny)
    data object Setting:Destination("history",Icons.Rounded.History)
}