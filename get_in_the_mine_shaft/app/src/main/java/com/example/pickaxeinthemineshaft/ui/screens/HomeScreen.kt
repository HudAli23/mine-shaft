package com.example.pickaxeinthemineshaft.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.example.pickaxeinthemineshaft.ui.components.SimpleGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.ui.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToTaskList: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToAvatar: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val dashboardItems = listOf(
        DashboardItem("Tasks", Icons.Default.List, onNavigateToTaskList),
        DashboardItem("Calendar", Icons.Default.DateRange, onNavigateToCalendar),
        DashboardItem("Avatar", Icons.Default.Person, onNavigateToAvatar),
        DashboardItem("Statistics", Icons.Default.Info, onNavigateToStatistics)
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome Back!",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        SimpleGrid(
            items = dashboardItems,
            columns = 2,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) { item ->
            DashboardCard(item)
        }
    }
}

@Composable
private fun DashboardCard(item: DashboardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = item.onClick),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.h6
            )
        }
    }
}

private data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
) 