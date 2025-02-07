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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.aplicacionfractal.data.models.Emisor
import com.example.aplicacionfractal.data.models.Factura
import com.example.aplicacionfractal.data.models.Receptor
import com.example.aplicacionfractal.ui.theme.ColorPrimario
import com.example.aplicacionfractal.utils.MenuPrincipal
import com.example.aplicacionfractal.utils.TabMultiple
import com.example.aplicacionfractal.viewModels.FacturaViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavHostController,
    facturaViewModel: FacturaViewModel = viewModel(),
    selectedItemIndex: Int,
    onSelectedItemChange: (Int) -> Unit
) {
    MenuPrincipal(
        navController = navController,
        selectedItemIndex = selectedItemIndex,
        onSelectedItemChange = onSelectedItemChange
    ) {
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
                TabMultiple(facturaViewModel)
            }
        }
    }
}

@Composable
fun ListadoFacturasRecibidas(facturaViewModel: FacturaViewModel) {
    val facturas by facturaViewModel.facturas.collectAsState()
    val facturasRecibidas = facturas.filter { !it.emitida }

    ListadoFacturasGenerico(
        facturas = facturasRecibidas,
        facturaViewModel = facturaViewModel,
        emptyMessage = "No hay facturas recibidas disponibles"
    )
}

@Composable
fun ListadoFacturasEmitidas(facturaViewModel: FacturaViewModel) {
    val facturas by facturaViewModel.facturas.collectAsState()
    val facturasEmitidas = facturas.filter { it.emitida }

    ListadoFacturasGenerico(
        facturas = facturasEmitidas,
        facturaViewModel = facturaViewModel,
        emptyMessage = "No hay facturas emitidas disponibles"
    )
}
@Composable
fun ListadoFacturasGenerico(
    facturas: List<Factura>,
    facturaViewModel: FacturaViewModel,
    emptyMessage: String
) {
    if (facturas.isEmpty()) {
        Text(emptyMessage, Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(facturas) { factura ->
                FacturaItem(
                    factura,
                    facturaViewModel = facturaViewModel,
                    onEdit = { /* Lógica para editar */ },
                    onDelete = { facturaViewModel.eliminarFactura(factura) }
                )
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
                    facturaViewModel = facturaViewModel,
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
    facturaViewModel: FacturaViewModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val porcentajeIva = 21
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var emisor by remember { mutableStateOf<Emisor?>(null) }
    var receptor by remember { mutableStateOf<Receptor?>(null) }

    // Configuración para formatear números con coma como separador decimal
    val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        decimalSeparator = ','
    }
    val decimalFormat = DecimalFormat("#,##0.00", decimalFormatSymbols)

    LaunchedEffect(factura, expanded) {
        if (expanded && (emisor == null || receptor == null)) {
            emisor = facturaViewModel.obtenerEmisor(factura.emisorId)
            receptor = facturaViewModel.obtenerReceptor(factura.receptorId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Factura Nº ${factura.nFactura}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(text = "Fecha: ${factura.fechaEmision?.toDate()?.let { dateFormat.format(it) } ?: "No disponible"}")
            Text(text = "Base Imponible: ${decimalFormat.format(factura.baseImponible)}€")
            Text(text = "IVA (${porcentajeIva}%): ${decimalFormat.format(factura.baseImponible * porcentajeIva / 100)}€")
            Text(text = "Total: ${decimalFormat.format(factura.total)}€", fontWeight = FontWeight.Bold)

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Detalles de la Factura", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))

                emisor?.let { emisor ->
                    Text("Emisor:", fontWeight = FontWeight.Bold)
                    Text("NIF: ${emisor.nif}")
                    Text("Dirección: ${emisor.direccionEmisor}")
                    Text("Empresa: ${emisor.empresa}")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                receptor?.let { receptor ->
                    Text("Receptor:", fontWeight = FontWeight.Bold)
                    Text("Nombre: ${receptor.cliente}")
                    Text("CIF: ${receptor.cif}")
                    Text("Dirección: ${receptor.direccionReceptor}")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onEdit) {
                        Text("Editar")
                    }
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar esta factura?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}