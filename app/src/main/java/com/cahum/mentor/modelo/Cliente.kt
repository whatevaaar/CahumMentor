package com.cahum.mentor.modelo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Cliente(
    val uid: String ="",
    val nombre: String = "",
    val tieneCv: Boolean = false,
    val perfilLinked: Boolean = false,
    val simulador: Boolean = false,
    val psicometria: Boolean = false,
    val estrategia: Boolean = false,
    var uidMentor: String =""
): Parcelable