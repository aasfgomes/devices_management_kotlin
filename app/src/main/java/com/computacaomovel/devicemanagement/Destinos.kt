package com.computacaomovel.devicemanagement

import androidx.annotation.DrawableRes

sealed class Destino(val route: String, @DrawableRes val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.list, title = "Home")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.create, title = "Logs")
    object Ecra03 : Destino(route = "ecra03", icon = R.drawable.delete, title = "Perfil")

    companion object {
        val toList = listOf(Ecra01, Ecra02, Ecra03)
    }
}