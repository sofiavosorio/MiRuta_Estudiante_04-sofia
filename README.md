# ¿Qué estás construyendo?

**MiRuta** es una app Android para organizar actividades académicas y personales. Tiene cinco pantallas: bienvenida de tres páginas (solo primer uso), Inicio con tarjetas y lista de actividades, Nueva actividad con validaciones, Detalle para marcar tareas como completadas y Configuración para nombre y tema. DataStore guarda onboarding, nombre y modo oscuro; las actividades permanecen en memoria mientras corre la app.

**Ejemplo:** abrir por primera vez → completar bienvenida → crear «Estudiar Kotlin» → verla en Inicio → abrir Detalle → marcar completada → configurar nombre y tema → reiniciar y verificar preferencias.

**Tu desafío MOV1-04:** Filtros por estado.

Lee `Guia_Estudiante_04.docx` antes de modificar código.

---

# MiRutaApp · MOV1-04

**Nombre:** __________  **Fecha:** __________

## Uso
1. Abrir la carpeta del proyecto en Android Studio, con JDK 17 y SDK Android 35 instalados.
2. Sincronizar Gradle. Si Android Studio no dispone de Gradle Wrapper, usar una instalación local de Gradle 8.9 o generar el wrapper con `gradle wrapper --gradle-version 8.9`. El ZIP incluye `gradle-wrapper.properties`, pero NO el ejecutable del wrapper.
3. Ejecutar en emulador Android 8.0 (API 26) o superior.
4. Completar todos los `TODO-ESTUDIANTE` y `TODO-01` a `TODO-13`.
5. Resolver el reto individual: **Filtro de estados**. Incorporar selector Todas, Pendientes y Completadas con lista reactiva.
6. Prueba de aceptación individual: Completar una de dos y cambiar filtros: cada uno debe mostrar lo correcto.

La aplicación guarda onboarding, nombre y tema con DataStore. Las actividades son temporales en memoria. Esqueleto didáctico, no solución resuelta.

## Entrega
ZIP con código fuente, capturas de pruebas, respuestas y sustentación en Android Studio. No modificar los nombres de paquetes o archivos.
