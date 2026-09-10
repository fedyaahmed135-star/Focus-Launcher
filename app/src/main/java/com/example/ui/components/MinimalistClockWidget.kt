package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.text.SimpleDateFormat
import java.util.*

/**
 * Minimalist real-time clock widget that provides essential information
 * (live hours/minutes, seconds tick, date, and live battery percentage)
 * with instant launch shortcuts to the system Clock and Calendar apps.
 */
@Composable
fun MinimalistClockWidget(
    modifier: Modifier = Modifier,
    isLarge: Boolean = true,
    showBattery: Boolean = true,
    showSeconds: Boolean = false
) {
    val context = LocalContext.current
    var currentTime by remember { mutableStateOf(Date()) }
    var batteryPercentage by remember { mutableIntStateOf(getBatteryLevel(context)) }
    var isCharging by remember { mutableStateOf(isDeviceCharging(context)) }

    // Real-time ticking engine: updates accurately on every second boundary
    LaunchedEffect(Unit) {
        var tickCounter = 0
        while (isActive) {
            currentTime = Date()
            tickCounter++
            // Refresh battery status every 30 seconds
            if (tickCounter % 30 == 0) {
                batteryPercentage = getBatteryLevel(context)
                isCharging = isDeviceCharging(context)
            }
            val millisUntilNextSecond = 1000L - (System.currentTimeMillis() % 1000L)
            delay(millisUntilNextSecond.coerceAtLeast(100L))
        }
    }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val secondsFormat = remember { SimpleDateFormat("ss", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("d MMMM, EEEE", Locale("az", "AZ")) }

    val formattedTime = timeFormat.format(currentTime)
    val formattedSeconds = secondsFormat.format(currentTime)
    val formattedDate = dateFormat.format(currentTime).replaceFirstChar { it.uppercase() }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        // Clock row with clickable intent to open system Alarm / Clock
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { openSystemClock(context) }
            )
        ) {
            Text(
                text = formattedTime,
                fontSize = if (isLarge) 68.sp else 46.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-1.5).sp,
                color = Color.White
            )

            if (showSeconds) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedSeconds,
                    fontSize = if (isLarge) 18.sp else 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = if (isLarge) 12.dp else 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Essential Information: Date & Battery status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Clickable date that opens the system Calendar
            Text(
                text = formattedDate,
                fontSize = if (isLarge) 15.sp else 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.LightGray,
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { openSystemCalendar(context) }
                )
            )

            if (showBattery && batteryPercentage >= 0) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.08f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (isCharging) Icons.Filled.BatteryChargingFull else Icons.Filled.BatteryFull,
                            contentDescription = "Batareya",
                            tint = if (isCharging) Color(0xFF81C784) else Color.LightGray,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$batteryPercentage%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

private fun getBatteryLevel(context: Context): Int {
    return try {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (level >= 0 && scale > 0) {
            (level * 100) / scale
        } else {
            -1
        }
    } catch (e: Exception) {
        -1
    }
}

private fun isDeviceCharging(context: Context): Boolean {
    return try {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
    } catch (e: Exception) {
        false
    }
}

private fun openSystemClock(context: Context) {
    try {
        val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(clockIntent)
    } catch (e: Exception) {
        try {
            val fallbackIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallbackIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Zəngli saat açıla bilmədi", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openSystemCalendar(context: Context) {
    try {
        val calendarUri = CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
        val calendarIntent = Intent(Intent.ACTION_VIEW, calendarUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(calendarIntent)
    } catch (e: Exception) {
        try {
            val genericIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(genericIntent)
        } catch (e2: Exception) {
            Toast.makeText(context, "Təqvim açıla bilmədi", Toast.LENGTH_SHORT).show()
        }
    }
}
