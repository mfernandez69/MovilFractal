package com.example.aplicacionfractal.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.aplicacionfractal.data.models.Emisor
import com.example.aplicacionfractal.data.models.Factura
import com.example.aplicacionfractal.data.models.Receptor
import com.example.aplicacionfractal.ui.theme.ColorPrimario
import com.example.aplicacionfractal.ui.theme.ColorSecundario
import com.example.aplicacionfractal.viewModels.FacturaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarFactura(
    navController: NavHostController,
    numeroFactura: String
) {
//    Log.d("PantallaEditarFactura", "PantallaEditarFactura started with numeroFactura = $numeroFactura")
    val facturaViewModel: FacturaViewModel = viewModel()
    var facturaExistente by remember { mutableStateOf<Factura?>(null) }
    var emisor by remember { mutableStateOf<Emisor?>(null) }
    var receptor by remember { mutableStateOf<Receptor?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(numeroFactura) {
        facturaExistente = facturaViewModel.obtenerFacturaPorNumero(numeroFactura)
        facturaExistente?.let { factura ->
            emisor = factura.emisorId?.let { facturaViewModel.obtenerEmisorPorId(it.id) }
            receptor = factura.receptorId?.let { facturaViewModel.obtenerReceptorPorId(it.id) }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Editar Factura") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ColorPrimario),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                }
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
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                facturaExistente?.let { factura ->
                    ContentEditView(navController, factura, emisor, receptor, facturaViewModel)
                } ?: Text("No se pudo cargar la factura", fontSize = 18.sp, modifier = Modifier.padding(16.dp))
            }
        }
    }
}


@Composable
fun ContentEditView(
    navController: NavController,
    factura: Factura,
    emisorInicial: Emisor?,
    receptorInicial: Receptor?,
    facturaViewModel: FacturaViewModel
) {
    var numFactura by remember { mutableStateOf(factura.nFactura) }
    var baseImponible by remember { mutableStateOf(factura.baseImponible.toString()) }
    var porcentajeIVA by remember { mutableStateOf(((factura.IVA / factura.baseImponible) * 100).roundToInt()) }
    var total by remember { mutableStateOf(factura.total) }

    var emitida by remember { mutableStateOf(factura.emitida) }
    var recibida by remember { mutableStateOf(!factura.emitida) }

    // Emisor
    var empresaEmisor by remember { mutableStateOf(emisorInicial?.empresa ?: "") }
    var nifEmisor by remember { mutableStateOf(emisorInicial?.nif ?: "") }
    var direccionEmisor by remember { mutableStateOf(emisorInicial?.direccionEmisor ?: "") }

    // Receptor
    var clienteReceptor by remember { mutableStateOf(receptorInicial?.cliente ?: "") }
    var cifReceptor by remember { mutableStateOf(receptorInicial?.cif ?: "") }
    var direccionReceptor by remember { mutableStateOf(receptorInicial?.direccionReceptor ?: "") }

    val ivaOptions = listOf(Pair(0, "0%"), Pair(4, "4%"), Pair(10, "10%"), Pair(21, "21%"))
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        OutlinedTextField(
//            value = numFactura,
//            onValueChange = { if (it.all { char -> char.isDigit() }) numFactura = it },
//            label = { Text(text = "Número de Factura") },
//            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
//        )

        Text(
            text = "Numero de factura: $numFactura",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Emitida"
                )
                Checkbox(
                    checked = emitida,
                    onCheckedChange = {
                        emitida = it
                        recibida = !it
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ColorPrimario
                    )
                )
            }
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "Recibida"
                )
                Checkbox(
                    checked = recibida,
                    onCheckedChange = {
                        emitida = !it
                        recibida = it
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ColorPrimario
                    )
                )
            }

        }

        // Campos para Emisor
        Text(text = "Datos del Emisor", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 30.dp))

        OutlinedTextField(value = nifEmisor, onValueChange = { nifEmisor = it }, label = { Text(text = "NIF del emisor") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = empresaEmisor, onValueChange = { empresaEmisor = it }, label = { Text(text = "Empresa emisora") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = direccionEmisor, onValueChange = { direccionEmisor = it }, label = { Text(text = "Dirección del emisor") },
            modifier = Modifier.fillMaxWidth())

        // Campos para Receptor
        Text(text = "Datos del Receptor", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 30.dp))

        OutlinedTextField(value = clienteReceptor, onValueChange = { clienteReceptor = it }, label = { Text(text = "Cliente receptor") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = cifReceptor, onValueChange = { cifReceptor = it }, label = { Text(text = "CIF del receptor") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(value = direccionReceptor, onValueChange = { direccionReceptor = it }, label = { Text(text = "Dirección del receptor") },
            modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = baseImponible,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) {
                    baseImponible = it
                    recalcularTotal(baseImponible.toDoubleOrNull() ?: 0.0, porcentajeIVA) { newTotal ->
                        total = newTotal
                    }
                }
            },
            label = { Text(text = "Base imponible") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
        )

        Text(
            text = "Porcentaje de IVA",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ivaOptions.forEach { option ->
                Button(
                    onClick = {
                        porcentajeIVA = option.first
                        recalcularTotal(baseImponible.toDoubleOrNull() ?: 0.0, porcentajeIVA) { newTotal ->
                            total = newTotal
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (porcentajeIVA == option.first) ColorPrimario else ColorSecundario,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                ) {
                    Text(text = option.second)
                }
            }
        }

        Text(
            text = "Total: ${String.format("%.2f", total)} €",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp, vertical = 16.dp)
        )

        Button(
            onClick = {
                val baseImponibleDouble = baseImponible.toDoubleOrNull() ?: 0.0
                val iva = baseImponibleDouble * (porcentajeIVA / 100.0)

                val emisorActualizado = Emisor(direccionEmisor, empresaEmisor, nifEmisor)
                val receptorActualizado = Receptor(direccionReceptor, clienteReceptor, cifReceptor)

                val facturaActualizada = factura.copy(
                    nFactura = numFactura,
                    baseImponible = baseImponibleDouble,
                    emitida = emitida,
                    IVA = iva,
                    total = total
                )

                CoroutineScope(Dispatchers.Main).launch {
                    facturaViewModel.actualizarFactura(facturaActualizada, emisorActualizado, receptorActualizado)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = ColorSecundario, contentColor = Color.White),
            modifier = Modifier.padding(vertical = 16.dp)
        ) {
            Text(text = "Actualizar Factura", fontSize = 16.sp)
        }
    }
}
fun recalcularTotal(baseImponible: Double, porcentajeIVA: Int, onTotalCalculated: (Double) -> Unit) {
    val iva = baseImponible * (porcentajeIVA / 100.0)
    val total = baseImponible + iva
    onTotalCalculated(total)
}