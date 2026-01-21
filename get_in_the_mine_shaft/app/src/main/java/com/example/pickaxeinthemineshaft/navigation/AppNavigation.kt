package com.example.pickaxeinthemineshaft.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pickaxeinthemineshaft.ui.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTaskList = { navController.navigate(Screen.TaskList.route) },
                onNavigateToCalendar = { navController.navigate(Screen.Calendar.route) },
                onNavigateToAvatar = { navController.navigate(Screen.Avatar.route) },
                onNavigateToStatistics = { navController.navigate(Screen.Statistics.route) }
            )
        }
        
        composable(Screen.TaskList.route) {
            TaskListScreen(
                onNavigateToAddTask = { navController.navigate(Screen.AddTask.route) },
                onNavigateToEditTask = { taskId ->
                    navController.navigate(Screen.EditTask.createRoute(taskId))
                }
            )
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
        
        composable(Screen.Avatar.route) {
            AvatarScreen()
        }
        
        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }
        
        composable(Screen.AddTask.route) {
            AddTaskScreen(
                onTaskAdded = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.EditTask.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            EditTaskScreen(
                taskId = taskId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 