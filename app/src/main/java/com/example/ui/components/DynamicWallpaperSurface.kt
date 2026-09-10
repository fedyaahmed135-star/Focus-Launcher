package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.LauncherSettingsRepository
import com.example.data.WallpaperPresets
import com.example.data.WallpaperType

/**
 * Dynamic Wallpaper Container that renders the active wallpaper
 * (Misty Mountain Minimalist as default, Classic Mountain, custom gallery photo, or AMOLED black)
 * with smooth dimming and high contrast.
 */
@Composable
fun DynamicWallpaperSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settingsRepo = remember { LauncherSettingsRepository.getInstance(context) }

    val activeWallpaperId by settingsRepo.activeWallpaperId.collectAsStateWithLifecycle()
    val customUriString by settingsRepo.customWallpaperUri.collectAsStateWithLifecycle()
    val wallpaperDim by settingsRepo.wallpaperDim.collectAsStateWithLifecycle()
    val isOledBlack by settingsRepo.oledPureBlack.collectAsStateWithLifecycle()

    val currentPreset = remember(activeWallpaperId) {
        WallpaperPresets.getById(activeWallpaperId)
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        if (!isOledBlack && activeWallpaperId != WallpaperPresets.AMOLED_PURE_BLACK.id) {
            when {
                // User custom picked wallpaper from phone gallery
                activeWallpaperId == "custom_photo" && customUriString != null -> {
                    AsyncImage(
                        model = Uri.parse(customUriString),
                        contentDescription = "Custom Wallpaper",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = wallpaperDim
                    )
                }
                // Preset drawables (Default: Misty Mountains Minimalist)
                currentPreset.type == WallpaperType.PRESET_DRAWABLE && currentPreset.drawableRes != null -> {
                    Image(
                        painter = painterResource(id = currentPreset.drawableRes),
                        contentDescription = currentPreset.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        alpha = wallpaperDim
                    )
                }
            }

            // High-contrast subtle overlay to guarantee clock and text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        }

        // Actual foreground content
        content()
    }
}
