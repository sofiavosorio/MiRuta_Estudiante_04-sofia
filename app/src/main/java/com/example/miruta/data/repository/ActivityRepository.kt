// CARPETA data/repository: el REPOSITORIO es el que guarda y entrega los datos de las actividades.
// El ViewModel le pide los datos al repositorio; las pantallas nunca hablan directo con él.
// Las actividades viven en MEMORIA: si se cierra la app, se pierden (así lo pide la guía).
package com.example.miruta.data.repository

import com.example.miruta.model.Actividad
import com.example.miruta.model.FiltroEstado

class ActivityRepository {
    // Lista privada: solo el repositorio la puede modificar.
    // mutableListOf = lista a la que se le pueden agregar o cambiar elementos.
    private val actividades = mutableListOf<Actividad>()

    // Devuelve una COPIA de la lista (toList) para que nadie de afuera la modifique.
    fun obtenerActividades(): List<Actividad> = actividades.toList()

    // find: busca la primera actividad con ese id; si no existe devuelve null.
    fun buscarActividad(id: Int): Actividad? = actividades.find { it.id == id }

    // Agrega una actividad. Devuelve false si ya existe una con el mismo id (no se repite).
    fun agregar(actividad: Actividad): Boolean {
        if (actividades.any { it.id == actividad.id }) return false  // any = ¿existe alguna que cumpla?
        actividades.add(actividad)
        return true
    }

    // Cambia una actividad de Pendiente a Completada o al revés.
    fun cambiarEstado(id: Int): Boolean {
        val indice = actividades.indexOfFirst { it.id == id }  // posición en la lista (-1 si no está)
        if (indice < 0) return false
        val actual = actividades[indice]
        // copy: crea una copia igual pero con completada invertida (! = negación: true->false, false->true)
        actividades[indice] = actual.copy(completada = !actual.completada)
        return true
    }

    // TODO-ESTUDIANTE: añadir las reglas del reto asignado en la capa apropiada.
    // Regla MOV1-04 (filtro de estados): según el filtro elegido devuelve solo las actividades que corresponden.
    // Está en el repositorio porque es una regla de DATOS (qué actividades se entregan).
    fun filtrarPorEstado(filtro: FiltroEstado): List<Actividad> {
        // Lista nueva donde se van guardando las actividades que sí se deben mostrar.
        val resultado = mutableListOf<Actividad>()
        // forEach: recorre las actividades una por una (como (1..3).forEach en Nueva actividad).
        actividades.forEach { actividad ->
            // when: según el filtro decide si esta actividad se muestra (como porcentajeDescuento de tumovil).
            val mostrar = when (filtro) {
                FiltroEstado.TODAS -> true                         // todas se muestran
                FiltroEstado.PENDIENTES -> !actividad.completada   // solo si completada = false
                FiltroEstado.COMPLETADAS -> actividad.completada   // solo si completada = true
            }
            if (mostrar) resultado.add(actividad)
        }
        return resultado
    }
}
