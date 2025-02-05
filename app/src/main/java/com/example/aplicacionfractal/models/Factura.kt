package com.example.aplicacionfractal.models
import java.time.LocalDateTime

data class Factura(
    val IVA: Double,
    val baseImponible: Double,
    val emisorId: String,
    val fechaEmision: LocalDateTime,
    val nFactura: Int,
    val total: Double
)
