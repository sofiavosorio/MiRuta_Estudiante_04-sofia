// Pantalla 5: CONFIGURACIÓN. Cambia el nombre de Inicio y el modo oscuro (se guardan en DataStore).
package com.example.miruta.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.miruta.viewmodel.PreferencesViewModel

@Composable
fun SettingsScreen(vm:PreferencesViewModel, onVolver:()->Unit) {
    // Valores guardados en DataStore (a través del PreferencesViewModel).
    val actual by vm.nombre.collectAsStateWithLifecycle()
    val oscuro by vm.oscuro.collectAsStateWithLifecycle()
    // nuevo = lo que se escribe en el campo. remember(actual): si el nombre guardado cambia,
    // el campo se reinicia con ese valor.
    var nuevo by remember(actual) { mutableStateOf(actual) }
    // TODO-12: validar nombre y mejorar composición visual.
    // mensaje = texto de respuesta; esError decide si se pinta de rojo (error) o verde (guardado).
    var mensaje by remember { mutableStateOf("") }
    var esError by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
        Text("Preferencias", style=MaterialTheme.typography.headlineSmall)
        // Tarjeta 1: nombre
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Nombre que aparece en Inicio", style=MaterialTheme.typography.titleMedium)
                // Al escribir se borra el mensaje anterior.
                OutlinedTextField(nuevo,{nuevo=it; mensaje=""},label={Text("Tu nombre")}, modifier=Modifier.fillMaxWidth())
                Button(onClick={
                    // Validaciones del nombre antes de guardarlo en DataStore
                    // isBlank = vacío o solo espacios. trim() quita los espacios del inicio y del final.
                    if (nuevo.isBlank()) { mensaje = "El nombre no puede estar vacío"; esError = true }
                    else { vm.guardarNombre(nuevo.trim()); mensaje = "Nombre guardado"; esError = false }
                }) { Text("Guardar nombre") }
                if (mensaje.isNotBlank()) Text(mensaje, color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
            }
        }
        // Tarjeta 2: modo oscuro. El Switch guarda el valor al instante (como en el proyecto DataStore).
        // Al guardarse, MainActivity recibe el nuevo valor y cambia el tema de toda la app.
        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.SpaceBetween) {
                Text("Modo oscuro", style=MaterialTheme.typography.titleMedium)
                Switch(checked=oscuro,onCheckedChange={vm.guardarOscuro(it)})
            }
        }
        OutlinedButton(onClick=onVolver) { Text("Volver") }
    }
}
