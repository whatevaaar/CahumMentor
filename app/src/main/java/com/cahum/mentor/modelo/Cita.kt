package com.cahum.mentor.modelo

class Cita(
    val uidMentor: String = "",
    val uidCliente: String = "",
    val fechaSeleccionada: String = "",
    val horaDeInicio: String = "",
    var todoElDia: Boolean = false,
    var duracionEnMinutos: Int = 0
)