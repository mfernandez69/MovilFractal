package com.example.aplicacionfractal.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import android.widget.Toast
import android.content.Context
import android.os.Environment
import androidx.compose.ui.platform.LocalContext
import com.example.aplicacionfractal.data.models.Emisor
import com.example.aplicacionfractal.data.models.Receptor
import com.example.aplicacionfractal.utils.TabMultiple
import com.itextpdf.text.Paragraph
import android.content.ContentValues
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.itextpdf.text.*
import com.itextpdf.layout.element.*



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
                TabMultiple(facturaViewModel,navController)
            }
        }
    }
}

@Composable
fun ListadoFacturasRecibidas(facturaViewModel: FacturaViewModel,navController: NavHostController) {
    val facturas by facturaViewModel.facturas.collectAsState()
    val facturasRecibidas = facturas.filter { !it.emitida }

    ListadoFacturasGenerico(
        facturas = facturasRecibidas,
        facturaViewModel = facturaViewModel,
        emptyMessage = "No hay facturas recibidas disponibles",
        navController = navController
    )
}

@Composable
fun ListadoFacturasEmitidas(facturaViewModel: FacturaViewModel,navController: NavHostController) {
    val facturas by facturaViewModel.facturas.collectAsState()
    val facturasEmitidas = facturas.filter { it.emitida }

    ListadoFacturasGenerico(
        facturas = facturasEmitidas,
        facturaViewModel = facturaViewModel,
        emptyMessage = "No hay facturas emitidas disponibles",
        navController = navController
    )
}

@Composable
fun ListadoFacturasGenerico(
    facturas: List<Factura>,
    facturaViewModel: FacturaViewModel,
    emptyMessage: String,
    navController: NavHostController
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
                    onEdit = { numeroFactura ->
                        navController.navigate("pantallaEditarFactura/$numeroFactura")
                    },
                    onDelete = { facturaViewModel.eliminarFactura(factura) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ListadoFacturas(facturaViewModel: FacturaViewModel, navController: NavHostController) {
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
                    onEdit = { numeroFactura: String ->
                        navController.navigate("pantallaEditarFactura/$numeroFactura")
                    },
                    onDelete = { facturaViewModel.eliminarFactura(factura) }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun FacturaItem(
    factura: Factura,
    facturaViewModel: FacturaViewModel,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val porcentajeIva = factura.IVA * 100 / factura.baseImponible
    val context = LocalContext.current  // Obtener contexto
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var emisor by remember { mutableStateOf<Emisor?>(null) }
    var receptor by remember { mutableStateOf<Receptor?>(null) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Factura Nº ${factura.nFactura}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    println("Descargando Factura ${factura.nFactura}")
                    // Llama a la función que maneja la exportación a PDF
                    exportToPDF(context, factura)
                }) {
                    Icon(
                        imageVector = Icons.Filled.FileDownload, // Icono de descarga
                        contentDescription = "Descargar Factura"
                    )
                }
            }

            Text(text = "Fecha: ${factura.fechaEmision}")
            Text(text = "Base Imponible: ${factura.baseImponible}€")
            Text(text = "IVA (${porcentajeIva}%): ${factura.baseImponible * porcentajeIva / 100}€")
            Text(text = "Total: ${factura.total}€", fontWeight = FontWeight.Bold)

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
                    Button(onClick = { onEdit(factura.nFactura) }) {
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

fun exportToPDF(context: Context, factura: Factura) {
    val document = Document()
    val contentResolver = context.contentResolver

    // Creamos los valores para el PDF en MediaStore
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "Factura_${factura.nFactura}.pdf")
        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) // ✅ Correcto para Android 10+
    }

    // Insertamos el archivo en MediaStore y obtenemos su URI
    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

    if (uri == null) {
        Toast.makeText(context, "No se pudo crear el archivo PDF", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        contentResolver.openOutputStream(uri)?.use { outputStream ->
            val pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream)
            document.open()
            document.add(Paragraph("Factura Nº: ${factura.nFactura}"))
            document.add(Paragraph("Fecha: ${factura.fechaEmision}"))
            document.add(Paragraph("Base Imponible: ${factura.baseImponible}"))
            document.add(Paragraph("IVA: ${factura.IVA}%"))
            document.add(Paragraph("Total: ${factura.total}"))
            document.close()

            Toast.makeText(context, "Factura exportada en Descargas", Toast.LENGTH_SHORT).show()
        } ?: throw Exception("OutputStream es nulo")
    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar la factura", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
