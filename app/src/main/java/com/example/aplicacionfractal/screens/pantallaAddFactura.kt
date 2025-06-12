package com.example.aplicacionfractal.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.aplicacionfractal.viewModels.EmisorViewModel
import com.example.aplicacionfractal.viewModels.FacturaViewModel
import com.example.aplicacionfractal.viewModels.ReceptorViewModel
import com.google.firebase.Timestamp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAddFactura(
    navController: NavHostController,
    facturaViewModel: FacturaViewModel = viewModel(),
    emisorViewModel: EmisorViewModel = viewModel(),
    receptorViewModel: ReceptorViewModel = viewModel()
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Agregar Factura")
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ColorPrimario
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
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
            ContentAddView(navController, facturaViewModel, emisorViewModel, receptorViewModel)
        }
    }

}

@Composable
fun ContentAddView(
    navController: NavController,
    facturaViewModel: FacturaViewModel,
    emisorViewModel: EmisorViewModel,
    receptorViewModel: ReceptorViewModel
) {
    val numFactura = facturaViewModel.generarNumeroFactura()
    var currentStep by remember { mutableIntStateOf(1) }

    // Step 1: Datos generales
    var emitida by remember { mutableStateOf(true) }
    var recibida by remember { mutableStateOf(false) }
    var baseImponible by remember { mutableStateOf("") }
    val ivaOptions = listOf(Pair(0, "Exento"), Pair(4, "Superreducido"), Pair(10, "Reducido"), Pair(21, "General"))
    var porcentajeiva by remember { mutableIntStateOf(21) }

    // Step 2: Emisor
    var nifEmisor by remember { mutableStateOf("") }
    var empresaEmisor by remember { mutableStateOf("") }
    var direccionEmisor by remember { mutableStateOf("") }

    // Step 3: Receptor
    var cifReceptor by remember { mutableStateOf("") }
    var clienteReceptor by remember { mutableStateOf("") }
    var direccionReceptor by remember { mutableStateOf("") }

    val isStep1Valid = baseImponible.isNotBlank()
    val isStep2Valid = nifEmisor.isNotBlank() && empresaEmisor.isNotBlank() && direccionEmisor.isNotBlank()
    val isStep3Valid = cifReceptor.isNotBlank() && clienteReceptor.isNotBlank() && direccionReceptor.isNotBlank()

    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            (1..3).forEach { step ->
                val canGo = when (step) {
                    1 -> true
                    2 -> isStep1Valid
                    3 -> isStep1Valid && isStep2Valid
                    else -> false
                }
                Text(
                    text = "$step",
                    fontWeight = if (currentStep == step) FontWeight.Bold else if (canGo) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (currentStep == step) Color.Black else if (canGo) ColorPrimario else Color.Gray,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .clickable(enabled = canGo && currentStep != step) { currentStep = step }
                )
                if (step != 3) {
                    Text(
                        text = "—",
                        color = Color.Gray,
                        fontSize = 20.sp
                    )
                }
            }
        }
        Text(
            text = "Numero de factura: $numFactura",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        when (currentStep) {
            1 -> {
                Text(
                    text = "Datos generales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                // Emitida/Recibida
                Row(
                    modifier = Modifier
                        .padding(horizontal = 30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(text = "Emitida")
                        Checkbox(
                            checked = emitida,
                            onCheckedChange = {
                                emitida = it
                                recibida = !it
                            },
                            colors = CheckboxDefaults.colors(checkedColor = ColorPrimario)
                        )
                    }
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(text = "Recibida")
                        Checkbox(
                            checked = recibida,
                            onCheckedChange = {
                                emitida = !it
                                recibida = it
                            },
                            colors = CheckboxDefaults.colors(checkedColor = ColorPrimario)
                        )
                    }
                }
                // Base imponible
                OutlinedTextField(
                    value = baseImponible,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() }) baseImponible = it
                    },
                    label = { Text("Base imponible") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
                )
                // Porcentaje de IVA
                Text(
                    text = "Porcentaje de IVA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                Row(
                    modifier = Modifier.padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ivaOptions.forEach { option ->
                        Button(
                            onClick = { porcentajeiva = option.first },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (porcentajeiva == option.first) ColorPrimario else ColorSecundario,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                        ) {
                            Text(text = "${option.first}%")
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = { currentStep = 2 },
                    enabled = isStep1Valid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorSecundario,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(2.dp)
                ) {
                    Text("Siguiente")
                }
            }
            2 -> {
                Text(
                    text = "Datos del emisor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                OutlinedTextField(
                    value = nifEmisor,
                    onValueChange = { nifEmisor = it },
                    label = { Text("NIF del emisor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = empresaEmisor,
                    onValueChange = { empresaEmisor = it },
                    label = { Text("Empresa emisora") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = direccionEmisor,
                    onValueChange = { direccionEmisor = it },
                    label = { Text("Dirección del emisor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { currentStep = 1 },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(2.dp)
                    ) {
                        Text("Anterior")
                    }
                    Button(
                        onClick = { currentStep = 3 },
                        enabled = isStep2Valid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorSecundario,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(2.dp)
                    ) {
                        Text("Siguiente")
                    }
                }
            }
            3 -> {
                Text(
                    text = "Datos del receptor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                )
                OutlinedTextField(
                    value = cifReceptor,
                    onValueChange = { cifReceptor = it },
                    label = { Text("CIF del receptor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = clienteReceptor,
                    onValueChange = { clienteReceptor = it },
                    label = { Text("Cliente receptor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = direccionReceptor,
                    onValueChange = { direccionReceptor = it },
                    label = { Text("Dirección del receptor") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 8.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { currentStep = 2 },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(2.dp)
                    ) {
                        Text("Anterior")
                    }
                    Button(
                        onClick = {
                            crearFactura(
                                navController = navController,
                                facturaViewModel = facturaViewModel,
                                emisorViewModel = emisorViewModel,
                                receptorViewModel = receptorViewModel,
                                direccionEmisor = direccionEmisor,
                                empresaEmisor = empresaEmisor,
                                nifEmisor = nifEmisor,
                                direccionReceptor = direccionReceptor,
                                clienteReceptor = clienteReceptor,
                                cifReceptor = cifReceptor,
                                numFactura = numFactura,
                                baseImponible = baseImponible,
                                emitida = emitida,
                                porcentajeiva = porcentajeiva
                            )
                        },
                        enabled = isStep3Valid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorSecundario,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(2.dp)
                    ) {
                        Text("Agregar Factura")
                    }
                }
            }
        }
    }
}

fun crearFactura(
    navController: NavController,
    facturaViewModel: FacturaViewModel,
    emisorViewModel: EmisorViewModel,
    receptorViewModel: ReceptorViewModel,
    direccionEmisor: String,
    empresaEmisor: String,
    nifEmisor: String,
    direccionReceptor: String,
    clienteReceptor: String,
    cifReceptor: String,
    numFactura: String,
    baseImponible: String,
    emitida: Boolean,
    porcentajeiva: Int
){
    val baseImponibleDouble = baseImponible.toDoubleOrNull() ?: 0.0
    val iva = baseImponibleDouble * porcentajeiva / 100 // % IVA
    val total = baseImponibleDouble + iva

    val emisor = Emisor(
        direccionEmisor = direccionEmisor,
        empresa = empresaEmisor,
        nif = nifEmisor
    )

    val receptor = Receptor(
        direccionReceptor = direccionReceptor,
        cliente = clienteReceptor,
        cif = cifReceptor
    )

    val factura = Factura(
        nFactura = numFactura,
        fechaEmision = Timestamp(Date().toInstant()),
        baseImponible = baseImponibleDouble,
        IVA = iva,
        total = total,
        emitida = emitida
    )

    emisorViewModel.agregarEmisor(emisor)
    receptorViewModel.agregarReceptor(receptor)
    facturaViewModel.agregarFactura(factura = factura, emisor = emisor, receptor = receptor)
    navController.navigate("pantallaPrincipal")
}
