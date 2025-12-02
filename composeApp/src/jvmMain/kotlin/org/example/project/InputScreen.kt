package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.theme.Theme

@Composable
fun InputScreen(viewModel: MainViewModel) {
    val ui = viewModel.uiState

    var masse by remember { mutableStateOf(ui.masse) }
    var gravite by remember { mutableStateOf(ui.gravite) }
    var vitesse0 by remember { mutableStateOf(ui.vitesse0) }
    var alpha0 by remember { mutableStateOf(ui.alpha0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = masse,
            onValueChange = { masse = it },
            label = { Text("Masse (kg)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = gravite,
            onValueChange = { gravite = it },
            label = { Text("Gravité (m/s²)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = vitesse0,
            onValueChange = { vitesse0 = it },
            label = { Text("Vitesse initiale v₀ (m/s)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = alpha0,
            onValueChange = { alpha0 = it },
            label = { Text("Angle α (degrees)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.submitInputs(masse, gravite, vitesse0, alpha0) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
