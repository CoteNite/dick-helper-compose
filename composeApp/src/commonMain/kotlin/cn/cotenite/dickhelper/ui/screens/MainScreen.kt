package cn.cotenite.dickhelper.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.awt.Color

@Composable
fun MainScreen(){

    var notes by remember { mutableStateOf("") }
    var showStopwatch by remember { mutableStateOf(false) } // 控制秒表显示
    var startTime by remember { mutableStateOf(0L) } // 记录开始时间（毫秒）
    var elapsedTime by remember { mutableStateOf(0L) } // 记录经过时间（毫秒）
    var isRunning by remember { mutableStateOf(false) } // 控制秒表是否正在运行

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay(10)
                elapsedTime = System.currentTimeMillis() - startTime
            }
        }
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Text("开启今天的第一发把！！！")

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.padding(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ){
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = "记录新的手艺活",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (showStopwatch) {
                    StopwatchDisplay(elapsedTime = elapsedTime)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                    ) {
                        Button(
                            onClick = {
                                isRunning = false
                                // TODO: 保存或处理计时结果
                            },
                            enabled = isRunning, // 只有在计时运行时才启用停止按钮
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Filled.Stop, contentDescription = "停止")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("停止")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                isRunning = false // 停止计时
                                showStopwatch = false // 返回初始界面
                                startTime = 0L // 重置计时数据
                                elapsedTime = 0L // 重置计时数据
                                // TODO: 这里可以添加最终保存或处理计时结果的逻辑
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // 结束按钮可以使用主色
                        ) {
                            Icon(Icons.Filled.Done, contentDescription = "结束") // 使用“完成”图标
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("结束")
                        }
                    }
                }else{
                    Text(
                        text = "准备开始",
                        style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            showStopwatch = true
                            startTime = System.currentTimeMillis() // 记录开始时间
                            elapsedTime = 0L // 重置已过时间
                            isRunning = true // 开始计时
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // 支付宝蓝色
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.PlayArrow,
                                contentDescription = "开始",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("开始", fontSize = 18.sp)
                        }
                    }
                }
            }

        }


    }
}


@Composable
fun StopwatchDisplay(elapsedTime: Long) {
    val minutes = (elapsedTime / 60000) % 60
    val seconds = (elapsedTime / 1000) % 60
    val milliseconds = elapsedTime % 1000

    Text(
        text = String.format("%02d:%02d.%03d", minutes, seconds, milliseconds),
        style = MaterialTheme.typography.displayMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}
