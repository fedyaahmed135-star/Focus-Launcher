package com.fedi.focuslauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fedi.focuslauncher.data.AppListRepository
import com.fedi.focuslauncher.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize the repository to start loading apps and listening to package changes immediately
    AppListRepository.getInstance(applicationContext)
    
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        LauncherApp()
      }
    }
  }
}
