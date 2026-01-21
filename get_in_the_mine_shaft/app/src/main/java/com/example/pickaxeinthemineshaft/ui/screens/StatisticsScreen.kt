package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.ui.viewmodels.StatisticsViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.BoxWithConstraints

/**
 * Statistics Screen - Displays task completion statistics with interactive charts.
 * 
 * Features:
 * - Tasks Completed: Shows daily task completion counts over time
 * - Completion Rate: Shows cumulative completion percentage (0-100%)
 * - Current/Longest Streak: Displays streak progression
 * - Active Habits: Shows count of recurring tasks
 * 
 * All charts include X and Y axes with proper labels and grid lines for readability.
 */
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Statistics",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                var expanded by remember { mutableStateOf(true) }
                StatCard(
                    title = "Tasks Completed",
                    value = uiState.tasksCompleted.toString(),
                    icon = Icons.Default.CheckCircle,
                    color = MaterialTheme.colors.primary,
                    chartValues = uiState.tasksCompletedHistory.map { it.toFloat() },
                    lineColor = Color(0xFF4CAF50),
                    fillColor = Color(0x334CAF50),
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                    chartType = ChartType.TASKS_COMPLETED
                )
            }
            item {
                var expanded by remember { mutableStateOf(true) }
                StatCard(
                    title = "Current Streak",
                    value = "${uiState.currentStreak} days",
                    icon = Icons.Default.Favorite,
                    color = MaterialTheme.colors.error,
                    chartValues = uiState.currentStreakHistory.map { it.toFloat() },
                    lineColor = Color(0xFFF44336),
                    fillColor = Color(0x33F44336),
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }
            item {
                var expanded by remember { mutableStateOf(true) }
                StatCard(
                    title = "Longest Streak",
                    value = "${uiState.longestStreak} days",
                    icon = Icons.Default.Star,
                    color = MaterialTheme.colors.primaryVariant,
                    chartValues = uiState.longestStreakHistory.map { it.toFloat() },
                    lineColor = Color(0xFF2196F3),
                    fillColor = Color(0x332196F3),
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }
            item {
                var expanded by remember { mutableStateOf(true) }
                StatCard(
                    title = "Completion Rate",
                    value = "${uiState.completionRate}%",
                    icon = Icons.Default.KeyboardArrowUp,
                    color = MaterialTheme.colors.secondary,
                    chartValues = uiState.completionRateHistory.map { it.toFloat() },
                    lineColor = Color(0xFFFF9800),
                    fillColor = Color(0x33FF9800),
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                    chartType = ChartType.COMPLETION_RATE
                )
            }
            item {
                var expanded by remember { mutableStateOf(true) }
                StatCard(
                    title = "Active Habits",
                    value = uiState.activeHabits.toString(),
                    icon = Icons.Default.Refresh,
                    color = MaterialTheme.colors.primaryVariant,
                    chartValues = uiState.activeHabitsHistory.map { it.toFloat() },
                    lineColor = Color(0xFF00BCD4),
                    fillColor = Color(0x3300BCD4),
                    expanded = expanded,
                    onClick = { expanded = !expanded }
                )
            }
        }
    }
}

/**
 * Chart type enum to determine Y-axis scaling behavior.
 * 
 * - TASKS_COMPLETED: Integer scale (0, 1, 2, ...) for daily task counts
 * - COMPLETION_RATE: Percentage scale (0-100%) for completion rates
 * - OTHER: Auto-scaled based on data range with padding
 */
enum class ChartType {
    TASKS_COMPLETED,
    COMPLETION_RATE,
    OTHER
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    chartValues: List<Float>? = null,
    lineColor: Color = Color.Blue,
    fillColor: Color = Color(0x332196F3),
    expanded: Boolean = true,
    onClick: () -> Unit = {},
    chartType: ChartType = ChartType.OTHER
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.h5,
                        color = color
                    )
                }
            }
            if (chartValues != null) {
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    SimpleLineAreaChart(
                        values = chartValues,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        lineColor = lineColor,
                        fillColor = fillColor,
                        chartType = chartType
                    )
                }
            }
        }
    }
}

