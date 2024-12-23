package com.computacaomovel.devicemanagement

import androidx.annotation.DrawableRes

sealed class Destino(val route: String, val title: String) {
    object Ecra01 : Destino(route = "ecra01", title = "Home")
    object Ecra02 : Destino(route = "ecra02",title = "Logs")
    object Ecra03 : Destino(route = "ecra03",title = "Perfil")

    companion object {
        val toList = listOf(Ecra01, Ecra02, Ecra03)
    }
}