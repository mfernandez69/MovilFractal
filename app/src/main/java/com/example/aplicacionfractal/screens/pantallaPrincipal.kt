package com.example.aplicacionfractal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aplicacionfractal.data.models.Factura
import com.example.aplicacionfractal.utils.MenuPrincipal
import com.example.aplicacionfractal.viewModels.FacturaViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    facturaViewModel: FacturaViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MenuPrincipal(navController = navController) { paddingValues ->
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Listado de Facturas") },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.size(5.dp))
                ListadoFacturas(facturaViewModel)
            }
        }
    }
}

@Composable
fun ListadoFacturas(facturaViewModel: FacturaViewModel) {
    val facturas by facturaViewModel.facturas.collectAsState()

    if (facturas.isEmpty()) {
        Text("No hay facturas disponibles", Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(facturas) { factura ->
                FacturaItem(
                    factura,
                    onEdit = { /* Lógica para editar */ },
                    onDelete = { facturaViewModel.eliminarFactura(factura) }
                )
            }
        }
    }
}

@Composable
fun FacturaItem(
    factura: Factura,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val fechaFormateada = factura.fechaEmision?.toDate()?.let { dateFormat.format(it) } ?: "Fecha no disponible"
    var expanded by remember { mutableStateOf(false) } // Controla si el footer está visible o no
    var showDialog by remember { mutableStateOf(false) } // Controla si se muestra el diálogo de confirmación

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .clickable { expanded = !expanded }, // Activa/desactiva el footer al pulsar la tarjeta
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Información principal de la factura
            Text(
                text = "Factura Nº ${factura.nFactura}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(text = "Fecha: $fechaFormateada")
            Text(text = "Base Imponible: ${factura.baseImponible}€")
            Text(text = "IVA: ${factura.IVA}%")
            Text(text = "Total: ${factura.total}€", fontWeight = FontWeight.Bold)

            // Footer desplegable (visible solo si `expanded` es true)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp)) // Espaciado entre el contenido principal y el footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onEdit) {
                        Text("Editar")
                    }
                    Button(
                        onClick = { showDialog = true }, // Muestra el diálogo de confirmación
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar la factura
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Cierra el diálogo al tocar fuera de él
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta factura?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false // Cierra el diálogo
                        onDelete() // Llama a la función para eliminar la factura
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { // Cierra el diálogo sin eliminar
                    Text("Cancelar")
                }
            }
        )
    }
}
