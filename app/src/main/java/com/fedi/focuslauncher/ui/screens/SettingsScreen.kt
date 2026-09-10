package com.fedi.focuslauncher.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fedi.focuslauncher.data.LauncherSettingsRepository

private data class SettingOption(
    val id: String,
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val isSwitch: Boolean = false,
    val isChecked: Boolean = false,
    val onToggle: ((Boolean) -> Unit)? = null,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsRepo = remember { LauncherSettingsRepository.getInstance(context) }
    val isMonochrome by settingsRepo.monochromeIcons.collectAsStateWithLifecycle()
    val isOledBlack by settingsRepo.oledPureBlack.collectAsStateWithLifecycle()
    val activeWallpaperId by settingsRepo.activeWallpaperId.collectAsStateWithLifecycle()
    val isFocusActive by settingsRepo.focusModeActive.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }

    // Android Launcher navigation: back returns to Home
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    val options = listOf(
        SettingOption(
            id = "wallpaper",
            icon = Icons.Filled.Wallpaper,
            title = "Divar Kağızı (Wallpaper)",
            subtitle = if (isOledBlack) "AMOLED Qara rejim aktivdir" else "Dumanlı dağlar (minimalist) və dinamik mövzular",
            onClick = { navController.navigate("wallpaper_settings") }
        ),
        SettingOption(
            id = "monochrome",
            icon = Icons.Filled.Palette,
            title = "Ağ-qara tətbiq ikonları",
            subtitle = if (isMonochrome) "Aktivdir (Bütün tətbiqlər monoxromdur)" else "Deaktivdir (Orijinal rənglər)",
            isSwitch = true,
            isChecked = isMonochrome,
            onToggle = { settingsRepo.setMonochromeIcons(it) },
            onClick = { settingsRepo.setMonochromeIcons(!isMonochrome) }
        ),
        SettingOption(
            id = "oled_black",
            icon = Icons.Filled.DarkMode,
            title = "AMOLED təmiz qara rejim",
            subtitle = if (isOledBlack) "Təmiz qara fon (Minimalist & Batareyaya qənaət)" else "Minimalist divar kağızı aktivdir",
            isSwitch = true,
            isChecked = isOledBlack,
            onToggle = { settingsRepo.setOledPureBlack(it) },
            onClick = { settingsRepo.setOledPureBlack(!isOledBlack) }
        ),
        SettingOption(
            id = "default_launcher",
            icon = Icons.Filled.Home,
            title = "Varsayılan Launcher kimi təyin et",
            subtitle = "Focus Launcher-i telefonun əsas ana ekranı et",
            onClick = { openHomeSettings(context) }
        ),
        SettingOption(
            id = "focus_mode",
            icon = Icons.Filled.TrackChanges,
            title = "Fokus rejimi",
            subtitle = if (isFocusActive) "Fokus rejimi aktivdir • Qaydalar və taymer" else "Pomodoro taymeri və diqqət yayındıranların bloklanması",
            onClick = { navController.navigate("focus_mode") }
        ),
        SettingOption(
            id = "notifications",
            icon = Icons.Filled.Notifications,
            title = "Narahat etməyin və bildirişlər",
            subtitle = "Sistem bildirişlərini və fokus qaydalarını idarə et",
            onClick = { openDndSettings(context) }
        ),
        SettingOption(
            id = "app_management",
            icon = Icons.Filled.Apps,
            title = "Cihaz tətbiq tənzimləmələri",
            subtitle = "Quraşdırılmış tətbiqlər və icazələr",
            onClick = { openAppSettings(context) }
        )
    )

    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) {
            options
        } else {
            options.filter {
                it.title.contains(searchQuery.trim(), ignoreCase = true) ||
                it.subtitle.contains(searchQuery.trim(), ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 20.dp)
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
                text = "Launcher Parametrləri",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Parametrlərdə axtar...", color = Color.Gray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", tint = Color.Gray) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = "Təmizlə", tint = Color.Gray)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = Color(0xFF1A1A1A),
                unfocusedContainerColor = Color(0xFF1A1A1A),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredOptions, key = { it.id }) { option ->
                SettingsRowItem(option)
            }
        }
    }
}

@Composable
private fun SettingsRowItem(option: SettingOption) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = option.onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF1A1A1A),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = option.subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        if (option.isSwitch) {
            Switch(
                checked = option.isChecked,
                onCheckedChange = option.onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color(0xFF2C2C2E)
                )
            )
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Aç",
                tint = Color.Gray
            )
        }
    }
}

private fun openHomeSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (e2: Exception) {
            Toast.makeText(context, "Sistem tənzimləmələri açıla bilmədi", Toast.LENGTH_SHORT).show()
        }
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

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Tətbiq parametrləri açıla bilmədi", Toast.LENGTH_SHORT).show()
    }
}

