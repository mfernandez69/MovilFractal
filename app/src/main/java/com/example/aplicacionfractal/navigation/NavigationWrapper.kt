package com.example.aplicacionfractal.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.aplicacionfractal.screens.PantallaAddFactura
import com.example.aplicacionfractal.screens.PantallaGastos
import com.example.aplicacionfractal.screens.PantallaPrincipal

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun NavigationWrapper(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "pantallaPrincipal") {
        composable("pantallaPrincipal") {
            PantallaPrincipal(navHostController)
        }
        composable("pantallaAddFactura") {
            PantallaAddFactura(navHostController)
        }
        composable("pantallaGastos") {
            PantallaGastos(navHostController)
        }
    }
}
