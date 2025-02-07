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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun GraficoGananciasPerdidas() {
    val entries = listOf(
        BarEntry(0f, 300f), // Ganancias (ajustamos a 0f para que las etiquetas coincidan)
        BarEntry(1f, 150f)  // Pérdidas
    )

    val dataSet = BarDataSet(entries, "").apply {
        colors = listOf(
            Color.parseColor("#4CAF50"), 
            Color.parseColor("#F44336")
        )
        valueTextColor = Color.BLACK
        valueTextSize = 14f

        // Bordes redondeados en las barras
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

                    // Configuración del eje X (etiquetas personalizadas)
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f // Intervalos fijos entre valores en el eje X
                        textColor = Color.BLACK
                        textSize = 12f
                        valueFormatter = IndexAxisValueFormatter(listOf("Ganancias", "Pérdidas")) // Etiquetas correctas
                    }

                    // Configuración del eje izquierdo (Y)
                    axisLeft.apply {
                        textColor = Color.BLACK
                        textSize = 12f
                        setDrawGridLines(true)
                    }

                    // Deshabilitar el eje derecho (Y)
                    axisRight.isEnabled = false

                    // Configuración general del gráfico
                    description.isEnabled = false // Ocultar descripción predeterminada del gráfico

                    legend.isEnabled = false // Ocultar la leyenda

                    setFitBars(true) // Ajustar las barras al espacio disponible

                    animateY(1500) // Animación vertical al cargar el gráfico

                    setExtraOffsets(10f, 10f, 10f, 10f) // Espaciado adicional para evitar cortes visuales en los bordes del gráfico.

                }
            }
        )
    }
}
