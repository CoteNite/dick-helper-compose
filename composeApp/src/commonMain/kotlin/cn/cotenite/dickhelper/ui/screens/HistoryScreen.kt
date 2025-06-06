package cn.cotenite.dickhelper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.cotenite.dickhelper.viewModle.history.HistoryViewModel
import kotlinx.datetime.*
import org.koin.compose.viewmodel.koinViewModel
import cn.cotenite.dickhelper.database.entity.Record
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    historyViewModel: HistoryViewModel = koinViewModel(),
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val dailyContributions by historyViewModel.dailyContributions.collectAsState()
    val allDates = historyViewModel.getDatesForContributionGraph()
    val totalContributions by historyViewModel.totalContributionsThisYear.collectAsState()

    val selectedDate by historyViewModel.selectedDate.collectAsState()
    val selectedDateRecords by historyViewModel.selectedDateRecords.collectAsState()

    val lazyListState = rememberLazyListState()

    // 自动滚动到今天所在的周
    LaunchedEffect(allDates) {
        if (allDates.isNotEmpty()) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val todayIndex = allDates.indexOf(today)
            if (todayIndex != -1) {
                val currentWeekIndex = todayIndex / 7
                lazyListState.animateScrollToItem(maxOf(0, currentWeekIndex - 3))
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LocalDensity.current
        val screenWidthDp = maxWidth.value.toInt()

        val squareSize = when {
            screenWidthDp < 360 -> 10.dp
            screenWidthDp < 600 -> 12.dp
            screenWidthDp < 900 -> 14.dp
            else -> 16.dp
        }
        val spacing = (squareSize.value * 0.2f).dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal =16.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 标题和总次数
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "贡献图",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "今年: $totalContributions 次",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.End
                )
            }

            // 贡献图卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 主要改动点：传入合并后的贡献图布局
                    ContributionGraphLayout(
                        allDates = allDates,
                        contributions = dailyContributions,
                        squareSize = squareSize,
                        spacing = spacing,
                        lazyListState = lazyListState,
                        getContributionLevel = { count -> historyViewModel.getContributionLevel(count) },
                        onSquareClick = { date, _ ->
                            historyViewModel.setSelectedDate(date)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ContributionLegend(squareSize = squareSize)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 显示选中日期的记录列表
            DailyRecordsList(records = selectedDateRecords, selectedDate = selectedDate)
        }
    }
}

// 主要改动点：重构此函数
@Composable
private fun ContributionGraphLayout(
    allDates: List<LocalDate>,
    contributions: Map<LocalDate, Int>,
    squareSize: Dp,
    spacing: Dp,
    lazyListState: androidx.compose.foundation.lazy.LazyListState,
    getContributionLevel: (Int) -> Int,
    onSquareClick: (LocalDate, Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 左侧星期标签 (需要微调)
        WeekdayLabels(
            squareSize = squareSize,
            spacing = spacing
        )

        // 右侧滚动区域 (现在只有一个 LazyRow)
        LazyRow(
            state = lazyListState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            val numWeeks = (allDates.size + 6) / 7 // 向上取整

            items(numWeeks) { weekIndex ->
                // 每个item现在是一个包含“月份标签”和“一周方块”的完整垂直列
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. 月份标签逻辑
                    val dateIndex = weekIndex * 7
                    val weekStart = if (dateIndex < allDates.size) allDates[dateIndex] else null
                    val prevWeekStart = if (weekIndex > 0 && ((weekIndex - 1) * 7) < allDates.size) allDates[(weekIndex - 1) * 7] else null
                    val shouldShowMonth = weekStart != null && (weekIndex == 0 || weekStart.month != prevWeekStart?.month)

                    // 月份标签的占位符，与左侧星期标签的顶部间隔对齐
                    Box(
                        modifier = Modifier
                            .width(squareSize)
                            .height(squareSize), // 高度为方块大小
                        contentAlignment = Alignment.BottomStart
                    ) {
                        if (shouldShowMonth) {
                            Text(
                                text = weekStart!!.month.toChineseString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = (squareSize.value * 0.5f).sp,
                                textAlign = TextAlign.Start
                            )
                        }
                    }

                    // 月份标签和方块网格之间的间距
                    Spacer(modifier = Modifier.height(spacing))

                    // 2. 一周的贡献方块 (7个)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing),
                        modifier = Modifier.width(squareSize) // 确保列宽度与方格大小一致
                    ) {
                        repeat(7) { dayIndex ->
                            val currentDateIndex = weekIndex * 7 + dayIndex
                            if (currentDateIndex < allDates.size) {
                                val date = allDates[currentDateIndex]
                                val count = contributions[date] ?: 0

                                ContributionSquare(
                                    count = count,
                                    date = date,
                                    size = squareSize,
                                    getContributionLevel = getContributionLevel,
                                    onClick = { onSquareClick(date, count) }
                                )
                            } else {
                                // 空白占位，保持每列有7个方块的高度
                                Spacer(modifier = Modifier.size(squareSize))
                            }
                        }
                    }
                }
            }
        }
    }
}

