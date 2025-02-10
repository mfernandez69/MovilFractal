package com.example.aplicacionfractal.utils

import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler


class RoundedBarChartRenderer(
    chart: BarChart,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : BarChartRenderer(chart, animator, viewPortHandler) {

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        try {
            val buffer = mBarBuffers[index]
            val paint = mRenderPaint

            for (j in buffer.buffer.indices step 4) {
                val left = buffer.buffer[j]
                val top = buffer.buffer[j + 1]
                val right = buffer.buffer[j + 2]
                val bottom = buffer.buffer[j + 3]

                // Ajustar el radio al tamaño del rectángulo
                val width = right - left
                val height = bottom - top
                val cornerRadius = minOf(20f, width / 2, height / 2)

                // Define un array con los radios de las esquinas (8 valores)
                val radii =
                    floatArrayOf(cornerRadius, cornerRadius, cornerRadius, cornerRadius,
                        0f,
                        0f,
                        0f,
                        0f)

                // Dibuja un rectángulo con esquinas redondeadas
                val path = Path()
                path.addRoundRect(
                    RectF(left, top.coerceAtMost(bottom), right.coerceAtLeast(left), bottom),
                    radii,
                    Path.Direction.CW
                )
                c.drawPath(path, paint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
