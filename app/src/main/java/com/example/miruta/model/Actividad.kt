// CARPETA model: aquí van los "moldes" de los datos. No tienen lógica ni pantallas.
package com.example.miruta.model

// data class: clase hecha para guardar datos. Kotlin le crea solo:
//   - copy()  -> hace una copia cambiando algún campo (se usa en el repositorio para cambiar "completada")
//   - equals() y toString()
// Son "val" (no se pueden cambiar). Para modificar una actividad se crea una copia con copy().
//
// id: identificador único (lo calcula el ViewModel: el mayor id + 1). Sirve para abrir el Detalle.
// titulo: obligatorio (lo valida el ViewModel).
// descripcion: opcional.
// categoria: "Académica" o "Personal" (se escoge con botones en Nueva actividad).
// prioridad: 1 = Baja, 2 = Media, 3 = Alta.
// completada: false = Pendiente, true = Completada. Empieza en false (valor por defecto).
data class Actividad(val id:Int, val titulo:String, val descripcion:String, val categoria:String, val prioridad:Int, val completada:Boolean=false)

// Reto MOV1-04: enum class = lista cerrada de opciones del filtro de estados.
// El filtro solo puede ser uno de estos 3 valores (igual que TipoCliente en el proyecto tumovil).
enum class FiltroEstado {
    TODAS,        // muestra todas las actividades
    PENDIENTES,   // solo las que tienen completada = false
    COMPLETADAS   // solo las que tienen completada = true
}
