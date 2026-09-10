package com.example.data

import androidx.annotation.DrawableRes
import com.example.R

enum class WallpaperType {
    PRESET_DRAWABLE,
    CUSTOM_URI,
    AMOLED_BLACK
}

data class WallpaperItem(
    val id: String,
    val name: String,
    val description: String,
    val type: WallpaperType,
    @DrawableRes val drawableRes: Int? = null,
    val uriString: String? = null,
    val isDefault: Boolean = false
)

object WallpaperPresets {
    val MOUNTAIN_MINIMALIST = WallpaperItem(
        id = "mountain_minimalist",
        name = "Dumanlı Dağlar (Minimalist)",
        description = "Misty peaks, dərin duman və yüksək kontrastlı monoxrom dağ mənzərəsi",
        type = WallpaperType.PRESET_DRAWABLE,
        drawableRes = R.drawable.img_mountain_minimalist,
        isDefault = true
    )

    val MOUNTAIN_CLASSIC = WallpaperItem(
        id = "mountain_classic",
        name = "Klassik Dağ Zirvəsi",
        description = "Sakit və geniş qaranlıq dağ silsiləsi",
        type = WallpaperType.PRESET_DRAWABLE,
        drawableRes = R.drawable.wallpaper_mountain
    )

    val AMOLED_PURE_BLACK = WallpaperItem(
        id = "amoled_black",
        name = "AMOLED Təmiz Qara",
        description = "Sıfır diqqət yayındırma, maksimum batareya qənaəti və OLED piksellərin sönməsi",
        type = WallpaperType.AMOLED_BLACK
    )

    val list: List<WallpaperItem> = listOf(
        MOUNTAIN_MINIMALIST,
        MOUNTAIN_CLASSIC,
        AMOLED_PURE_BLACK
    )

    fun getById(id: String): WallpaperItem {
        return list.firstOrNull { it.id == id } ?: MOUNTAIN_MINIMALIST
    }
}
