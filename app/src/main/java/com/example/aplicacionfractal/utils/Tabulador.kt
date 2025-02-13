package com.example.aplicacionfractal.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.aplicacionfractal.screens.ListadoFacturas
import com.example.aplicacionfractal.screens.ListadoFacturasEmitidas
import com.example.aplicacionfractal.screens.ListadoFacturasRecibidas
import com.example.aplicacionfractal.ui.theme.ColorPrimario
import com.example.aplicacionfractal.viewModels.FacturaViewModel
import kotlinx.coroutines.launch

@Composable
fun TabMultiple(facturaViewModel: FacturaViewModel,navController: NavHostController){
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        contentColor = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        indicator = { tabPositions ->
            SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .height(4.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = ColorPrimario
            )
        }
    ) {
        Tab(
            selected = pagerState.currentPage == 0,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(0)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Recibidas",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Tab(
            selected = pagerState.currentPage == 1,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(1)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Emitidas",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Tab(
            selected = pagerState.currentPage == 2,
            onClick = {
                scope.launch {
                    pagerState.animateScrollToPage(2)
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Todos",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> ListadoFacturasRecibidas(facturaViewModel,navController)
            1 -> ListadoFacturasEmitidas(facturaViewModel,navController)
            2 -> ListadoFacturas(facturaViewModel,navController)
        }
    }
}