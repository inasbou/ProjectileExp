package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainViewModel {

    var uiState by mutableStateOf(AppState())
        private set

    fun selectTab(index: Int) {
        uiState = uiState.copy(currentTab = index)
    }

    fun submitInputs(m: String, g: String, v0: String, a0: String) {
        uiState = uiState.copy(
            masse = m,
            gravite = g,
            vitesse0 = v0,
            alpha0 = a0,
            currentTab = 1 // switch to results tab on submit
        )
    }
}

data class AppState(
    val currentTab: Int = 0,
    val masse: String = "",
    val gravite: String = "9.81", // gravity default value
    val vitesse0: String = "",
    val alpha0: String = ""
)
