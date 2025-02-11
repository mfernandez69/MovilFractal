package com.example.aplicacionfractal.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarFactura(
    navController: NavHostController,
    numeroFactura: Int
) {
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
    var numFactura by remember { mutableStateOf(factura.nFactura.toString()) }
    var baseImponible by remember { mutableStateOf(factura.baseImponible.toString()) }

    // Emisor
    var empresaEmisor by remember { mutableStateOf(emisorInicial?.empresa ?: "") }
    var nifEmisor by remember { mutableStateOf(emisorInicial?.nif ?: "") }
    var direccionEmisor by remember { mutableStateOf(emisorInicial?.direccionEmisor ?: "") }

    // Receptor
    var clienteReceptor by remember { mutableStateOf(receptorInicial?.cliente ?: "") }
    var cifReceptor by remember { mutableStateOf(receptorInicial?.cif ?: "") }
    var direccionReceptor by remember { mutableStateOf(receptorInicial?.direccionReceptor ?: "") }

    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = numFactura,
            onValueChange = { if (it.all { char -> char.isDigit() }) numFactura = it },
            label = { Text(text = "Número de Factura") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

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

        OutlinedTextField(value = baseImponible, onValueChange = { if (it.all { char -> char.isDigit() }) baseImponible = it },
            label = { Text(text = "Base imponible") }, modifier = Modifier.fillMaxWidth())

        Button(
            onClick = {
                val baseImponibleDouble = baseImponible.toDoubleOrNull() ?: 0.0
                val iva = baseImponibleDouble * 0.21 // 21% de IVA
                val total = baseImponibleDouble + iva

                val emisorActualizado = Emisor(direccionEmisor, empresaEmisor, nifEmisor)
                val receptorActualizado = Receptor(direccionReceptor, clienteReceptor, cifReceptor)

                val facturaActualizada = factura.copy(
                    nFactura = numFactura.toInt(),
                    baseImponible = baseImponibleDouble,
                    IVA = iva,
                    total = total
                )

                // Usa un coroutineScope para esperar la operación
                CoroutineScope(Dispatchers.Main).launch {
                    facturaViewModel.actualizarFactura(facturaActualizada, emisorActualizado, receptorActualizado)
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.textButtonColors(containerColor = ColorSecundario, contentColor = Color.White)
        ) {
            Text(text = "Actualizar Factura", fontSize = 16.sp)
        }
    }
}
