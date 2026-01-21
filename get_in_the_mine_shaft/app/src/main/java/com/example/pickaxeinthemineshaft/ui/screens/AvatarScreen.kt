package com.example.pickaxeinthemineshaft.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import com.example.pickaxeinthemineshaft.ui.components.SimpleGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickaxeinthemineshaft.ui.viewmodels.AvatarViewModel
import com.example.pickaxeinthemineshaft.data.model.Mood
import com.example.pickaxeinthemineshaft.data.model.PersonalityType
import com.example.pickaxeinthemineshaft.data.model.Achievement
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.rememberScaffoldState
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.random.Random
import androidx.compose.material.icons.filled.Pets
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentSatisfied

@Composable
fun AvatarScreen(
    viewModel: AvatarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    // Get real completion rate from StatisticsViewModel
    val statisticsViewModel: com.example.pickaxeinthemineshaft.ui.viewmodels.StatisticsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val statsUiState by statisticsViewModel.uiState.collectAsState()
    val realCompletionRate = statsUiState.completionRate
    // Compute pet reaction based on real completion rate
    val petReaction = when {
        realCompletionRate >= 70 -> "PROUD"
        realCompletionRate <= 40 -> "SCORNFUL"
        else -> "ENCOURAGING"
    }
    var showMessage by remember { mutableStateOf(false) }
    var lastMessage by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pet Icon (use different icon for each mood)
        val (icon, color) = when (petReaction) {
            "SCORNFUL" -> Pair(Icons.Default.SentimentVeryDissatisfied, MaterialTheme.colors.error)
            "ENCOURAGING" -> Pair(Icons.Default.SentimentSatisfied, MaterialTheme.colors.primary)
            "PROUD" -> Pair(Icons.Default.Star, MaterialTheme.colors.secondary)
            else -> Pair(Icons.Default.Face, MaterialTheme.colors.onSurface)
        }
        Icon(icon, contentDescription = null, modifier = Modifier.size(100.dp), tint = color)
        Spacer(modifier = Modifier.height(8.dp))
        // Show mood label
        Text(
            text = "Mood: ${petReaction.capitalize()}",
            style = MaterialTheme.typography.subtitle1,
            color = color
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to make the pet say something
        Button(onClick = {
            showMessage = true
            lastMessage = randomPetMessage(petReaction)
        }) {
            Text("Talk to Pet")
        }
        if (showMessage) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                lastMessage,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.caption)
            Text(value, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
private fun PetReactionSection(petReaction: String) {
    val (icon, color) = when (petReaction) {
        "PROUD" -> Pair(Icons.Default.Star, MaterialTheme.colors.primary)
        "HAPPY" -> Pair(Icons.Default.Favorite, MaterialTheme.colors.primary)
        "SAD" -> Pair(Icons.Default.Warning, MaterialTheme.colors.error)
        else -> Pair(Icons.Default.Face, MaterialTheme.colors.onSurface)
    }
    val message = petReactionMessage(petReaction)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(100.dp), tint = color)
        Text(message, style = MaterialTheme.typography.h6, color = color, modifier = Modifier.padding(top = 8.dp))
    }
}

private fun petReactionMessage(petReaction: String): String {
    return when (petReaction) {
        "PROUD" -> "Your pet is proud of your streak!"
        "HAPPY" -> "Your pet is happy with your progress!"
        "SAD" -> "Your pet is sad. Try to complete more tasks!"
        else -> "Your pet is waiting to see what you'll do next."
    }
}

private fun randomPetMessage(petReaction: String): String {
    val messages = when (petReaction) {
        "SCORNFUL" -> listOf(
            "You can do better than that...",
            "Is that all you've got?",
            "Come on, I expected more!"
        )
        "ENCOURAGING" -> listOf(
            "Keep going, you're making progress!",
            "I'm here to cheer you on!",
            "We can do it! Try to complete more tasks."
        )
        "PROUD" -> listOf(
            "I'm so proud of you! 🏆",
            "You're crushing it!",
            "Amazing work, keep it up!"
        )
        else -> listOf(
            "I'm here if you need me!",
            "What will we do next?",
            "Let's make today awesome!"
        )
    }
    return messages[Random.nextInt(messages.size)]
}