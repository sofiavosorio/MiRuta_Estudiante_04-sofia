package com.example.miruta.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tema de la app. Recibe "oscuro" (viene de DataStore) y escoge la paleta clara u oscura.
// Todas las pantallas usan MaterialTheme.colorScheme.primary, .error, .background, etc.,
// así cambian solas cuando se activa el modo oscuro.
@Composable
fun MiRutaTheme(oscuro:Boolean, content:@Composable ()->Unit) {
    // TODO-06: enriquecer colores de prioridades y estilos.
    val colores = if (oscuro) darkColorScheme(primary = AzulClaro, secondary = VerdeClaro, error = PrioridadAlta)
                  else lightColorScheme(primary = AzulPrincipal, secondary = VerdeAccion, error = RojoPrioridad)
    MaterialTheme(colorScheme = colores, typography = MiRutaTypography, content = content)
}

// Funciones normales (NO son @Composable): reciben la prioridad y devuelven un color o un texto.
// Se usan en ActivityCard, AddActivityScreen y DetailScreen para no repetir el mismo "when".

// Fondo de la tarjeta según la prioridad.
fun colorPrioridad(prioridad: Int): Color {
    return when (prioridad) {
        3 -> PrioridadAlta
        2 -> PrioridadMedia
        else -> PrioridadBaja
    }
}

// Color del texto que indica la prioridad.
fun colorTextoPrioridad(prioridad: Int): Color {
    return when (prioridad) {
        3 -> RojoPrioridad
        2 -> Amarillo
        else -> VerdeAccion
    }
}

// Convierte el número de prioridad en texto: 1 -> "Baja", 2 -> "Media", 3 -> "Alta".
fun nombrePrioridad(prioridad: Int): String {
    return when (prioridad) {
        3 -> "Alta"
        2 -> "Media"
        else -> "Baja"
    }
}
