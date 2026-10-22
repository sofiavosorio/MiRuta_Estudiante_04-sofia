// Pantalla 2: INICIO. Saludo con el nombre, botones, filtro de estados (reto MOV1-04) y lista de actividades.
package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miruta.model.FiltroEstado
import com.example.miruta.ui.components.ActivityCard
import com.example.miruta.ui.theme.AzulPrincipal
import com.example.miruta.viewmodel.ActivityViewModel
import com.example.miruta.viewmodel.PreferencesViewModel

// Recibe los dos ViewModel y 3 funciones de navegación (las define AppNavigation).
// onDetalle recibe el id de la actividad tocada.
@Composable
fun HomeScreen(vm:ActivityViewModel, prefs:PreferencesViewModel, onAgregar:()->Unit, onDetalle:(Int)->Unit, onAjustes:()->Unit) {
    // collectAsStateWithLifecycle: convierte el StateFlow del ViewModel en estado de Compose.
    // Cuando el valor cambia en el ViewModel, esta pantalla se vuelve a dibujar sola.
    val actividades by vm.actividades.collectAsStateWithLifecycle()
    // Reto MOV1-04: la pantalla observa el filtro y la lista filtrada del ViewModel.
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val filtradas by vm.actividadesFiltradas.collectAsStateWithLifecycle()
    // Nombre guardado en DataStore (por defecto "Estudiante").
    val nombre by prefs.nombre.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Hola, $nombre", style=MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { Button(onClick=onAgregar) { Text("Nueva") }; OutlinedButton(onClick=onAjustes) { Text("Preferencias") } }
        Spacer(Modifier.height(12.dp))
        // TODO-09: desarrollar la interfaz solicitada por su variante.
        // Selector de estados: 3 botones en una fila; el elegido se pinta de azul
        // (igual que los botones Regular / VIP / Especial del proyecto tumovil).
        // Al tocar uno se llama vm.cambiarFiltro y el ViewModel actualiza la lista filtrada.
        Text("Filtrar por estado")
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceEvenly) {
            BotonFiltro("Todas", FiltroEstado.TODAS, filtro) { vm.cambiarFiltro(it) }
            BotonFiltro("Pendientes", FiltroEstado.PENDIENTES, filtro) { vm.cambiarFiltro(it) }
            BotonFiltro("Completadas", FiltroEstado.COMPLETADAS, filtro) { vm.cambiarFiltro(it) }
        }
        Spacer(Modifier.height(8.dp))
        // Mensajes cuando no hay nada que mostrar (estado vacío).
        if (actividades.isEmpty()) Text("No hay actividades registradas")
        else if (filtradas.isEmpty()) Text(if (filtro == FiltroEstado.PENDIENTES) "No tienes actividades pendientes" else "No tienes actividades completadas")
        // LazyColumn: lista vertical con scroll. items recorre la lista FILTRADA y crea una tarjeta por actividad.
        // key = it.id: Compose identifica cada tarjeta por su id (sirve cuando la lista cambia).
        LazyColumn { items(filtradas, key={it.id}) { item -> ActivityCard(item) { onDetalle(item.id) } } }
    }
}

// Botón de un filtro: si es el seleccionado se pinta azul con letra blanca, si no, gris claro con letra negra.
// opcion = el filtro que representa este botón; seleccionado = el filtro actual del ViewModel.
@Composable
fun BotonFiltro(texto:String, opcion:FiltroEstado, seleccionado:FiltroEstado, onClick:(FiltroEstado)->Unit) {
    val activo = opcion == seleccionado
    Button(
        onClick = { onClick(opcion) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (activo) AzulPrincipal else Color.LightGray,
            contentColor = if (activo) Color.White else Color.Black
        ),
        contentPadding = PaddingValues(horizontal = 12.dp)  // menos relleno para que quepan los 3 botones
    ) {
        Text(texto)
    }
}
