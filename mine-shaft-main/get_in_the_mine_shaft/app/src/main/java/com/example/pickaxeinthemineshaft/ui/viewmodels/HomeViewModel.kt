package com.example.pickaxeinthemineshaft.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickaxeinthemineshaft.data.TaskRepository
import com.example.pickaxeinthemineshaft.data.AvatarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val avatarRepository: AvatarRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        Log.d(TAG, "Initializing HomeViewModel")
        viewModelScope.launch {
            taskRepository.getActiveTasks().collect { tasks ->
                avatarRepository.getAvatar().collect { avatar ->
                    _uiState.value = HomeUiState(
                        activeTasks = tasks.size,
                        avatarLevel = avatar?.level ?: 1
                    )
                }
            }
        }
        Log.d(TAG, "HomeViewModel initialization completed")
    }
}

data class HomeUiState(
    val activeTasks: Int = 0,
    val avatarLevel: Int = 1
) 