package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(
    onSubmit: (String, String, String, String) -> Unit
) {
    MaterialTheme {
        var masse by remember { mutableStateOf("") }
        var gravite by remember { mutableStateOf("9.81") } // default g
        var vitesse0 by remember { mutableStateOf("") }
        var alpha0 by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onClick = {
                    onSubmit(masse, gravite, vitesse0, alpha0)
                },

                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }
}
