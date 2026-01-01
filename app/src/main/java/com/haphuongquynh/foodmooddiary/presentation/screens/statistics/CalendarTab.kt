package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.haphuongquynh.foodmooddiary.domain.model.DayEntry
import com.haphuongquynh.foodmooddiary.domain.model.MoodTrendPoint
import com.haphuongquynh.foodmooddiary.ui.theme.*
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarTab(
    moodTrend: List<MoodTrendPoint>,
    onViewAllMeals: (LocalDate) -> Unit = {},
    onEntryClick: (String) -> Unit = {}
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showAllMeals by remember { mutableStateOf(false) }
    var expandedEntryId by remember { mutableStateOf<String?>(null) }

    val entriesByDate = remember(moodTrend) {
        moodTrend.associateBy { point ->
            Instant.ofEpochMilli(point.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        /* ================= MONTH NAV ================= */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ChevronLeft, null, tint = PastelGreen)
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("vi"))} ${currentMonth.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ChevronRight, null, tint = PastelGreen)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /* ================= CALENDAR GRID ================= */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(16.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("T2","T3","T4","T5","T6","T7","CN").forEach {
                        Text(
                            text = it,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                val daysInMonth = currentMonth.lengthOfMonth()
                val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek.value
                val totalCells =
                    if (daysInMonth + firstDayOfMonth - 1 <= 35) 35 else 42

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items((1..totalCells).toList()) { index ->
                        val dayNumber = index - firstDayOfMonth + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val isToday = date == LocalDate.now()
                            val isSelected = date == selectedDate
                            val dayData = entriesByDate[date]

                            CalendarDayCell(
                                day = dayNumber,
                                isToday = isToday,
                                isSelected = isSelected,
                                hasMeals = (dayData?.entryCount ?: 0) > 0,
                                mealCount = dayData?.entryCount ?: 0,
                                onClick = {
                                    selectedDate = if (selectedDate == date) null else date
                                    showAllMeals = false
                                    expandedEntryId = null
                                }
                            )
                        } else {
                            Box(Modifier.size(44.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ================= LEGEND ================= */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = BlackSecondary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = "Chú thích",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhiteText
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    LegendItem(
                        color = PastelGreen,
                        label = "Hôm nay"
                    )

                    LegendItem(
                        color = GoldPrimary,
                        label = "Có bữa ăn",
                        border = true
                    )

                    LegendItem(
                        color = PastelGreen.copy(alpha = 0.3f),
                        label = "Đang chọn"
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        /* ================= SELECTED DATE DETAIL ================= */
        AnimatedVisibility(
            visible = selectedDate != null,
            enter = fadeIn(tween(300)) + expandVertically(tween(300)),
            exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
        ) {
            selectedDate?.let { date ->
                val dayData = entriesByDate[date]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BlackSecondary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {

                        // Date header with close button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = PastelGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, Locale("vi"))} ${date.year}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhiteText
                                )
                            }

                            IconButton(
                                onClick = { selectedDate = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Đóng",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlackTertiary)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            DayStat(
                                icon = Icons.Default.Restaurant,
                                value = dayData?.entryCount?.toString() ?: "0",
                                label = "Bữa ăn"
                            )
                            DayStat(
                                icon = Icons.Default.SentimentSatisfiedAlt,
                                value = dayData?.averageMoodScore?.let { String.format("%.1f", it) } ?: "--",
                                label = "Điểm TB"
                            )
                        }

                        /* ================= MEALS LIST ================= */
                        if ((dayData?.entryCount ?: 0) > 0) {
                            Spacer(Modifier.height(20.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Bữa ăn trong ngày",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhiteText
                                )

                                if (dayData!!.entries.size > 3) {
                                    TextButton(
                                        onClick = { showAllMeals = !showAllMeals }
                                    ) {
                                        Text(
                                            text = if (showAllMeals) "Thu gọn" else "Xem tất cả (${dayData.entries.size})",
                                            color = PastelGreen,
                                            fontSize = 13.sp
                                        )
                                        Icon(
                                            imageVector = if (showAllMeals) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = PastelGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(12.dp))

                            val entriesToShow = if (showAllMeals) dayData!!.entries else dayData!!.entries.take(3)

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                entriesToShow.forEach { entry ->
                                    ExpandableMealCard(
                                        entry = entry,
                                        isExpanded = expandedEntryId == entry.id,
                                        onToggleExpand = {
                                            expandedEntryId = if (expandedEntryId == entry.id) null else entry.id
                                        }
                                    )
                                }
                            }
                        } else {
                            // No meals message
                            Spacer(Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BlackTertiary)
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.RestaurantMenu,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "Chưa có bữa ăn nào",
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Prompt to select date if none selected
        if (selectedDate == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlackSecondary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = PastelGreen.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Chọn một ngày trên lịch",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "để xem chi tiết bữa ăn",
                            color = Color.Gray.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

/* ================= EXPANDABLE MEAL CARD ================= */

@Composable
fun ExpandableMealCard(
    entry: DayEntry,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val photoPath = entry.localPhotoPath ?: entry.photoUrl
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )

    Card(
        onClick = onToggleExpand,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackTertiary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Main row (always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo or placeholder
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF2C2C2E)),
                    contentAlignment = Alignment.Center
                ) {
                    if (photoPath != null) {
                        AsyncImage(
                            model = if (photoPath.startsWith("/")) File(photoPath) else photoPath,
                            contentDescription = entry.foodName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Restaurant,
                            null,
                            tint = PastelGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.foodName,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(entry.timestamp)),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Mood emoji
                entry.mood?.let { mood ->
                    Text(
                        text = mood,
                        fontSize = 28.sp
                    )
                }

                Spacer(Modifier.width(8.dp))

                // Expand indicator
                Icon(
                    Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Thu gọn" else "Mở rộng",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }

            // Expanded content
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1A1A1A))
                        .padding(16.dp)
                ) {
                    // Large photo if available
                    if (photoPath != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            AsyncImage(
                                model = if (photoPath.startsWith("/")) File(photoPath) else photoPath,
                                contentDescription = entry.foodName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    // Details grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailChip(
                            icon = Icons.Default.Restaurant,
                            label = entry.foodName,
                            color = PastelGreen
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DetailChip(
                            icon = Icons.Default.AccessTime,
                            label = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                .format(java.util.Date(entry.timestamp)),
                            color = Color(0xFF64B5F6)
                        )

                        entry.mood?.let { mood ->
                            DetailChip(
                                icon = Icons.Default.EmojiEmotions,
                                label = mood,
                                color = Color(0xFFFFD93D)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = label,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ================= COMPONENTS ================= */

@Composable
fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasMeals: Boolean,
    mealCount: Int,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> PastelGreen.copy(alpha = 0.4f)
                    isToday -> PastelGreen.copy(alpha = 0.2f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (hasMeals && !isSelected) {
                    Modifier.border(2.dp, GoldPrimary, CircleShape)
                } else if (isSelected) {
                    Modifier.border(2.dp, PastelGreen, CircleShape)
                } else {
                    Modifier
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = PastelGreen),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = when {
                    isSelected -> FontWeight.Bold
                    isToday -> FontWeight.Bold
                    else -> FontWeight.Normal
                },
                color = when {
                    isSelected -> WhiteText
                    isToday -> PastelGreen
                    else -> WhiteText
                }
            )

            // Meal count indicator
            if (hasMeals && mealCount > 0) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) WhiteText else GoldPrimary)
                )
            }
        }
    }
}

@Composable
fun DayStat(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = PastelGreen, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WhiteText)
        Text(label, fontSize = 11.sp, color = Color.Gray)
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    border: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(if (border) Color.Transparent else color)
                .then(
                    if (border)
                        Modifier.border(2.dp, color, CircleShape)
                    else Modifier
                )
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}