/**
 * Custom line area chart component with X and Y axes.
 * 
 * This chart renders:
 * - Y-axis (left): Shows value scale (task counts or percentages)
 * - X-axis (bottom): Shows time scale (days)
 * - Grid lines: Horizontal lines for easier value reading
 * - Data line: Connects data points with rounded caps
 * - Area fill: Semi-transparent fill under the line
 * - Data points: Small circles at each data point
 * 
 * @param values List of Float values to plot
 * @param chartType Determines Y-axis scale:
 *   - TASKS_COMPLETED: Integer scale (0, 1, 2, ...)
 *   - COMPLETION_RATE: Percentage scale (0-100%)
 *   - OTHER: Auto-scaled based on data range
 */
@Composable
fun SimpleLineAreaChart(
    values: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Blue,
    fillColor: Color = Color(0x332196F3),
    chartType: ChartType = ChartType.OTHER
) {
    val density = LocalDensity.current
    
    // Calculate chart dimensions, padding, and axis ranges outside Canvas
    // This ensures Text composables can access the same layout information
    val chartInfo = remember(values, chartType, density) {
        if (values.isEmpty()) return@remember null
        
        val yAxisLabelWidth = with(density) { 30.dp.toPx() }
        val xAxisLabelHeight = with(density) { 20.dp.toPx() }
        val paddingTop = with(density) { 10.dp.toPx() }
        val paddingBottom = xAxisLabelHeight + with(density) { 10.dp.toPx() }
        val paddingLeft = yAxisLabelWidth + with(density) { 10.dp.toPx() }
        val paddingRight = with(density) { 10.dp.toPx() }
        
        val maxVal = values.maxOrNull() ?: 1f
        val minVal = values.minOrNull() ?: 0f
        val rawRange = (maxVal - minVal)
        
        // Determine Y-axis scale based on chart type
        // Tasks Completed: Integer scale from 0 to max (rounded up)
        // Completion Rate: Fixed 0-100% scale
        // Other: Auto-scaled with padding for visibility
        val (finalMax, finalMin, finalRange) = if (chartType == ChartType.TASKS_COMPLETED) {
            val roundedMax = maxVal.let { 
                val intVal = if (it % 1f == 0f) it.toInt() else it.toInt() + 1
                intVal.toFloat().coerceAtLeast(1f)
            }
            Triple(roundedMax, 0f, roundedMax)
        } else if (chartType == ChartType.COMPLETION_RATE) {
            Triple(100f, 0f, 100f)
        } else {
            if (rawRange == 0f) {
                val centerVal = maxVal
                val padding = maxOf(centerVal * 0.3f, 2f)
                Triple(centerVal + padding, centerVal - padding, padding * 2f)
            } else {
                val padding = rawRange * 0.15f
                Triple(maxVal + padding, minVal - padding, rawRange + padding * 2f)
            }
        }
        
        val yLabelCount = if (chartType == ChartType.TASKS_COMPLETED) {
            finalMax.toInt().coerceAtMost(5) + 1
        } else if (chartType == ChartType.COMPLETION_RATE) {
            5
        } else {
            5
        }
        
        ChartInfo(
            paddingTop = paddingTop,
            paddingBottom = paddingBottom,
            paddingLeft = paddingLeft,
            paddingRight = paddingRight,
            finalMax = finalMax,
            finalMin = finalMin,
            finalRange = finalRange,
            yLabelCount = yLabelCount
        )
    }
    
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (values.isEmpty() || chartInfo == null) return@Canvas
            
            val chartWidth = size.width - chartInfo.paddingLeft - chartInfo.paddingRight
            val chartHeight = size.height - chartInfo.paddingTop - chartInfo.paddingBottom
            
            // Draw Y-axis (vertical line on left side)
            val yAxisX = chartInfo.paddingLeft
            drawLine(
                color = Color.Gray,
                start = Offset(yAxisX, chartInfo.paddingTop),
                end = Offset(yAxisX, chartInfo.paddingTop + chartHeight),
                strokeWidth = 2f
            )
            
            // Draw X-axis (horizontal line at bottom)
            val xAxisY = chartInfo.paddingTop + chartHeight
            drawLine(
                color = Color.Gray,
                start = Offset(chartInfo.paddingLeft, xAxisY),
                end = Offset(chartInfo.paddingLeft + chartWidth, xAxisY),
                strokeWidth = 2f
            )
            
            // Draw Y-axis grid lines (horizontal lines for easier value reading)
            for (i in 0 until chartInfo.yLabelCount) {
                val yPos = chartInfo.paddingTop + chartHeight - (i / (chartInfo.yLabelCount - 1).toFloat()) * chartHeight
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    start = Offset(chartInfo.paddingLeft, yPos),
                    end = Offset(chartInfo.paddingLeft + chartWidth, yPos),
                    strokeWidth = 1f
                )
            }
            
            // Calculate data points
            val stepX = chartWidth / (values.size - 1).coerceAtLeast(1)
            val points = values.mapIndexed { i, v ->
                val normalized = ((v - chartInfo.finalMin) / chartInfo.finalRange).coerceIn(0f, 1f)
                Offset(
                    chartInfo.paddingLeft + i * stepX,
                    chartInfo.paddingTop + chartHeight - (normalized * chartHeight)
                )
            }
            
            // Area fill
            val areaPath = Path().apply {
                moveTo(points.first().x, chartInfo.paddingTop + chartHeight)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, chartInfo.paddingTop + chartHeight)
                close()
            }
            drawPath(areaPath, fillColor, style = Fill)
            
            // Line with rounded caps
            if (points.size > 1) {
                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    linePath,
                    lineColor,
                    style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                
                // Draw small circles at data points for better visibility
                points.forEach { point ->
                    drawCircle(
                        color = lineColor,
                        radius = 3f,
                        center = point
                    )
                }
            }
        }
        
        // Draw axis labels using Text composables positioned over the Canvas
        // Using Text composables instead of native canvas text for better Compose integration
        chartInfo?.let { info ->
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val chartWidthPx = with(density) { maxWidth.toPx() }
                val chartHeightPx = with(density) { maxHeight.toPx() }
                val actualChartWidth = chartWidthPx - info.paddingLeft - info.paddingRight
                val actualChartHeight = chartHeightPx - info.paddingTop - info.paddingBottom
                
                // Y-axis labels: Show value scale (task counts or percentages)
                for (i in 0 until info.yLabelCount) {
                    val labelValue = info.finalMin + (info.finalMax - info.finalMin) * (i / (info.yLabelCount - 1).toFloat())
                    val yPos = info.paddingTop + actualChartHeight - (i / (info.yLabelCount - 1).toFloat()) * actualChartHeight
                    
                    val labelText = when (chartType) {
                        ChartType.TASKS_COMPLETED -> labelValue.toInt().toString()
                        ChartType.COMPLETION_RATE -> "${labelValue.toInt()}%"
                        else -> String.format("%.1f", labelValue)
                    }
                    
                    Text(
                        text = labelText,
                        style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                        textAlign = TextAlign.Right,
                        modifier = Modifier
                            .offset(
                                x = 0.dp,
                                y = with(density) { 
                                    val offsetPx = 5.dp.toPx()
                                    Dp((yPos - offsetPx) / density.density)
                                }
                            )
                            .width(with(density) { 
                                val offsetPx = 5.dp.toPx()
                                Dp((info.paddingLeft - offsetPx) / density.density)
                            })
                    )
                }
                
                // X-axis labels: Show day labels (D1, D2, D3, etc.)
                val stepX = actualChartWidth / (values.size - 1).coerceAtLeast(1)
                for (i in values.indices) {
                    val xPos = info.paddingLeft + i * stepX
                    val dayLabel = "D${i + 1}" // Day label format
                    
                    Text(
                        text = dayLabel,
                        style = TextStyle(fontSize = 10.sp, color = Color.Gray),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset(
                                x = with(density) { 
                                    val offsetPx = 10.dp.toPx()
                                    Dp((xPos - offsetPx) / density.density)
                                },
                                y = with(density) { 
                                    val offsetPx = 5.dp.toPx()
                                    Dp((info.paddingTop + actualChartHeight + offsetPx) / density.density)
                                }
                            )
                            .width(20.dp)
                    )
                }
            }
        }
    }
}
private data class ChartInfo(
    val paddingTop: Float,
    val paddingBottom: Float,
    val paddingLeft: Float,
    val paddingRight: Float,
    val finalMax: Float,
    val finalMin: Float,
    val finalRange: Float,
    val yLabelCount: Int
)
