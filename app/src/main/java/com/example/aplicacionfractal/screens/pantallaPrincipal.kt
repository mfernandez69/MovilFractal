package com.example.aplicacionfractal.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.aplicacionfractal.utils.MenuPrincipal

@Composable
fun PantallaPrincipal(navController: NavHostController) {
    MenuPrincipal(navController = navController) {
        // Contenido de la pantalla principal
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Contenido de la Pantalla Principal")
        }
    }
}