package com.fedi.focuslauncher.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fedi.focuslauncher.R
import com.fedi.focuslauncher.data.LauncherSettingsRepository
import com.fedi.focuslauncher.ui.components.DynamicWallpaperSurface
import com.fedi.focuslauncher.ui.components.MinimalistClockWidget
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsRepo = remember { LauncherSettingsRepository.getInstance(context) }

    val isOledBlack by settingsRepo.oledPureBlack.collectAsStateWithLifecycle()
    val isFocusActive by settingsRepo.focusModeActive.collectAsStateWithLifecycle()

    // Real Android Launcher: Back button on Home screen never exits the launcher
    BackHandler(enabled = true) {
        // No-op: stays on Home
    }

    DynamicWallpaperSurface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // Swiping upwards from anywhere on the home screen smoothly opens the App Drawer
                    if (dragAmount < -18f) {
                        navController.navigate("app_drawer")
                    }
                }
            }
    ) {
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Top Bar: Focus indicator & Settings shortcut
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isFocusActive) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E2A1E),
                        modifier = Modifier.clickable { navController.navigate("focus_mode") }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Fokus aktiv",
                                fontSize = 12.sp,
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { navController.navigate("wallpaper_settings") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Wallpaper,
                            contentDescription = "Divar Kağızı",
                            tint = Color.LightGray.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { navController.navigate("settings") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Parametrlər",
                            tint = Color.LightGray.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Real-time Minimalist Clock Widget
            MinimalistClockWidget(
                isLarge = true,
                showBattery = true,
                modifier = Modifier.padding(top = 40.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            
            // App Drawer Swipe Indicator
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("app_drawer") }
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Tətbiqləri aç",
                    tint = Color.LightGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Yuxarı çək",
                    fontSize = 11.sp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )
            }

            // Bottom Dock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DockIcon(
                    icon = Icons.Filled.Phone,
                    contentDescription = "Zənglər",
                    onClick = { openDialer(context) }
                )
                DockIcon(
                    icon = Icons.Filled.ChatBubble,
                    contentDescription = "Mesajlar",
                    onClick = { openMessages(context) }
                )
                DockIcon(
                    icon = Icons.Filled.Apps,
                    contentDescription = "Tətbiqlər",
                    onClick = { navController.navigate("app_drawer") }
                )
                DockIcon(
                    icon = Icons.Filled.CameraAlt,
                    contentDescription = "Kamera",
                    onClick = { openCamera(context) }
                )
            }
        }
    }
}


@Composable
fun DockIcon(icon: ImageVector, contentDescription: String = "", onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

private fun openDialer(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Zəng tətbiqi tapılmadı", Toast.LENGTH_SHORT).show()
    }
}

private fun openMessages(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MESSAGING)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallback = Intent(Intent.ACTION_VIEW).apply {
                type = "vnd.android-dir/mms-sms"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (e2: Exception) {
            Toast.makeText(context, "Mesaj tətbiqi tapılmadı", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun openCamera(context: Context) {
    try {
        val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Kamera tətbiqi tapılmadı", Toast.LENGTH_SHORT).show()
    }
}

