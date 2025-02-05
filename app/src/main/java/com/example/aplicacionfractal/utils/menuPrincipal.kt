package com.example.aplicacionfractal.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.aplicacionfractal.ui.theme.ColorPrimario
import com.example.aplicacionfractal.ui.theme.ColorSecundario


@Composable
fun MenuPrincipal(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val items = listOf(
        NavigationItem("Facturas", Icons.Filled.Home),
        NavigationItem("Gastos", Icons.Filled.ShoppingCart),
        NavigationItem("Clientes", Icons.Filled.DateRange),
        NavigationItem("Hitos", Icons.Filled.Build)
    )
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = ColorPrimario,
                contentColor = Color.White,
                actions = {
                    items.forEachIndexed { index, item ->
                        if (index == items.size / 2) {
                            Spacer(Modifier.weight(1f))
                        }
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                when (index) {
                                    0 -> navController.navigate("pantallaPrincipal")
                                    1 -> navController.navigate("pantallaPago")
                                    2 -> navController.navigate("pantallaAgenda")
                                    3 -> navController.navigate("pantallaAlumnos")
                                }
                            }
                        )
                        if (index == items.size / 2 - 1) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { /* Acción del botón central */ },
                        containerColor = ColorSecundario,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Añadir")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}


data class NavigationItem(
    val title: String,
    val icon: ImageVector
)
