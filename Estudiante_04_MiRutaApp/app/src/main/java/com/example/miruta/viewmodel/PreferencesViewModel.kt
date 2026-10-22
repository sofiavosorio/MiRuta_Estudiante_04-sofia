// ViewModel de las PREFERENCIAS: conecta DataStore (PreferencesManager) con las pantallas.
package com.example.miruta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.local.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(private val manager:PreferencesManager): ViewModel() {
    // stateIn convierte el Flow de DataStore en un StateFlow (siempre tiene un valor actual).
    //   - viewModelScope: vive mientras viva el ViewModel.
    //   - WhileSubscribed(5000): lee DataStore mientras alguna pantalla lo observe (y 5 s más).
    //   - el último parámetro es el valor inicial mientras llega el valor real.
    val onboarding = manager.onboardingCompletado.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val nombre = manager.nombre.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Estudiante")
    val oscuro = manager.oscuro.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Las funciones de guardar de DataStore son suspend, por eso se llaman dentro de viewModelScope.launch.
    fun finalizarOnboarding() = viewModelScope.launch { manager.finalizarOnboarding() }
    fun guardarNombre(valor:String) = viewModelScope.launch { manager.guardarNombre(valor) }
    fun guardarOscuro(valor:Boolean) = viewModelScope.launch { manager.guardarModoOscuro(valor) }
}
