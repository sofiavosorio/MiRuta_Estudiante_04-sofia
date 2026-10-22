// Pantalla 3: NUEVA ACTIVIDAD. Formulario con validaciones y estado de carga.
package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miruta.ui.theme.AzulPrincipal
import com.example.miruta.ui.theme.colorTextoPrioridad
import com.example.miruta.ui.theme.nombrePrioridad
import com.example.miruta.viewmodel.ActivityViewModel

// onVolver = regresar a Home (popBackStack). Se usa al guardar bien o al tocar Cancelar.
@Composable
fun AddActivityScreen(vm:ActivityViewModel, onVolver:()->Unit) {
    // Estados LOCALES del formulario (lo que el usuario va escribiendo/eligiendo).
    // Solo se mandan al ViewModel al tocar Registrar.
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Académica") }  // valor inicial
    var prioridad by remember { mutableIntStateOf(1) }          // 1 = Baja
    // Estados que vienen del ViewModel.
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    // Al abrir el formulario se borra el error que hubiera quedado de antes.
    // LaunchedEffect(Unit) se ejecuta una sola vez cuando aparece la pantalla.
    LaunchedEffect(Unit) { vm.limpiarError() }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("Nueva actividad", style=MaterialTheme.typography.headlineSmall)
        // value = lo que se muestra; onValueChange = cada letra escrita actualiza el estado.
        // enabled = !cargando: mientras se guarda no se puede escribir.
        OutlinedTextField(titulo,{titulo=it}, label={Text("Título")}, enabled=!cargando, modifier=Modifier.fillMaxWidth())
        OutlinedTextField(descripcion,{descripcion=it}, label={Text("Descripción")}, enabled=!cargando, modifier=Modifier.fillMaxWidth())
        // TODO-10: reglas de errores, categorías y estados visuales.
        // Categoría: se escoge con botones (como el tipo de cliente en tumovil), no se escribe a mano.
        Text("Categoría")
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            // forEach: crea un botón por cada categoría de la lista.
            listOf("Académica", "Personal").forEach { c ->
                BotonOpcion(c, activo = categoria == c, habilitado = !cargando) { categoria = c }
            }
        }
        Text("Prioridad: ${nombrePrioridad(prioridad)}", color=colorTextoPrioridad(prioridad))
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
            // (1..3) = rango del 1 al 3: un botón por prioridad (Baja, Media, Alta).
            (1..3).forEach { p -> BotonOpcion(nombrePrioridad(p), activo = prioridad == p, habilitado = !cargando) { prioridad = p } }
        }
        // Si el ViewModel dejó un mensaje de error, se muestra en rojo.
        if(error.isNotBlank()) Text(error, color=MaterialTheme.colorScheme.error)
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            // Registrar: se desactiva mientras carga (otra protección contra la doble pulsación).
            // La validación y el guardado los hace el ViewModel; la pantalla solo le pasa los datos.
            Button(enabled=!cargando, onClick={vm.registrar(titulo,descripcion,categoria,prioridad,onVolver)}) { Text(if(cargando) "Guardando…" else "Registrar") }
            OutlinedButton(enabled=!cargando, onClick=onVolver) { Text("Cancelar") }
        }
        // Mientras la corrutina trabaja (delay de 1,5 s) se muestra el círculo de carga (como en CorutinasApp).
        if (cargando) CircularProgressIndicator()
    }
}

// Botón de opción reutilizable: el elegido se pinta de azul con letra blanca, los demás en gris claro.
@Composable
fun BotonOpcion(texto:String, activo:Boolean, habilitado:Boolean, onClick:()->Unit) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (activo) AzulPrincipal else Color.LightGray,
            contentColor = if (activo) Color.White else Color.Black
        )
    ) {
        Text(texto)
    }
}
