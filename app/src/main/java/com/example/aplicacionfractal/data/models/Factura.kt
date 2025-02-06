package com.example.aplicacionfractal.data.models
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.time.LocalDateTime

@IgnoreExtraProperties
data class Factura(
    @get:PropertyName("IVA") @set:PropertyName("IVA")
    var IVA: Double = 0.0,

    @get:PropertyName("baseImponible") @set:PropertyName("baseImponible")
    var baseImponible: Double = 0.0,

    @get:PropertyName("emisorId") @set:PropertyName("emisorId")
    var emisorId: DocumentReference? = null,

    @get:PropertyName("receptorId") @set:PropertyName("receptorId")
    var receptorId: DocumentReference? = null,

    @get:PropertyName("fechaEmision") @set:PropertyName("fechaEmision")
    var fechaEmision: Timestamp? = null,

    @get:PropertyName("nFactura") @set:PropertyName("nFactura")
    var nFactura: Int = 0,

    @get:PropertyName("total") @set:PropertyName("total")
    var total: Double = 0.0
)

