package com.example.batteryreminder

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.batteryreminder.ui.theme.BatteryReminderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatteryReminderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ReminderScreen()
                }
            }
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun ReminderScreen() {
    val context = LocalContext.current
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var calendar = remember { Calendar.getInstance()}
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Reminder Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val datePicker = android.app.DatePickerDialog(
                context,
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        },modifier = Modifier.fillMaxWidth()) {
            Text("Pick Date")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val timePicker = TimePickerDialog(context, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
            timePicker.show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Pick Time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
            Log.d("ReminderManager", "Scheduling alarm for ${calendar.time} with message: $message")
            Log.d("ReminderManager", "Time millis: ${calendar.timeInMillis}")
            ReminderManager.scheduleReminder(context, calendar, message.text)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Schedule Reminder")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    BatteryReminderTheme {
        ReminderScreen()
    }
}