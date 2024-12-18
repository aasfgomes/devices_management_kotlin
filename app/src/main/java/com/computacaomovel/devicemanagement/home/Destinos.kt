package com.computacaomovel.devicemanagement.home

import androidx.annotation.DrawableRes
import com.computacaomovel.devicemanagement.R

// Esta classe selada (sealed class) representa os diferentes ecrãs (destinos) da aplicação.
// Cada objeto de Destino define uma rota, um ícone, e um título.
sealed class Destino(val route: String, @DrawableRes val icon: Int, val title: String) {
    object Ecra01 : Destino(route = "ecra01", icon = R.drawable.list, title = "Home")
    object Ecra02 : Destino(route = "ecra02", icon = R.drawable.create, title = "Logs")
    object Ecra03 : Destino(route = "ecra03", icon = R.drawable.delete, title = "Perfil")


    // Companion object que cria uma lista de todos os destinos disponíveis
    companion object {
        val toList = listOf(Ecra01, Ecra02, Ecra03)
    }
}
