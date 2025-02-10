package com.example.aplicacionfractal.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.aplicacionfractal.screens.PantallaAddFactura
import com.example.aplicacionfractal.screens.PantallaGastos
import com.example.aplicacionfractal.screens.PantallaPrincipal
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun NavigationWrapper(navHostController: NavHostController) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    NavHost(navController = navHostController, startDestination = "pantallaPrincipal") {
        composable("pantallaPrincipal") {
            PantallaPrincipal(
                navController = navHostController,
                selectedItemIndex = selectedItemIndex,
                onSelectedItemChange = { selectedItemIndex = it }
            )
        }
        composable("pantallaAddFactura") {
            PantallaAddFactura(navController = navHostController)
        }
        composable("pantallaGastos") {
            PantallaGastos(
                navController = navHostController,
                selectedItemIndex = selectedItemIndex,
                onSelectedItemChange = { selectedItemIndex = it }
            )
        }
    }
}
