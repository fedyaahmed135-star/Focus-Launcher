package com.fedi.focuslauncher.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LauncherSettingsRepository private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("focus_launcher_prefs", Context.MODE_PRIVATE)

    private val _monochromeIcons = MutableStateFlow(prefs.getBoolean(KEY_MONOCHROME_ICONS, true))
    val monochromeIcons: StateFlow<Boolean> = _monochromeIcons.asStateFlow()

    private val _oledPureBlack = MutableStateFlow(prefs.getBoolean(KEY_OLED_BLACK, false))
    val oledPureBlack: StateFlow<Boolean> = _oledPureBlack.asStateFlow()

    private val _activeWallpaperId = MutableStateFlow(
        prefs.getString(KEY_ACTIVE_WALLPAPER_ID, WallpaperPresets.MOUNTAIN_MINIMALIST.id)
            ?: WallpaperPresets.MOUNTAIN_MINIMALIST.id
    )
    val activeWallpaperId: StateFlow<String> = _activeWallpaperId.asStateFlow()

    private val _customWallpaperUri = MutableStateFlow(prefs.getString(KEY_CUSTOM_WALLPAPER_URI, null))
    val customWallpaperUri: StateFlow<String?> = _customWallpaperUri.asStateFlow()

    private val _wallpaperDim = MutableStateFlow(prefs.getFloat(KEY_WALLPAPER_DIM, 0.65f))
    val wallpaperDim: StateFlow<Float> = _wallpaperDim.asStateFlow()

    private val _focusModeActive = MutableStateFlow(prefs.getBoolean(KEY_FOCUS_ACTIVE, false))
    val focusModeActive: StateFlow<Boolean> = _focusModeActive.asStateFlow()

    private val _focusDurationMinutes = MutableStateFlow(prefs.getInt(KEY_FOCUS_MINUTES, 25))
    val focusDurationMinutes: StateFlow<Int> = _focusDurationMinutes.asStateFlow()

    fun setMonochromeIcons(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MONOCHROME_ICONS, enabled).apply()
        _monochromeIcons.value = enabled
    }

    fun setOledPureBlack(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_OLED_BLACK, enabled).apply()
        _oledPureBlack.value = enabled
        if (enabled) {
            setActiveWallpaperId(WallpaperPresets.AMOLED_PURE_BLACK.id)
        } else if (_activeWallpaperId.value == WallpaperPresets.AMOLED_PURE_BLACK.id) {
            setActiveWallpaperId(WallpaperPresets.MOUNTAIN_MINIMALIST.id)
        }
    }

    fun setActiveWallpaperId(wallpaperId: String) {
        prefs.edit().putString(KEY_ACTIVE_WALLPAPER_ID, wallpaperId).apply()
        _activeWallpaperId.value = wallpaperId
        if (wallpaperId == WallpaperPresets.AMOLED_PURE_BLACK.id) {
            prefs.edit().putBoolean(KEY_OLED_BLACK, true).apply()
            _oledPureBlack.value = true
        } else {
            prefs.edit().putBoolean(KEY_OLED_BLACK, false).apply()
            _oledPureBlack.value = false
        }
    }

    fun setCustomWallpaperUri(uriString: String?) {
        prefs.edit().putString(KEY_CUSTOM_WALLPAPER_URI, uriString).apply()
        _customWallpaperUri.value = uriString
        if (uriString != null) {
            setActiveWallpaperId("custom_photo")
        }
    }

    fun setWallpaperDim(dim: Float) {
        val clamped = dim.coerceIn(0.2f, 0.95f)
        prefs.edit().putFloat(KEY_WALLPAPER_DIM, clamped).apply()
        _wallpaperDim.value = clamped
    }

    fun setFocusModeActive(active: Boolean) {
        prefs.edit().putBoolean(KEY_FOCUS_ACTIVE, active).apply()
        _focusModeActive.value = active
    }

    fun setFocusDurationMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_FOCUS_MINUTES, minutes).apply()
        _focusDurationMinutes.value = minutes
    }

    companion object {
        private const val KEY_MONOCHROME_ICONS = "monochrome_icons"
        private const val KEY_OLED_BLACK = "oled_pure_black"
        private const val KEY_ACTIVE_WALLPAPER_ID = "active_wallpaper_id"
        private const val KEY_CUSTOM_WALLPAPER_URI = "custom_wallpaper_uri"
        private const val KEY_WALLPAPER_DIM = "wallpaper_dim"
        private const val KEY_FOCUS_ACTIVE = "focus_mode_active"
        private const val KEY_FOCUS_MINUTES = "focus_duration_minutes"

        @Volatile
        private var instance: LauncherSettingsRepository? = null

        fun getInstance(context: Context): LauncherSettingsRepository {
            return instance ?: synchronized(this) {
                instance ?: LauncherSettingsRepository(context).also { instance = it }
            }
        }
    }
}
