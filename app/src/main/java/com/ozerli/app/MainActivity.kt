package com.ozerli.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozerli.app.notifications.ReminderWorker
import com.ozerli.app.ui.AddRequestScreen
import com.ozerli.app.ui.HistoryScreen
import com.ozerli.app.ui.MainScreen
import com.ozerli.app.ui.StatsScreen
import com.ozerli.app.ui.theme.OzerLiTheme
import com.ozerli.app.viewmodel.RequestViewModel

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // בקשת הרשאת התראות
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // הפעלת תזכורות תקופתיות
        ReminderWorker.schedule(this)

        setContent {
            OzerLiTheme {
                val navController = rememberNavController()
                val viewModel: RequestViewModel = viewModel()

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel = viewModel,
                            onAddClick = { navController.navigate("add") },
                            onStatsClick = { navController.navigate("stats") },
                            onHistoryClick = { navController.navigate("history") }
                        )
                    }
                    composable("add") {
                        AddRequestScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("stats") {
                        StatsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("history") {
                        HistoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
