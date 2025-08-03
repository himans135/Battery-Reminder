package com.example.batteryreminder

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.batteryreminder.ui.theme.BatteryReminderTheme
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_SMS_PERMISSION = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Dynamically register BatteryReceiver here
        val batteryReceiver = BatteryReceiver()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryReceiver, filter)

        enableEdgeToEdge()
        setContent {
            BatteryReminderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ReminderScreen()
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.SEND_SMS
                ),
                REQUEST_SMS_PERMISSION
            )
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
@Composable
fun ReminderScreen() {
    val context = LocalContext.current
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var calendar = remember { Calendar.getInstance()}

    // Dialog control states
    var showDialog by remember { mutableStateOf(false) }
    var scheduledMessage by remember { mutableStateOf("") }
    var scheduledTime by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    var selectedDate by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var selectedTime by remember { mutableStateOf(timeFormat.format(calendar.time)) }

    // SMS toggle and phone number
    var sendSMS by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color(0xFFF4F4F4))   // Light Grey background
            .background(Color.Black)   // Light Grey background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())  // Adds top padding below system bar
        ) {
            // Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
//                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = "Battery Reminder",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))  // Add spacing before heading

            // Main Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    text = "Set a Reminder",
                    style = MaterialTheme.typography.headlineMedium
                )
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Reminder Message") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Send SMS")
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(checked = sendSMS, onCheckedChange = { sendSMS = it })
                }
                if (sendSMS) {
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Spacer(modifier = Modifier.height(16.dp))

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
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick Date: $selectedDate")
                }

//                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val timePicker = TimePickerDialog(context, { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
                    timePicker.show()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Pick Time: $selectedTime")
                }

                //Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = @androidx.annotation.RequiresPermission(android.Manifest.permission.SCHEDULE_EXACT_ALARM) {
                    val trimmedMessage = message.text.trim()
                    val number = phoneNumber.text.trim()

                    if (trimmedMessage.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Please enter a reminder message",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (sendSMS && number.length < 10) {
                        Toast.makeText(
                            context,
                            "Please enter a valid phone number",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d(
                            "ReminderManager",
                            "Scheduling alarm for ${calendar.time} with message: '${message}'"
                        )
                        Log.d("ReminderManager", "Time millis: ${calendar.timeInMillis}")

                        scheduledMessage = trimmedMessage
                        scheduledTime = calendar.time.toString()
                        showDialog = true
                        ReminderManager.scheduleReminder(
                            context,
                            calendar,
                            trimmedMessage,
                            if (sendSMS) number else null
                        )

                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Schedule Reminder ")
                }
            }
        }
        // Show Dialog
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Reminder Scheduled") },
                text = {
                    Text("Reminder set for:\n$scheduledTime\n\nMessage:\n$scheduledMessage")
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
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