package com.example.aplicacionfractal.screens


import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.TextAlignment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import java.text.SimpleDateFormat
import java.util.Locale
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
import androidx.compose.ui.platform.LocalContext
import com.example.aplicacionfractal.data.models.Emisor
import com.example.aplicacionfractal.data.models.Receptor
import com.example.aplicacionfractal.utils.TabMultiple
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.FirebaseFirestore
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
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
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

            Text(text = "Fecha: ${factura.fechaEmision?.toDate()?.let { dateFormat.format(it) } ?: "No disponible"}")
            Text(text = "Base Imponible: ${factura.baseImponible}€")
            Text(text = "IVA (${porcentajeIva}%): ${factura.IVA}€")
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
fun obtenerEmisorYReceptor(
    context: Context,
    factura: Factura,
    onComplete: (Emisor?, Receptor?) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Referencias a los documentos
    val emisorRef = factura.emisorId
    val receptorRef = factura.receptorId

    var emisor: Emisor? = null
    var receptor: Receptor? = null

    // Contador de procesos terminados
    var procesosCompletados = 0

    fun verificarFinalizacion() {
        procesosCompletados++
        if (procesosCompletados == 2) {
            onComplete(emisor, receptor)
        }
    }

    // Obtener Emisor
    emisorRef?.get()?.addOnSuccessListener { doc ->
        if (doc.exists()) {
            emisor = doc.toObject(Emisor::class.java)
        }
        verificarFinalizacion()
    }?.addOnFailureListener {
        Toast.makeText(context, "Error al obtener emisor", Toast.LENGTH_SHORT).show()
        verificarFinalizacion()
    }

    // Obtener Receptor
    receptorRef?.get()?.addOnSuccessListener { doc ->
        if (doc.exists()) {
            receptor = doc.toObject(Receptor::class.java)
        }
        verificarFinalizacion()
    }?.addOnFailureListener {
        Toast.makeText(context, "Error al obtener receptor", Toast.LENGTH_SHORT).show()
        verificarFinalizacion()
    }

    // Si ambos son nulos desde el inicio, se llama onComplete
    if (emisorRef == null && receptorRef == null) {
        onComplete(null, null)
    }
}

fun exportToPDF(context: Context, factura: Factura) {
    obtenerEmisorYReceptor(context, factura) { emisor, receptor ->
        val contentResolver = context.contentResolver

        val porcentajeIva = factura.IVA * 100 / factura.baseImponible

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Factura_${factura.nFactura}.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri == null) {
            Toast.makeText(context, "No se pudo crear el archivo PDF", Toast.LENGTH_SHORT).show()
            return@obtenerEmisorYReceptor
        }

        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                val writer = PdfWriter(outputStream)
                val pdfDocument = PdfDocument(writer)
                val document = Document(pdfDocument)

                val fontBold = com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD)
                val fontNormal = com.itextpdf.kernel.font.PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA)

                // Título de la factura
                val title = Paragraph("Factura Nº: ${factura.nFactura}")
                    .setFont(fontBold)
                    .setFontSize(22f)
                    .setFontColor(DeviceRgb(0, 0, 128))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20f)

                document.add(title)

                // Tabla de datos del Emisor
                //document.add(Paragraph("Datos del Emisor").setFont(fontBold).setFontSize(14f).setMarginBottom(5f))
//                val tableEmisor = Table(floatArrayOf(1f, 2f)).useAllAvailableWidth()
//                emisor?.let {
//                    addTableRow(tableEmisor, "Empresa:", it.empresa, fontBold, fontNormal)
//                    addTableRow(tableEmisor, "NIF:", it.nif, fontBold, fontNormal)
//                    addTableRow(tableEmisor, "Dirección:", it.direccionEmisor, fontBold, fontNormal)
//                }
//                document.add(tableEmisor.setMarginBottom(15f))

                // Tabla de datos del Receptor
                //document.add(Paragraph("Datos del Receptor").setFont(fontBold).setFontSize(14f).setMarginBottom(5f))
//                val tableReceptor = Table(floatArrayOf(1f, 2f)).useAllAvailableWidth()
//                receptor?.let {
//                    addTableRow(tableReceptor, "Cliente:", it.cliente, fontBold, fontNormal)
//                    addTableRow(tableReceptor, "CIF:", it.cif, fontBold, fontNormal)
//                    addTableRow(tableReceptor, "Dirección:", it.direccionReceptor, fontBold, fontNormal)
//                }
//                document.add(tableReceptor.setMarginBottom(15f))

                // Tabla de datos de la Factura
                //document.add(Paragraph("Datos de la Factura").setFont(fontBold).setFontSize(14f).setMarginBottom(5f))
                val tableFactura = Table(floatArrayOf(1f, 2f)).useAllAvailableWidth()
                emisor?.let {
                    addTableHeader(tableFactura, "Datos del Emisor", fontBold, fontNormal)
                    addTableRow(tableFactura, "Empresa:", it.empresa, fontBold, fontNormal)
                    addTableRow(tableFactura, "NIF:", it.nif, fontBold, fontNormal)
                    addTableRow(tableFactura, "Dirección:", it.direccionEmisor, fontBold, fontNormal)
                }
                receptor?.let {
                    addTableHeader(tableFactura, "Datos del Receptor", fontBold, fontNormal)
                    addTableRow(tableFactura, "Cliente:", it.cliente, fontBold, fontNormal)
                    addTableRow(tableFactura, "CIF:", it.cif, fontBold, fontNormal)
                    addTableRow(tableFactura, "Dirección:", it.direccionReceptor, fontBold, fontNormal)
                }
                addTableHeader(tableFactura, "Datos de la Factura", fontBold, fontNormal)
                addTableRow(tableFactura, "Base Imponible:", "${factura.baseImponible} €", fontBold, fontNormal)
                addTableRow(tableFactura, "IVA (${porcentajeIva}%):", "${factura.IVA}€", fontBold, fontNormal)
                addTableRow(tableFactura, "Total:", "${factura.total} €", fontBold, fontNormal)
                document.add(tableFactura.setMarginBottom(15f))

                document.close()

                Toast.makeText(context, "Factura exportada en Descargas", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al exportar la factura", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}


fun addTableRow(table: Table, label: String, value: String, fontBold: com.itextpdf.kernel.font.PdfFont, fontNormal: com.itextpdf.kernel.font.PdfFont) {
    table.addCell(Cell().add(Paragraph(label).setFont(fontBold).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
    table.addCell(Cell().add(Paragraph(value).setFont(fontNormal).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER))
}

fun addTableHeader(table: Table, label: String, fontBold: com.itextpdf.kernel.font.PdfFont, fontNormal: com.itextpdf.kernel.font.PdfFont) {
    table.addCell(Cell().add(Paragraph(label).setFont(fontBold).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setFontSize(14f))
    table.addCell(Cell().add(Paragraph("").setFont(fontBold).setTextAlignment(TextAlignment.LEFT)).setBorder(Border.NO_BORDER).setFontSize(14f))
}



