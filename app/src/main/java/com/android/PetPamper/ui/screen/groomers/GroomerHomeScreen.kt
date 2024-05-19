package com.android.PetPamper.ui.screen.groomers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.PetPamper.model.GroomerViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar
import java.util.Locale

@Composable
fun GroomerHome(email: String) {
  val groomerViewModel = remember { mutableStateOf(GroomerViewModel(email)) }
  val selectedDate = remember { mutableStateOf(Calendar.getInstance()) }
  // Map to store selected hours for multiple dates
  val selectedHoursMap = remember { mutableStateMapOf<String, MutableList<Int>>() }

  Column(modifier = Modifier.padding(16.dp)) {
    MonthYearPicker(selectedDate) { date -> selectedDate.value = date }
    DayPicker(selectedDate) { date -> selectedDate.value = date }
    HourPicker(selectedDate.value, selectedHoursMap)
    Spacer(modifier = Modifier.height(16.dp))
    ConfirmButton(groomerViewModel.value, selectedHoursMap) { println("Hours saved successfully") }
  }
}

@Composable
fun MonthYearPicker(selectedDate: MutableState<Calendar>, onDateChange: (Calendar) -> Unit) {
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
            "${selectedDate.value.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())} ${selectedDate.value.get(Calendar.YEAR)}",
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
fun DayPicker(selectedDate: MutableState<Calendar>, onDateSelected: (Calendar) -> Unit) {
  val today = Calendar.getInstance()
  val daysInMonth = getDaysInMonth(selectedDate.value)
  LazyVerticalGrid(columns = GridCells.Fixed(7), contentPadding = PaddingValues(vertical = 8.dp)) {
    items(daysInMonth) { day ->
      val dayDate =
          Calendar.getInstance().apply {
            timeInMillis = selectedDate.value.timeInMillis
            set(Calendar.DAY_OF_MONTH, day)
          }
      // Check if the day is before today
      val isPast =
          dayDate.before(today) &&
              dayDate.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)

      Box(
          contentAlignment = Alignment.Center,
          modifier =
              Modifier.aspectRatio(1f)
                  .clip(RoundedCornerShape(50))
                  .background(
                      if (day == selectedDate.value.get(Calendar.DAY_OF_MONTH)) Color(0xFF4CAF50)
                      else if (!isPast) Color.Transparent else Color.LightGray)
                  .then(
                      if (!isPast)
                          Modifier.clickable {
                            selectedDate.value = dayDate
                            onDateSelected(dayDate)
                          }
                      else Modifier)) {
            Text(
                day.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = if (isPast) Color.Gray else Color.Black)
          }
    }
  }
}

fun getDaysInMonth(calendar: Calendar): List<Int> {
  val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
  return (1..maxDay).toList()
}

@Composable
fun HourPicker(selectedDate: Calendar, selectedHoursMap: MutableMap<String, MutableList<Int>>) {
  val dateKey = selectedDateToString(selectedDate)
  // Ensure we create a new mutable list if it doesn't exist, but don't mutate the state map yet
  val hoursForDate = selectedHoursMap[dateKey] ?: mutableListOf()

  Text(
      "Select Hour",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(8.dp))
  val hours = (9..17).toList() // Hours from 9 AM to 5 PM

  LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      contentPadding = PaddingValues(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(hours) { hour ->
          val isSelected = hoursForDate.contains(hour)
          HourBox(hour, isSelected) {
            if (isSelected) {
              // Directly mutate and create a new list for the map
              selectedHoursMap[dateKey] = hoursForDate.filter { it != hour }.toMutableList()
            } else {
              // Add hour and create a new list for the map
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
          Modifier.fillMaxWidth()
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

@Composable
fun ConfirmButton(
    groomerViewModel: GroomerViewModel,
    selectedHoursMap: Map<String, List<Int>>,
    onConfirm: () -> Unit
) {
  Button(
      onClick = { groomerViewModel.saveHoursToFirebase(selectedHoursMap) { onConfirm() } },
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) {
        Text("Save Available Hours", style = MaterialTheme.typography.bodyLarge)
      }
}

@Preview(showBackground = true)
@Composable
fun PreviewGroomerHome() {

  // Mock the behavior as if the GroomerHome is fetching this data
  GroomerHome(email = "alilou12@example.com")
}
