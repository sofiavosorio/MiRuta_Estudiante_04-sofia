// CARPETA viewmodel: aquí va la LÓGICA de la app (validaciones, corrutinas, estado).
// Las pantallas solo muestran lo que hay aquí y le avisan cuando el usuario hace algo (MVVM).
package com.example.miruta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruta.data.repository.ActivityRepository
import com.example.miruta.model.Actividad
import com.example.miruta.model.FiltroEstado
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Recibe el repositorio por el constructor (se lo pasa MainActivity con un Factory).
// ViewModel sobrevive a la rotación de pantalla: los datos no se pierden al girar el celular.
class ActivityViewModel(private val repository: ActivityRepository): ViewModel() {

    // PATRÓN de estado: la variable privada con "_" (MutableStateFlow) solo se cambia aquí adentro,
    // y la pública (asStateFlow) es de SOLO LECTURA para las pantallas.
    // StateFlow = guarda un valor y avisa a quien lo observe cada vez que cambia.

    // Lista completa de actividades (la usa Detail y el contador de Home).
    private val _actividades = MutableStateFlow(repository.obtenerActividades())
    val actividades = _actividades.asStateFlow()
    // true mientras se está "guardando" (se muestra el círculo de carga y "Guardando…").
    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()
    // Mensaje de error de validación ("" = no hay error).
    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    // Reto MOV1-04: filtro elegido (empieza en Todas) y la lista que se ve en Home según ese filtro.
    private val _filtro = MutableStateFlow(FiltroEstado.TODAS)
    val filtro = _filtro.asStateFlow()
    private val _actividadesFiltradas = MutableStateFlow(repository.filtrarPorEstado(FiltroEstado.TODAS))
    val actividadesFiltradas = _actividadesFiltradas.asStateFlow()

    // Se llama con el botón "Registrar" de Nueva actividad.
    // alTerminar = función que manda la pantalla (regresar a Home); se ejecuta solo si se guardó bien.
    fun registrar(titulo:String, descripcion:String, categoria:String, prioridad:Int, alTerminar:()->Unit) {
        // DOBLE PULSACIÓN: si ya se está guardando, se ignora el segundo clic (no se crean dos actividades).
        if (_cargando.value) return
        _error.value = ""
        // TODO-03: validar título, prioridad y la regla particular del estudiante.
        // VALIDACIONES en orden: si alguna falla se guarda el mensaje y se sale con return (no se registra).
        if (titulo.isBlank()) { _error.value = "El título es obligatorio"; return }               // vacío o solo espacios
        else if (categoria.isBlank()) { _error.value = "Escoge una categoría"; return }
        else if (prioridad < 1) { _error.value = "La prioridad debe ser Baja, Media o Alta"; return } // menor que 1 no existe
        else if (prioridad > 3) { _error.value = "La prioridad debe ser Baja, Media o Alta"; return } // mayor que 3 no existe
        // Se pone cargando en true ANTES de lanzar la corrutina, así un segundo clic inmediato ya queda bloqueado.
        _cargando.value = true
        // CORRUTINA: viewModelScope.launch hace el trabajo sin congelar la pantalla.
        // viewModelScope = la corrutina se cancela sola si el ViewModel se destruye.
        viewModelScope.launch {
            try {
                // delay: simula que guardar tarda 1,5 segundos (como una llamada a un servidor).
                // Sin delay el guardado sería instantáneo y no se vería el estado de carga.
                delay(1500)
                // id nuevo = el mayor id que exista + 1 (si la lista está vacía, maxOfOrNull da null y se usa 0).
                // trim() quita los espacios del inicio y del final.
                val nueva = Actividad((repository.obtenerActividades().maxOfOrNull { it.id } ?: 0)+1, titulo.trim(), descripcion.trim(), categoria, prioridad)
                // TODO-04: gestionar resultado de agregar y actualizar el StateFlow.
                // agregar devuelve true si se guardó; en ese caso se actualizan las listas y se regresa a Home.
                if (repository.agregar(nueva)) {
                    actualizarListas()
                    alTerminar()
                } else {
                    _error.value = "No se pudo registrar la actividad"
                }
            } finally { _cargando.value = false }  // finally: se ejecuta siempre, haya error o no
        }
    }

    // Se llama con el botón de la pantalla Detalle.
    fun cambiarEstado(id: Int) {
        // TODO-05: actualizar la lista observable tras cambiar el estado.
        // Si el repositorio lo cambió, se vuelven a pedir las listas para que Home y Detail se actualicen.
        if (repository.cambiarEstado(id)) actualizarListas()
    }

    // Reto MOV1-04: se llama al tocar Todas / Pendientes / Completadas en Home.
    fun cambiarFiltro(nuevo: FiltroEstado) {
        _filtro.value = nuevo
        actualizarListas()
    }

    // Borra el mensaje de error (se llama al abrir Nueva actividad).
    fun limpiarError() { _error.value = "" }

    // Cada vez que algo cambia (agregar, cambiar estado o cambiar filtro) se vuelven a pedir las listas
    // al repositorio. Como son StateFlow, la pantalla que las observa se actualiza sola (lista reactiva).
    private fun actualizarListas() {
        _actividades.value = repository.obtenerActividades()
        _actividadesFiltradas.value = repository.filtrarPorEstado(_filtro.value)
    }
}
