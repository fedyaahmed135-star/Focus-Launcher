package com.fedi.focuslauncher.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fedi.focuslauncher.data.LauncherSettingsRepository
import kotlinx.coroutines.delay

@Composable
fun FocusModeScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsRepo = remember { LauncherSettingsRepository.getInstance(context) }
    val isFocusActive by settingsRepo.focusModeActive.collectAsStateWithLifecycle()
    val focusEndTime by settingsRepo.focusEndTimeMillis.collectAsStateWithLifecycle()
    val savedMinutes by settingsRepo.focusDurationMinutes.collectAsStateWithLifecycle()

    var selectedDuration by remember(savedMinutes) { mutableIntStateOf(savedMinutes) }
    var remainingSeconds by remember { mutableIntStateOf(if (isFocusActive) ((focusEndTime - System.currentTimeMillis()) / 1000L).toInt().coerceAtLeast(0) else savedMinutes * 60) }

    // Android Launcher navigation: Back returns to Home screen
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    // Active Countdown Timer Engine (Global Sync)
    LaunchedEffect(isFocusActive, focusEndTime) {
        if (isFocusActive) {
            while (true) {
                val now = System.currentTimeMillis()
                val rem = ((focusEndTime - now) / 1000L).toInt()
                if (rem <= 0) {
                    remainingSeconds = 0
                    settingsRepo.setFocusModeActive(false)
                    Toast.makeText(context, "Fokus sessiyası uğurla tamamlandı!", Toast.LENGTH_LONG).show()
                    break
                } else {
                    remainingSeconds = rem
                }
                kotlinx.coroutines.delay(1000L)
            }
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Fokus Rejimi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))

            // Master Switch
            Switch(
                checked = isFocusActive,
                onCheckedChange = { active ->
                    if (active) {
                        if (remainingSeconds == 0) remainingSeconds = selectedDuration * 60
                        settingsRepo.setFocusEndTimeMillis(System.currentTimeMillis() + remainingSeconds * 1000L)
                        settingsRepo.setFocusModeActive(true)
                    } else {
                        settingsRepo.setFocusModeActive(false)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF2C2C2E)
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Minimalist Timer Display
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (isFocusActive) Color.White else Color.DarkGray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeFormatted,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isFocusActive) "Fokus aktivdir" else "Dayandırılıb",
                    fontSize = 13.sp,
                    color = if (isFocusActive) Color(0xFF81C784) else Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Timer Duration Selectors (15m, 25m Pomodoro, 45m, 60m)
        Text(
            text = "Müddət seçin",
            fontSize = 14.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(15, 25, 45, 60).forEach { mins ->
                val isSelected = selectedDuration == mins
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color.White else Color(0xFF1A1A1A),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedDuration = mins
                            settingsRepo.setFocusDurationMinutes(mins)
                            if (!isFocusActive) {
                                remainingSeconds = mins * 60
                            }
                        }
                ) {
                    Text(
                        text = "${mins}d",
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons: Start / Pause & Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (isFocusActive) {
                        settingsRepo.setFocusModeActive(false)
                    } else {
                        if (remainingSeconds == 0) {
                            remainingSeconds = selectedDuration * 60
                        }
                        settingsRepo.setFocusEndTimeMillis(System.currentTimeMillis() + remainingSeconds * 1000L)
                        settingsRepo.setFocusModeActive(true)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFocusActive) Color(0xFF2C2C2E) else Color.White,
                    contentColor = if (isFocusActive) Color.White else Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = if (isFocusActive) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFocusActive) "Fasilə ver" else "Fokusa Başla",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            OutlinedButton(
                onClick = {
                    settingsRepo.setFocusModeActive(false)
                    remainingSeconds = selectedDuration * 60
                },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sıfırla",
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Focus Tools Card
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF141414),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Diqqət yayındıranların qarşısını al",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Fokus müddətində zənglər və bildirişləri tənzimləyin",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))

                // DND Settings button
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF1F1F1F),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { openDndSettings(context) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DoNotDisturbOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Narahat Etməyin rejimini aç",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Sistem səviyyəsində bildirişləri kəs",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Motivational Minimalist Quote
        Text(
            text = "“ Sadəlik ən yüksək səviyyəli zəriflikdir. ”",
            fontSize = 13.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }
}

private fun openDndSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SOUND_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (e2: Exception) {
            Toast.makeText(context, "Səs parametrləri açıla bilmədi", Toast.LENGTH_SHORT).show()
        }
    }
}
