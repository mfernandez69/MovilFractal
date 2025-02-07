package com.example.aplicacionfractal.screens

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.aplicacionfractal.utils.MenuPrincipal
import com.example.aplicacionfractal.utils.TabMultiple
import com.example.aplicacionfractal.viewModels.GastosViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.graphics.Color as ComposeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaGastos(
    navController: NavHostController,
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
                    title = { Text("Graficos") },
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
                Graficos()
            }
        }
    }
}
@Composable
fun Graficos(){
    LazyColumn (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){
        item {
            GraficoGananciasPerdidas()
        }
    }
}
@Composable
fun GraficoGananciasPerdidas(viewModel: GastosViewModel = GastosViewModel()) {
    // Observar las ganancias y pérdidas desde el ViewModel
    val gananciasPerdidas by viewModel.gananciasPerdidas.collectAsState()

    // Extraer valores de ganancias y pérdidas
    val (ganancias, perdidas) = gananciasPerdidas

    // Mostrar un indicador de carga si los datos aún no están listos
    if (ganancias == 0.0 && perdidas == 0.0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Crear las entradas del gráfico basadas en los valores observados
    val entries = listOf(
        BarEntry(0f, ganancias.toFloat()), // Ganancias
        BarEntry(1f, perdidas.toFloat())   // Pérdidas
    )

    val dataSet = BarDataSet(entries, "").apply {
        colors = listOf(
            Color.parseColor("#4CAF50"), // Color para ganancias
            Color.parseColor("#F44336") // Color para pérdidas
        )
        valueTextColor = Color.BLACK
        valueTextSize = 14f

        setDrawValues(true)
        barShadowColor = Color.LTGRAY // Sombra detrás de las barras (opcional)
    }

    val barData = BarData(dataSet).apply {
        barWidth = 0.4f // Ajustar el ancho de las barras
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(6.dp, shape = RoundedCornerShape(16.dp))
            .background(ComposeColor.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ComposeColor.White),
            factory = { context ->
                BarChart(context).apply {
                    data = barData

                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f // Intervalos fijos entre valores en el eje X
                        textColor = Color.BLACK
                        textSize = 12f
                        valueFormatter =
                            IndexAxisValueFormatter(listOf("Ganancias", "Pérdidas")) // Etiquetas correctas
                    }

                    axisLeft.apply {
                        textColor = Color.BLACK
                        textSize = 12f
                        setDrawGridLines(true)
                    }

                    axisRight.isEnabled = false

                    description.isEnabled = false

                    legend.isEnabled = false

                    setFitBars(true)

                    animateY(1500)

                    setExtraOffsets(10f, 10f, 10f, 10f)
                }
            }
        )
    }
}


