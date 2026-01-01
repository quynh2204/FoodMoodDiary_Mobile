package com.haphuongquynh.foodmooddiary.presentation.screens.statistics

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    onEntryClick: (String) -> Unit = {} // Entry ID
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

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

                Spacer(Modifier.height(12.dp))

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
                                onClick = { selectedDate = date }
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

        LegendItem(
            color = PastelGreen,
            label = "Ngày hôm nay"
        )

        LegendItem(
            color = GoldPrimary,
            label = "Có bữa ăn ghi nhận",
            border = true
        )
    }
}

Spacer(Modifier.height(24.dp))


        /* ================= SELECTED DATE ================= */
        selectedDate?.let { date ->
            val dayData = entriesByDate[date]

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = BlackSecondary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text(
                        text = "${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, Locale("vi"))} ${date.year}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhiteText
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        DayStat(Icons.Default.Restaurant, dayData?.entryCount?.toString() ?: "--", "Bữa ăn")
                        DayStat(Icons.Default.SentimentSatisfiedAlt,
                            dayData?.averageMoodScore?.let { String.format("%.1f", it) } ?: "--",
                            "Tâm trạng"
                        )
                        DayStat(Icons.Default.Photo, dayData?.entryCount?.toString() ?: "--", "Ảnh")
                    }

                    /* ================= MEAL PREVIEW ================= */
                    if ((dayData?.entryCount ?: 0) > 0) {
                        Spacer(Modifier.height(20.dp))
                        Divider(color = BlackTertiary)
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Bữa ăn trong ngày",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhiteText
                        )

                        Spacer(Modifier.height(12.dp))

                        LazyColumn(
                            modifier = Modifier.heightIn(max = 280.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val entriesToShow = dayData!!.entries.take(3)
                            items(entriesToShow.size) { index ->
                                val entry = entriesToShow[index]
                                MealPreviewItemWithPhoto(
                                    entry = entry,
                                    onClick = { onEntryClick(entry.id) }
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        TextButton(
                            onClick = { onViewAllMeals(date) },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Xem tất cả →", color = PastelGreen)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

/* ================= PREVIEW ITEM ================= */

@Composable
fun MealPreviewItemWithPhoto(
    entry: DayEntry,
    onClick: () -> Unit = {}
) {
    val photoPath = entry.localPhotoPath ?: entry.photoUrl

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = BlackTertiary),
        shape = RoundedCornerShape(12.dp)
    ) {
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
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Composable
fun MealPreviewItem(
    title: String,
    subtitle: String,
    moodScore: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BlackTertiary),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Restaurant,
                null,
                tint = PastelGreen,
                modifier = Modifier.size(28.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, color = WhiteText)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }

            Text(
                text = "$moodScore/10",
                fontWeight = FontWeight.Bold,
                color = PastelGreen
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
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                when {
                    isToday -> PastelGreen.copy(alpha = 0.3f)
                    isSelected -> PastelGreen.copy(alpha = 0.15f)
                    else -> Color.Transparent
                }
            )
            .border(
                if (hasMeals) 1.dp else 0.dp,
                if (hasMeals) GoldPrimary else Color.Transparent,
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                fontSize = 14.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isToday) PastelGreen else WhiteText
            )
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
        Icon(icon, null, tint = PastelGreen, modifier = Modifier.size(28.dp))
        Spacer(Modifier.height(6.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WhiteText)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    border: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (border)
                        Modifier.border(1.dp, color, CircleShape)
                    else Modifier
                )
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            color = WhiteText
        )
    }
}
