package com.example.aplicacionfractal.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacionfractal.ui.theme.ColorPrimario
import com.example.aplicacionfractal.ui.theme.ColorSecundario


@Composable
fun MenuPrincipal(
    navController: NavHostController,
    selectedItemIndex: Int,
    onSelectedItemChange: (Int) -> Unit,
    content: @Composable () -> Unit
) {
    val items = listOf(
        NavigationItem("Facturas", Icons.Filled.Home),
        NavigationItem("Gastos", Icons.Filled.ShoppingCart),
        NavigationItem("Clientes", Icons.Filled.DateRange),
        NavigationItem("Hitos", Icons.Filled.Build)
    )

    Scaffold(
        bottomBar = {
            Box(contentAlignment = Alignment.TopCenter) {
                BottomAppBar(
                    containerColor = ColorPrimario,
                    contentColor = Color.White,
                    tonalElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        items.forEachIndexed { index, item ->
                            if (index == items.size / 2) {
                                Spacer(modifier = Modifier.width(56.dp)) // Espacio para el FAB
                            }
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title, fontSize = 12.sp) },
                                selected = selectedItemIndex == index,
                                onClick = {
                                    onSelectedItemChange(index)
                                    when (index) {
                                        0 -> navController.navigate("pantallaPrincipal")
                                        1 -> navController.navigate("pantallaGastos")
                                        2 -> navController.navigate("pantallaAgenda")
                                        3 -> navController.navigate("pantallaAlumnos")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                FloatingActionButton(
                    onClick = { navController.navigate("pantallaAddFactura") },
                    containerColor = ColorSecundario,
                    contentColor = Color.White,
                    modifier = Modifier.offset(y = (-25).dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir")
                }
            }
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