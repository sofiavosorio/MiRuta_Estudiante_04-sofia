// CARPETA ui/theme: colores, tema y tipografía de la app.
package com.example.miruta.ui.theme

import androidx.compose.ui.graphics.Color

// Color(0xFF......) = color en código hexadecimal. FF = totalmente visible (sin transparencia).

// Colores principales (modo claro)
val AzulPrincipal = Color(0xFF2455A4)   // botones y elementos principales
val VerdeAccion = Color(0xFF087F72)     // color secundario y "Completada"
val RojoPrioridad = Color(0xFFC24135)   // prioridad Alta y errores

// Colores en modo oscuro (más claros para que se lean sobre fondo oscuro).
val AzulClaro = Color(0xFFA9C7FF)
val VerdeClaro = Color(0xFF7FD8C9)

// Fondos de las tarjetas según la prioridad (1 = Baja, 2 = Media, 3 = Alta).
val PrioridadBaja = Color(0xFFD6F2EC)   // verde suave
val PrioridadMedia = Color(0xFFFFE9B8)  // amarillo suave
val PrioridadAlta = Color(0xFFF8D3CF)   // rojo suave
val Amarillo = Color(0xFFB07A00)        // texto de prioridad Media
