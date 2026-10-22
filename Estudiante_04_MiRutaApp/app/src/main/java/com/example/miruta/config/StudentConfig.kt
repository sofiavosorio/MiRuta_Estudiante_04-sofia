// CARPETA config: datos fijos de la entrega. No se deben cambiar.
package com.example.miruta.config

/** Identificador de entrega; no constituye detector de IA. */
// object = existe una sola copia en toda la app (no se crea con "new" ni con paréntesis).
// const val = constante: su valor se conoce al compilar y nunca cambia.
object StudentConfig {
    const val CODIGO_ESTUDIANTE = "MOV1-04"          // mi código de estudiante
    const val RETO_ASIGNADO = 4                      // número del reto individual
    const val DESCRIPCION_RETO = "Filtro de estados" // mi reto: filtrar Todas / Pendientes / Completadas
}
