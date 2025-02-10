package com.example.aplicacionfractal.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
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
    var porcentajeiva by remember { mutableIntStateOf(21) }
    var iva0 by remember { mutableStateOf(false) }
    var iva10 by remember { mutableStateOf(false) }
    var iva21 by remember { mutableStateOf(true) }

    var emitida by remember { mutableStateOf(true) }
    var recibida by remember { mutableStateOf(false) }
    var numFactura by remember { mutableStateOf("") }
    var baseImponible by remember { mutableStateOf("") }

    // Emisor
    var empresaEmisor by remember { mutableStateOf("") }
    var nifEmisor by remember { mutableStateOf("") }
    var direccionEmisor by remember { mutableStateOf("") }

    // Receptor
    var clienteReceptor by remember { mutableStateOf("") }
    var cifReceptor by remember { mutableStateOf("") }
    var direccionReceptor by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
                enabled = true,
                flingBehavior = null,
                reverseScrolling = false
            )
            .requiredHeight(1100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        OutlinedTextField(
            value = numFactura,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    numFactura = it
                }
            },
            label = { Text(text = "Número de Factura") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

        // Campos para Emisor
        Text(
            text = "Datos del Emisor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        OutlinedTextField(
            value = nifEmisor,
            onValueChange = { nifEmisor = it },
            label = { Text(text = "NIF del emisor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = empresaEmisor,
            onValueChange = { empresaEmisor = it },
            label = { Text(text = "Empresa emisora") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = direccionEmisor,
            onValueChange = { direccionEmisor = it },
            label = { Text(text = "Dirección del emisor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        // Campos para Receptor
        Text(
            text = "Datos del Receptor",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        )

        OutlinedTextField(
            value = clienteReceptor,
            onValueChange = { clienteReceptor = it },
            label = { Text(text = "Cliente receptor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = cifReceptor,
            onValueChange = { cifReceptor = it },
            label = { Text(text = "CIF del receptor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = direccionReceptor,
            onValueChange = { direccionReceptor = it },
            label = { Text(text = "Dirección del receptor") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Next)
        )

        Text(
            text = "Porcentaje de iva",
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
        ) {
            Row(
                modifier = Modifier
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "0%"
                )
                Checkbox(
                    checked = iva0,
                    onCheckedChange = {
                        iva0 = it
                        iva10 = false
                        iva21 = false
                        porcentajeiva = 0
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
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "10%"
                )
                Checkbox(
                    checked = iva10,
                    onCheckedChange = {
                        iva10 = it
                        iva0 = false
                        iva21 = false
                        porcentajeiva = 10
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
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "21%"
                )
                Checkbox(
                    checked = iva21,
                    onCheckedChange = {
                        iva21 = it
                        iva0 = false
                        iva10 = false
                        porcentajeiva = 21
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = ColorPrimario
                    )
                )
            }
        }

        OutlinedTextField(
            value = baseImponible,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    baseImponible = it
                }
            },
            label = { Text(text = "Base imponible") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(bottom = 15.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next)
        )

        Button(
            onClick = {
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
                    nFactura = numFactura.toInt(),
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
            },
            colors = ButtonDefaults.textButtonColors(
                containerColor = ColorSecundario,
                contentColor = Color.White
            )
        ) {
            Text(text = "Agregar Factura", fontSize = 16.sp)
        }

        //Spacer(modifier = Modifier.size(150.dp))
    }
}
