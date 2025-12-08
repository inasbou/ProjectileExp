package org.example.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.theme.Theme
import org.example.project.AppContainer


@Composable
fun InputScreen() {
    val vm = AppContainer.mainViewModel
    val ui = vm.uiState

    var masse by remember { mutableStateOf(ui.masse) }
    var gravite by remember { mutableStateOf(ui.gravite) }
    var vitesse0 by remember { mutableStateOf(ui.vitesse0) }
    var alpha0 by remember { mutableStateOf(ui.alpha0) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {

        OutlinedTextField(value = masse, onValueChange = { masse = it }, label = { Text("Masse") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = gravite, onValueChange = { gravite = it }, label = { Text("Gravité") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = vitesse0, onValueChange = { vitesse0 = it }, label = { Text("Vitesse v0") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = alpha0, onValueChange = { alpha0 = it }, label = { Text("Angle α") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                vm.onEvent(AppEvent.SubmitInputs(masse, gravite, vitesse0, alpha0))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}
