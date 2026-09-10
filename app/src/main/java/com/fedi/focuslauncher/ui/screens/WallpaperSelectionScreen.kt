package com.fedi.focuslauncher.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.fedi.focuslauncher.data.LauncherSettingsRepository
import com.fedi.focuslauncher.data.WallpaperItem
import com.fedi.focuslauncher.data.WallpaperPresets
import com.fedi.focuslauncher.data.WallpaperType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperSelectionScreen(navController: NavController) {
    val context = LocalContext.current
    val settingsRepo = remember { LauncherSettingsRepository.getInstance(context) }

    val activeWallpaperId by settingsRepo.activeWallpaperId.collectAsStateWithLifecycle()
    val customUriString by settingsRepo.customWallpaperUri.collectAsStateWithLifecycle()
    val wallpaperDim by settingsRepo.wallpaperDim.collectAsStateWithLifecycle()

    // Android Photo Picker launcher (Google Play Policy compliant - 0 permissions required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Take persistable URI permission if available
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            settingsRepo.setCustomWallpaperUri(uri.toString())
            Toast.makeText(context, "Şəxsi divar kağızı təyin edildi", Toast.LENGTH_SHORT).show()
        }
    }

    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        // Top Bar
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
            Column {
                Text(
                    text = "Divar Kağızı (Wallpaper)",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Text(
                    text = "Minimalist dağ teması və dinamik seçimlər",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section: Preset Wallpapers
            item {
                Text(
                    text = "Kataloq və Mövzular",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            items(WallpaperPresets.list, key = { it.id }) { wallpaper ->
                val isSelected = activeWallpaperId == wallpaper.id
                WallpaperCardItem(
                    wallpaper = wallpaper,
                    isSelected = isSelected,
                    onClick = {
                        settingsRepo.setActiveWallpaperId(wallpaper.id)
                        Toast.makeText(context, "${wallpaper.name} aktiv edildi", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Section: Custom Photo Pick
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Qalereyadan Şəxsi Şəkil",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161616),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF242424),
                            modifier = Modifier.size(54.dp)
                        ) {
                            if (customUriString != null) {
                                AsyncImage(
                                    model = Uri.parse(customUriString),
                                    contentDescription = "Seçilmiş Şəkil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.AddPhotoAlternate,
                                        contentDescription = "Şəkil seç",
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (customUriString != null) "Şəxsi Şəkli Dəyişdir" else "Qalereyadan Divar Kağızı Seç",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            Text(
                                text = if (customUriString != null) "Cihazın yaddaşından seçilib" else "Öz minimalist şəklinizi əlavə edin",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        if (activeWallpaperId == "custom_photo") {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Aktivdir",
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Section: Brightness / Dimming Slider
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Qaranlıq Səviyyəsi (Oxunaqlılıq üçün)",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF161616),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Divar kağızı parlaqlığı",
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${(wallpaperDim * 100).toInt()}%",
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Slider(
                            value = wallpaperDim,
                            onValueChange = { settingsRepo.setWallpaperDim(it) },
                            valueRange = 0.2f..0.95f,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = Color.DarkGray
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperCardItem(
    wallpaper: WallpaperItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF161616),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // Thumbnail preview
            Box(
                modifier = Modifier
                    .size(width = 64.dp, height = 80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (wallpaper.type == WallpaperType.AMOLED_BLACK) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                            .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
                    )
                } else if (wallpaper.drawableRes != null) {
                    Image(
                        painter = painterResource(id = wallpaper.drawableRes),
                        contentDescription = wallpaper.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Landscape,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wallpaper.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    if (wallpaper.isDefault) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF2A2A2A)
                        ) {
                            Text(
                                text = "Default",
                                fontSize = 10.sp,
                                color = Color(0xFF81C784),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = wallpaper.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Aktiv",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