// 主要改动点：调整顶部的Spacer
@Composable
private fun WeekdayLabels(
    squareSize: Dp,
    spacing: Dp
) {
    Column(
        modifier = Modifier.padding(end = spacing * 2),
        horizontalAlignment = Alignment.End
    ) {
        Spacer(modifier = Modifier.height(squareSize + spacing))

        val weekdays = listOf("", "一", "", "三", "", "五", "")

        weekdays.forEach { day ->
            Box(
                modifier = Modifier
                    .height(squareSize + spacing)
                    .wrapContentWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (day.isNotBlank()) {
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = (squareSize.value * 0.6f).sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContributionSquare(
    count: Int,
    date: LocalDate,
    size: Dp,
    getContributionLevel: (Int) -> Int,
    onClick: () -> Unit
) {
    val level = getContributionLevel(count)
    val color = getContributionColor(level)
    val tooltipState = rememberTooltipState()
    val scope = rememberCoroutineScope()

    TooltipBox(
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip {
                Text("${count} contributions on ${date.month.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} ${date.dayOfMonth}th.")
            }
        },
        state = tooltipState
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .clickable(onClick = {
                    onClick()
                    scope.launch { tooltipState.show() }
                })
        )
    }
}

@Composable
private fun ContributionLegend(squareSize: Dp) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "少",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = (squareSize.value * 0.6f).sp
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            repeat(5) { level ->
                Box(
                    modifier = Modifier
                        .size(squareSize * 0.8f)
                        .clip(RoundedCornerShape(1.dp))
                        .background(getContributionColor(level))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "多",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = (squareSize.value * 0.6f).sp
        )
    }
}

@Composable
private fun getContributionColor(level: Int): Color {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (isDark) {
        when (level) {
            0 -> Color(0xFF161b22)
            1 -> Color(0xFF0e4429)
            2 -> Color(0xFF006d32)
            3 -> Color(0xFF26a641)
            4 -> Color(0xFF39d353)
            else -> Color(0xFF161b22)
        }
    } else {
        when (level) {
            0 -> Color(0xFFebedf0)
            1 -> Color(0xFF9be9a8)
            2 -> Color(0xFF40c463)
            3 -> Color(0xFF30a14e)
            4 -> Color(0xFF216e39)
            else -> Color(0xFFebedf0)
        }
    }
}

private fun Month.toChineseString(): String {
    return when (this) {
        Month.JANUARY -> "1月"
        Month.FEBRUARY -> "2月"
        Month.MARCH -> "3月"
        Month.APRIL -> "4月"
        Month.MAY -> "5月"
        Month.JUNE -> "6月"
        Month.JULY -> "7月"
        Month.AUGUST -> "8月"
        Month.SEPTEMBER -> "9月"
        Month.OCTOBER -> "10月"
        Month.NOVEMBER -> "11月"
        Month.DECEMBER -> "12月"
    }
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

@Composable
fun DailyRecordsList(records: List<Record>, selectedDate: LocalDate?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        if (selectedDate != null) {
            Text(
                text = "${selectedDate.year}年${selectedDate.month.toChineseString()}${selectedDate.dayOfMonth}日 记录 (${records.size} 次)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (records.isEmpty()) {
                Text(
                    text = "当天没有记录。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(records) { record ->
                        HistoryRecordItem(record = record)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        } else {
            Text(
                text = "点击方格查看每日记录。",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HistoryRecordItem(record: Record) {
    val localDateTime = Instant.fromEpochMilliseconds(record.date)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedTime = "${localDateTime.hour.toString().padStart(2, '0')}:" +
            "${localDateTime.minute.toString().padStart(2, '0')}:" +
            localDateTime.second.toString().padStart(2, '0')

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "时间: $formattedTime",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "模式: ${record.mode}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalSeconds = record.duration / 1000
            val minutes = (totalSeconds / 60) % 60
            val seconds = totalSeconds % 60
            val durationFormatted = if (minutes > 0) {
                String.format("%02d分%02d秒", minutes, seconds)
            } else {
                String.format("%02d秒", seconds)
            }

            Text(
                text = "时长: $durationFormatted",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (record.content.isNotBlank()) {
                Text(
                    text = "感想: ${record.content}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}