// CARPETA data/local: guarda datos pequeños en el celular que NO se borran al cerrar la app (DataStore).
// Aquí se guardan 3 cosas: si ya se vio el onboarding, el nombre y si el modo oscuro está activo.
package com.example.miruta.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el archivo de preferencias llamado "miruta_preferences".
// "by preferencesDataStore" hace que exista uno solo para toda la app.
private val Context.dataStore by preferencesDataStore("miruta_preferences")

// Recibe el Context (se lo manda MainActivity) porque DataStore lo necesita para encontrar el archivo.
class PreferencesManager(private val context: Context) {

    // companion object: las LLAVES con las que se guarda cada dato (como el nombre de una columna).
    // booleanPreferencesKey = guarda true/false. stringPreferencesKey = guarda texto.
    companion object {
        private val ONBOARDING = booleanPreferencesKey("onboarding")
        private val NOMBRE = stringPreferencesKey("nombre")
        private val OSCURO = booleanPreferencesKey("oscuro")
    }

    // LEER: cada dato es un Flow (flujo de datos). Cada vez que el valor guardado cambia,
    // el Flow emite el valor nuevo y la pantalla que lo observa se actualiza sola.
    // map: toma las preferencias y saca solo el dato que se necesita.
    // ?: (elvis) = valor por defecto si todavía no se ha guardado nada.
    val onboardingCompletado: Flow<Boolean> = context.dataStore.data.map { it[ONBOARDING] ?: false }
    val nombre: Flow<String> = context.dataStore.data.map { it[NOMBRE] ?: "Estudiante" }
    val oscuro: Flow<Boolean> = context.dataStore.data.map { it[OSCURO] ?: false }

    // GUARDAR: son funciones suspend porque escribir en el disco puede tardar.
    // suspend = solo se puede llamar dentro de una corrutina (viewModelScope.launch en el ViewModel),
    // así la pantalla no se congela mientras se guarda.
    suspend fun finalizarOnboarding() { context.dataStore.edit { it[ONBOARDING] = true } }

    // TODO-ESTUDIANTE: implementar las dos funciones de persistencia.
    // TODO-01: suspend porque se llama dentro de una corrutina; edit guarda el valor con la llave NOMBRE.
    suspend fun guardarNombre(valor: String) {
        context.dataStore.edit { preferences ->
            preferences[NOMBRE] = valor
        }
    }
    // TODO-02: igual que el anterior, pero con la llave OSCURO (true = modo oscuro).
    suspend fun guardarModoOscuro(valor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OSCURO] = valor
        }
    }
}
