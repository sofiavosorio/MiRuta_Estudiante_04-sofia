// CARPETA ui/screens: las 5 pantallas de la app. Pantalla 1: bienvenida (solo el primer uso).
package com.example.miruta.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// onTerminar = lo que pasa al tocar "Empezar" (AppNavigation guarda en DataStore y abre Home).
@Composable
fun OnboardingScreen(onTerminar:()->Unit) {
    // pagina: estado LOCAL de esta pantalla (0, 1 o 2). Al cambiar, la pantalla se vuelve a dibujar.
    // remember = guarda el valor entre recomposiciones. mutableIntStateOf = estado de tipo Int.
    var pagina by remember { mutableIntStateOf(0) }
    // Contenido de las 3 páginas: la posición [pagina] escoge cuál mostrar.
    val titulos = listOf("Bienvenido a MiRuta", "Organiza tus actividades", "Personaliza la experiencia")
    val descripciones = listOf("Organiza tu día: entregas, lecturas, talleres y compromisos en un solo lugar.", "Consulta pendientes y prioridades, y marca lo que ya terminaste.", "Configura tu nombre y el modo oscuro; se guardan aunque cierres la app.")
    // TODO-08: ilustración/indicador visual y botón anterior.
    // Íconos vistos en clase: Home, Add (agregar actividades) y Star (personalizar).
    val iconos = listOf(Icons.Default.Home, Icons.Default.Add, Icons.Default.Star)
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement=Arrangement.Center, horizontalAlignment=Alignment.CenterHorizontally) {
        // Ilustración de cada página (un ícono grande con el color principal del tema).
        Icon(imageVector=iconos[pagina], contentDescription=titulos[pagina], tint=MaterialTheme.colorScheme.primary, modifier=Modifier.size(120.dp))
        Spacer(Modifier.height(24.dp))
        Text(titulos[pagina], style=MaterialTheme.typography.headlineMedium, textAlign=TextAlign.Center)
        Spacer(Modifier.height(16.dp)); Text(descripciones[pagina], textAlign=TextAlign.Center); Spacer(Modifier.height(28.dp))
        // Indicador: una barrita por página, la actual se pinta con el color principal
        // (igual que PagerLindicator del proyecto Onboarding). repeat(3) dibuja 3 indicadores.
        Row(horizontalArrangement=Arrangement.Center) {
            repeat(times = 3) { Indicador(seleccionado = it == pagina) }
        }
        Spacer(Modifier.height(28.dp))
        // Si hay botón Anterior, los dos botones se separan a los lados; si no, Siguiente queda centrado.
        Row(Modifier.fillMaxWidth(), horizontalArrangement=if (pagina > 0) Arrangement.SpaceBetween else Arrangement.Center) {
            // El botón Anterior solo aparece desde la segunda página.
            if (pagina > 0) OutlinedButton(onClick={ pagina = pagina - 1 }) { Text("Anterior") }
            // En las páginas 0 y 1 avanza; en la página 2 dice "Empezar" y termina el onboarding.
            Button(onClick={ if(pagina<2) pagina++ else onTerminar() }) { Text(if(pagina==2) "Empezar" else "Siguiente") }
        }
    }
}

// Una barrita del indicador. clip(CircleShape) redondea las puntas.
@Composable
fun Indicador(seleccionado: Boolean) {
    Box(
        modifier = Modifier
            .padding(all = 5.dp)
            .height(10.dp)
            .width(25.dp)
            .clip(CircleShape)
            .background(color = if (seleccionado) MaterialTheme.colorScheme.primary else Color.Gray)
    )
}
