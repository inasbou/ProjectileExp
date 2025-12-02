package org.example.project

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val state = viewModel.uiState

    Column {
        TabRow(selectedTabIndex = state.currentTab) {
            Tab(
                selected = state.currentTab == 0,
                onClick = { viewModel.selectTab(0) },
                text = { Text("Input") }
            )
            Tab(
                selected = state.currentTab == 1,
                onClick = { viewModel.selectTab(1) },
                text = { Text("Results") }
            )

        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (state.currentTab) {
                0 -> InputScreen(viewModel)
                1 -> ResultWindow(
                    state.masse,
                    state.gravite,
                    state.vitesse0,
                    state.alpha0
                )
            }
        }
    }
}

fun main() = application {
    val viewModel = remember { MainViewModel() }

    Window(onCloseRequest = ::exitApplication, title = "Projectile Simulation") {
        org.example.project.theme.Theme { // use the theme defined
            MainScreen(viewModel)
        }
    }
}
