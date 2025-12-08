package org.example.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.example.project.AppContainer

class MainViewModel {

    var uiState by mutableStateOf(AppState())
        private set

    fun onEvent(event: AppEvent) {
        when (event) {
            is AppEvent.SelectTab -> {
                uiState = uiState.copy(currentTab = event.tab)
            }
            is AppEvent.SubmitInputs -> {
                uiState = uiState.copy(
                    masse = event.masse,
                    gravite = event.gravite,
                    vitesse0 = event.vitesse0,
                    alpha0 = event.alpha0,
                    currentTab = TabType.Results
                )
            }
            AppEvent.ResetInputs -> {
                uiState = AppState()
            }
        }
    }
}


data class AppState(
    val currentTab: TabType = TabType.Input,
    val masse: String = "",
    val gravite: String = "9.81",
    val vitesse0: String = "",
    val alpha0: String = ""
)
sealed class AppEvent {
    data class SelectTab(val tab: TabType) : AppEvent()
    data class SubmitInputs(
        val masse: String,
        val gravite: String,
        val vitesse0: String,
        val alpha0: String
    ) : AppEvent()

    object ResetInputs : AppEvent()
}
enum class TabType { Input, Results }
