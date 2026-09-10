package com.fedi.focuslauncher

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fedi.focuslauncher.ui.screens.AppDrawerScreen
import com.fedi.focuslauncher.ui.screens.FocusModeScreen
import com.fedi.focuslauncher.ui.screens.HomeScreen
import com.fedi.focuslauncher.ui.screens.SettingsScreen
import com.fedi.focuslauncher.ui.screens.WallpaperSelectionScreen
import com.fedi.focuslauncher.ui.screens.WidgetsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LauncherApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home_pager",
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        composable("home_pager") {
            val pagerState = rememberPagerState(initialPage = 1, pageCount = { 2 })
            val coroutineScope = rememberCoroutineScope()

            // Essential for an Android Launcher: pressing back on Widgets page scrolls to Home.
            // On Home page, back press does nothing so user never exits the launcher!
            BackHandler(enabled = true) {
                if (pagerState.currentPage == 0) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> WidgetsScreen(navController)
                    1 -> HomeScreen(navController)
                }
            }
        }
        composable("app_drawer") {
            AppDrawerScreen(navController)
        }
        composable("focus_mode") {
            FocusModeScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("wallpaper_settings") {
            WallpaperSelectionScreen(navController)
        }
    }
}

