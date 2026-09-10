package com.fedi.focuslauncher.data

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InstalledApp(
    val name: String,
    val packageName: String,
    val activityName: String,
    val iconBitmap: ImageBitmap? = null
)

class AppListRepository private constructor(private val context: Context) {

    private val applicationContext = context.applicationContext
    
    private val _installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val installedApps: StateFlow<List<InstalledApp>> = _installedApps.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        // Register BroadcastReceiver to listen for package changes
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                loadApps(forceRefresh = true)
            }
        }
        
        applicationContext.registerReceiver(receiver, filter)
        
        // Initial load
        loadApps(forceRefresh = true)
    }

    fun loadApps(forceRefresh: Boolean = false) {
        if (!forceRefresh && _installedApps.value.isNotEmpty()) {
            return
        }

        coroutineScope.launch {
            _isLoading.value = true
            
            val pm = applicationContext.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }

            val loadedApps = resolveInfos
                .mapNotNull { resolveInfo ->
                    val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                    val packageName = activityInfo.packageName
                    val activityName = activityInfo.name
                    val appName = resolveInfo.loadLabel(pm)?.toString() ?: packageName

                    val drawable = resolveInfo.loadIcon(pm)
                    val bitmap = drawableToBitmap(drawable)

                    InstalledApp(
                        name = appName,
                        packageName = packageName,
                        activityName = activityName,
                        iconBitmap = bitmap?.asImageBitmap()
                    )
                }
                .sortedBy { it.name.lowercase() }

            _installedApps.value = loadedApps
            _isLoading.value = false
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return try {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                return drawable.bitmap
            }
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @Volatile
        private var instance: AppListRepository? = null

        fun getInstance(context: Context): AppListRepository {
            return instance ?: synchronized(this) {
                instance ?: AppListRepository(context).also { instance = it }
            }
        }
    }
}
