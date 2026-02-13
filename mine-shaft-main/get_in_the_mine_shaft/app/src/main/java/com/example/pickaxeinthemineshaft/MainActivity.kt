package com.example.pickaxeinthemineshaft
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.pickaxeinthemineshaft.navigation.AppNavigation
import com.example.pickaxeinthemineshaft.ui.components.BottomNavBar
import com.example.pickaxeinthemineshaft.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Starting MainActivity onCreate")
        super.onCreate(savedInstanceState)
        
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            Log.e(TAG, "Uncaught exception", e)
        }
        
        setContent {
            Log.d(TAG, "Setting up MainScreen")
            mainScreen()
        }
        
        Log.d(TAG, "MainActivity onCreate completed")
    }
}

@Composable
private fun mainScreen() {
    DisposableEffect(Unit) {
        Log.d(TAG, "MainScreen composition started")
        onDispose {
            Log.d(TAG, "MainScreen disposed")
        }
    }
    
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            val navController = rememberNavController()
            
            Scaffold(
                bottomBar = {
                    BottomNavBar(navController = navController)
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    AppNavigation(navController = navController)
                }
            }
        }
    }
}