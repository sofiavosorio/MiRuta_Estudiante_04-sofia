// CARPETA navigation: nombres de las pantallas (rutas) y el NavHost que cambia entre ellas.
package com.example.miruta.navigation

// object con las rutas como constantes: así no se escribe el texto a mano en cada lugar
// y se evitan errores de escritura.
object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ADD = "add"
    const val DETAIL = "detail/{id}"   // {id} = parámetro que viaja en la ruta
    const val SETTINGS = "settings"
    // Arma la ruta real con el id: detalle(3) -> "detail/3"
    fun detalle(id:Int) = "detail/$id"
}
