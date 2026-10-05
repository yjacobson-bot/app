package com.ozerli.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ozerli.app.notifications.ReminderWorker
import com.ozerli.app.ui.AddRequestScreen
import com.ozerli.app.ui.AddTaskScreen
import com.ozerli.app.ui.HistoryScreen
import com.ozerli.app.ui.MainScreen
import com.ozerli.app.ui.StatsScreen
import com.ozerli.app.ui.theme.OzerLiTheme
import com.ozerli.app.viewmodel.RequestViewModel
import com.ozerli.app.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* granted or not */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        ReminderWorker.schedule(this)

        setContent {
            OzerLiTheme {
                val nav     = rememberNavController()
                val vm      : RequestViewModel = viewModel()
                val taskVm  : TaskViewModel    = viewModel()

                NavHost(navController = nav, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            viewModel         = vm,
                            taskViewModel     = taskVm,
                            onAddRequestClick = { nav.navigate("add_request") },
                            onAddTaskClick    = { nav.navigate("add_task") },
                            onStatsClick      = { nav.navigate("stats") },
                            onHistoryClick    = { nav.navigate("history") },
                            onEditRequest     = { vm.selectForEdit(it); nav.navigate("add_request") },
                            onEditTask        = { taskVm.selectForEdit(it); nav.navigate("add_task") }
                        )
                    }
                    composable("add_request") {
                        AddRequestScreen(viewModel = vm, onBack = { nav.popBackStack() })
                    }
                    composable("add_task") {
                        AddTaskScreen(viewModel = taskVm, onBack = { nav.popBackStack() })
                    }
                    composable("stats") {
                        StatsScreen(viewModel = vm, onBack = { nav.popBackStack() })
                    }
                    composable("history") {
                        HistoryScreen(viewModel = vm, onBack = { nav.popBackStack() })
                    }
                }
            }
        }
    }
}
