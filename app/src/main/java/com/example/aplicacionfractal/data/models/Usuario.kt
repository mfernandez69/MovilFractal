package com.example.aplicacionfractal.data.models

import com.google.firebase.Timestamp

data class Usuario(
    val email: String = "",
    val role: String = "admin",
    val createdAt: Timestamp = Timestamp.now()
)