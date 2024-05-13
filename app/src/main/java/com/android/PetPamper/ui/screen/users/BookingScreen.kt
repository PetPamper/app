package com.android.PetPamper.ui.screen.users

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.android.PetPamper.database.FirebaseConnection
import com.android.PetPamper.ui.screen.groomers.ConfirmButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BookingScreen(groomerEmail: String) {
    val dateList = remember { mutableStateListOf<String>() }
    val hoursList = remember { mutableStateListOf<String>() }
    val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
    val selectedHoursMap = remember { mutableStateMapOf<String, MutableList<Int>>() }

    val firebaseConnection = FirebaseConnection()
    LaunchedEffect(groomerEmail) {
        firebaseConnection.fetchAvailableDates(groomerEmail) { fetchedDates ->
            dateList.clear()
            dateList.addAll(fetchedDates)
            Log.d("dayDate1", dateList.toString())
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Select Date", style = MaterialTheme.typography.titleMedium)

        MonthYearPicker(selectedDate) { date ->
            selectedDate.value = date
        }
        DayPicker(selectedDate, dateList) { date ->
            selectedDate.value = date
        }

        LaunchedEffect(selectedDate.value) {
            firebaseConnection.fetchAvailableHours(groomerEmail, selectedDateToString(selectedDate.value)) { fetchedHours ->
                hoursList.clear()
                hoursList.addAll(fetchedHours)
                Log.d("dayDate2", hoursList.toString())
            }
        }

        HourPicker(selectedDate.value, hoursList, selectedHoursMap)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun MonthYearPicker(selectedDate: MutableState<Calendar>,  onDateChange: (Calendar) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = {
                val newDate =
                    (selectedDate.value.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                selectedDate.value = newDate // Update the state
                onDateChange(newDate)
            }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month")
        }
        Text(
            "${selectedDate.value.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${selectedDate.value.get(
                Calendar.YEAR)}",
            style = MaterialTheme.typography.titleMedium)
        IconButton(
            onClick = {
                val newDate =
                    (selectedDate.value.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                selectedDate.value = newDate // Update the state
                onDateChange(newDate)
            }) {
            Icon(Icons.Default.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun DayPicker(
    selectedDate: MutableState<Calendar>,
    dateList: MutableList<String>,
    onDateSelected: (Calendar) -> Unit
) {
    val today = Calendar.getInstance()
    val daysInMonth = getDaysInMonth(selectedDate.value)

    LazyVerticalGrid(columns = GridCells.Fixed(7), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(daysInMonth) { day ->
            val dayDate = Calendar.getInstance().apply {
                timeInMillis = selectedDate.value.timeInMillis
                set(Calendar.DAY_OF_MONTH, day)
            }
            val isPast = dayDate.before(today) && dayDate.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)

//            Log.d( "dayDate", "${selectedDateToString(dayDate)}")
            val isInDayList = dateList.contains(selectedDateToString(dayDate))

            val backgroundColor = when {
                day == selectedDate.value.get(Calendar.DAY_OF_MONTH) -> Color(0xFF4CAF50)  // Selected day color
                !isPast && isInDayList -> Color.Transparent  // Default color for selectable future dates within the list
                else -> Color.LightGray  // Color for past days and days not in the list
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(50))
                    .background(backgroundColor)
                    .then(
                        if (!isPast && isInDayList) Modifier.clickable {  // Only clickable if not past and in the date list
                            selectedDate.value = dayDate
                            onDateSelected(dayDate)
                        } else Modifier
                    )
            ) {
                Text(
                    day.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isPast || !isInDayList) Color.Gray else Color.Black  // Dim color for past days or days not available
                )
            }
        }
    }
}


fun getDaysInMonth(calendar: Calendar): List<Int> {
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    return (1..maxDay).toList()
}

@Composable
fun HourPicker(selectedDate: Calendar, hourList: List<String>, selectedHoursMap: MutableMap<String, MutableList<Int>>) {
    val dateKey = selectedDateToString(selectedDate)
    val hoursForDate = selectedHoursMap[dateKey] ?: mutableListOf()

    Text(
        "Select Hour",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(8.dp)
    )

    // Assuming hourList contains hours in the format "HH:mm" for the selected date
    val availableHours = hourList.mapNotNull {
        it.split(":").firstOrNull()?.toIntOrNull()
    }.distinct()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(availableHours) { hour ->
            val isSelected = hoursForDate.contains(hour)
            HourBox(hour, isSelected) {
                if (isSelected) {
                    selectedHoursMap[dateKey] = hoursForDate.filter { it != hour }.toMutableList()
                } else {
                    selectedHoursMap[dateKey] = (hoursForDate + hour).toMutableList()
                }
            }
        }
    }
}

fun selectedDateToString(calendar: Calendar): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time)

@Composable
fun HourBox(hour: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFF4CAF50) else Color(0xFF2196F3))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp)) {
        Text(
            text = String.format("%02d:00", hour),
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
    }
}