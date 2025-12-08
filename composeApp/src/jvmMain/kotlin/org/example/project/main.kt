package org.example.project

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.project.AppContainer
import org.example.project.theme.Theme
@Composable
fun MainScreen() {
    val viewModel = AppContainer.mainViewModel
    val state = viewModel.uiState

    Column {
        TabRow(selectedTabIndex = state.currentTab.ordinal) {

            Tab(
                selected = state.currentTab == TabType.Input,
                onClick = { viewModel.onEvent(AppEvent.SelectTab(TabType.Input)) },
                text = { Text("Input") }
            )

            Tab(
                selected = state.currentTab == TabType.Results,
                onClick = { viewModel.onEvent(AppEvent.SelectTab(TabType.Results)) },
                text = { Text("Results") }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (state.currentTab) {
                TabType.Input -> InputScreen()
                TabType.Results -> ResultWindow(
                    state.masse, state.gravite, state.vitesse0, state.alpha0
                )
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Projectile Simulation"
    ) {
        Theme {
            MainScreen()
        }
    }
}