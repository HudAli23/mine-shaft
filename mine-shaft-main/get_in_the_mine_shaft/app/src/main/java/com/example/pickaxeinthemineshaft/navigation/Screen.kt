package com.example.pickaxeinthemineshaft.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object TaskList : Screen("task_list")
    object Calendar : Screen("calendar")
    object Settings : Screen("settings")
    object Avatar : Screen("avatar")
    object Statistics : Screen("statistics")
    object AddTask : Screen("add_task")
    object EditTask : Screen("edit_task/{taskId}") {
        fun createRoute(taskId: Long) = "edit_task/$taskId"
    }
} 