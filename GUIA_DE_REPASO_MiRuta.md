# Guía de repaso · MiRuta · MOV1-04 (Filtro de estados)

Esta guía resume **todo lo que está hecho en el proyecto**, cómo funciona por dentro y las **preguntas que te pueden hacer en la sustentación** con sus respuestas.

> Consejo: lee la guía con Android Studio abierto al lado y busca en el código cada cosa que se menciona. Con `Ctrl + Shift + F` puedes buscar por ejemplo `TODO-04` o `MOV1-04`.

---

## 1. ¿Qué es MiRuta?

Una app Android (Jetpack Compose) para **organizar actividades académicas y personales**. Tiene **5 pantallas**:

| # | Pantalla | Archivo | Qué hace |
|---|---|---|---|
| 1 | Bienvenida (Onboarding) | `ui/screens/OnboardingScreen.kt` | 3 páginas, indicador y botones Anterior/Siguiente. Solo sale la **primera vez**. |
| 2 | Inicio (Home) | `ui/screens/HomeScreen.kt` | Saludo con el nombre, **filtro de estados (mi reto)** y lista de tarjetas. |
| 3 | Nueva actividad | `ui/screens/AddActivityScreen.kt` | Formulario: título, descripción, categoría, prioridad. Valida y muestra carga. |
| 4 | Detalle | `ui/screens/DetailScreen.kt` | Toda la información de una actividad y botón para marcarla completada/pendiente. |
| 5 | Configuración | `ui/screens/SettingsScreen.kt` | Cambiar nombre y modo oscuro (se guardan con DataStore). |

**Qué se guarda y dónde:**
- **DataStore** (no se borra al cerrar la app): si ya se vio el onboarding, el nombre y el modo oscuro.
- **Memoria** (se borra al cerrar la app): las actividades. Así lo pide la guía.

---

## 2. Arquitectura MVVM (cómo se conectan las piezas)

```
 PANTALLAS (View)            VIEWMODEL (lógica)              DATOS (Model)
 ─────────────────           ──────────────────              ─────────────
 HomeScreen        ──pide──▶ ActivityViewModel   ──pide──▶  ActivityRepository (memoria)
 AddActivityScreen ◀─observa─ (StateFlow)        ◀─entrega─  Actividad (data class)
 DetailScreen                                                 FiltroEstado (enum)

 SettingsScreen    ──pide──▶ PreferencesViewModel ──pide──▶ PreferencesManager (DataStore)
 MainActivity      ◀─observa─ (StateFlow)         ◀─Flow───
```

- **Model** (carpeta `model` y `data`): los datos y cómo se guardan.
- **ViewModel** (carpeta `viewmodel`): validaciones, corrutinas y estado. Las pantallas **nunca** hablan directo con el repositorio ni con DataStore.
- **View** (carpeta `ui`): solo dibuja lo que el ViewModel tiene y le avisa cuando el usuario toca algo.

**Flujo de un dato (ejemplo: marcar completada):**
1. En Detalle toco "Marcar como completada" → se llama `vm.cambiarEstado(id)`.
2. El ViewModel le pide al repositorio `repository.cambiarEstado(id)`, que hace `copy(completada = !completada)`.
3. El ViewModel llama a `actualizarListas()`, que pone valores nuevos en `_actividades` y `_actividadesFiltradas`.
4. Detalle y Home observan esos StateFlow con `collectAsStateWithLifecycle()`, así que se vuelven a dibujar solas.

---

## 3. Recorrido archivo por archivo

### `config/StudentConfig.kt`
Constantes de la entrega: `MOV1-04`, reto 4, "Filtro de estados". **No se modifica.**

### `model/Actividad.kt`
- `data class Actividad(id, titulo, descripcion, categoria, prioridad, completada = false)`.
- Prioridad: 1 = Baja, 2 = Media, 3 = Alta.
- **Reto:** `enum class FiltroEstado { TODAS, PENDIENTES, COMPLETADAS }` (igual que `TipoCliente` en tumovil).

### `data/repository/ActivityRepository.kt`
Lista privada `mutableListOf<Actividad>()` con estas operaciones:
- `obtenerActividades()`: devuelve una copia (`toList()`).
- `agregar()`: devuelve `false` si el id ya existe.
- `cambiarEstado()`: busca con `indexOfFirst` y reemplaza con `copy(completada = !...)`.
- **Reto:** `filtrarPorEstado(filtro)`: recorre la lista con `forEach`, decide con `when` si cada actividad se muestra y la agrega con `add` a una lista nueva.

### `data/local/PreferencesManager.kt` (TODO-01 y 02)
- 3 llaves: `ONBOARDING`, `NOMBRE`, `OSCURO`.
- Leer: `Flow` con `dataStore.data.map { it[LLAVE] ?: valorPorDefecto }`.
- Guardar: funciones `suspend` con `dataStore.edit { it[LLAVE] = valor }` (igual que `saveEmail` y `saveDarkMode` del proyecto DataStore).

### `viewmodel/ActivityViewModel.kt` (TODO-03, 04 y 05 + reto)
- Estados con patrón `_privado` (MutableStateFlow) y público (`asStateFlow()`): `actividades`, `cargando`, `error`, `filtro`, `actividadesFiltradas`.
- `registrar()`:
  1. Si `cargando` es true, `return` (bloquea la **doble pulsación**).
  2. Validaciones con `if / else if`: título vacío (`isBlank`), categoría vacía, prioridad menor que 1 o mayor que 3.
  3. `_cargando = true`, luego `viewModelScope.launch { delay(1500) ... }`.
  4. Calcula el id (mayor id + 1), llama a `agregar` y, si devuelve true, actualiza las listas y vuelve a Home.
  5. En `finally` pone `_cargando = false`.
- `cambiarEstado(id)`, `cambiarFiltro(nuevo)` y `actualizarListas()`.

### `viewmodel/PreferencesViewModel.kt`
- Convierte los Flow de DataStore en StateFlow con `stateIn(viewModelScope, WhileSubscribed(5000), valorInicial)`.
- `guardarNombre`, `guardarOscuro` y `finalizarOnboarding` lanzan corrutinas, porque las funciones de DataStore son `suspend`.

### `MainActivity.kt`
- Crea `PreferencesManager` y `ActivityRepository`.
- Crea los ViewModel con un **Factory**, porque reciben parámetros.
- Lee el onboarding con `first()` dentro de `LaunchedEffect` y elige si arranca en HOME u ONBOARDING.
- Aplica `MiRutaTheme(oscuro)` y un `Surface` con el fondo del tema.

### `navigation/Routes.kt` y `navigation/AppNavigation.kt` (TODO-13)
- Rutas: `onboarding`, `home`, `add`, `detail/{id}`, `settings`.
- Onboarding → Home con `popUpTo(ONBOARDING) { inclusive = true }`, así Atrás no vuelve a la bienvenida.
- Detail usa `navArgument("id") { type = NavType.IntType }` y `getInt("id") ?: -1`.
- Las demás pantallas vuelven con `popBackStack()`.

### `ui/theme/Color.kt` y `Theme.kt` (TODO-06)
- Paleta clara y oscura (`lightColorScheme` y `darkColorScheme`).
- Colores de prioridad y funciones `colorPrioridad()`, `colorTextoPrioridad()` y `nombrePrioridad()`.

### `ui/components/ActivityCard.kt` (TODO-07)
Tarjeta con el fondo según la prioridad (`CardDefaults.cardColors`), título, categoría, prioridad y el estado en verde (Completada) o gris (Pendiente).

### `ui/screens/OnboardingScreen.kt` (TODO-08)
Ícono por página, indicador de 3 barritas (como `PagerLindicator`), botón Anterior desde la página 2 y "Empezar" en la última.

### `ui/screens/HomeScreen.kt` (TODO-09, reto)
3 `BotonFiltro` en una fila, mensajes de lista vacía y `LazyColumn` con la **lista filtrada**.

### `ui/screens/AddActivityScreen.kt` (TODO-10)
Botones para elegir categoría y prioridad, mensaje de error, botón deshabilitado mientras carga, "Guardando…" y `CircularProgressIndicator`.

### `ui/screens/DetailScreen.kt` (TODO-11)
Si el id no es válido muestra "Actividad no encontrada". El botón cambia de texto según el estado.

### `ui/screens/SettingsScreen.kt` (TODO-12)
El nombre no puede estar vacío (`isBlank`). Muestra "Nombre guardado". El switch de modo oscuro guarda al instante.

---

## 4. Mi reto MOV1-04: Filtro de estados

**Qué pide:** un selector Todas / Pendientes / Completadas con lista reactiva.

**Dónde está (para mostrarlo en la sustentación):**

| Capa | Archivo | Qué hay |
|---|---|---|
| Model | `model/Actividad.kt` | `enum class FiltroEstado` |
| Datos (la regla) | `data/repository/ActivityRepository.kt` | `filtrarPorEstado()` con `forEach` + `when` + `add` |
| ViewModel (estado) | `viewmodel/ActivityViewModel.kt` | `_filtro`, `_actividadesFiltradas`, `cambiarFiltro()` y `actualizarListas()` |
| View (interfaz) | `ui/screens/HomeScreen.kt` | 3 `BotonFiltro` y `LazyColumn(filtradas)` |

**¿Cómo filtra?** Crea una lista vacía (`mutableListOf`), recorre todas las actividades con `forEach` y, según el filtro, el `when` dice si cada una se muestra (`true`) o no. Las que sí, se agregan con `add`. Todo esto es lo mismo que vimos: `when` en tumovil, `forEach` y `add` en el esqueleto.

**¿Por qué es "reactiva"?** La lista filtrada es un `StateFlow`. Cada vez que se agrega una actividad, se cambia un estado o se cambia el filtro, el ViewModel le pone un valor nuevo. Home lo observa con `collectAsStateWithLifecycle()` y se redibuja sola, sin recargar nada a mano.

**Ejemplo de la prueba:** si marco una actividad como completada en Detalle y vuelvo a Home con el filtro en "Pendientes", ya no aparece. Eso pasa porque `cambiarEstado` llama a `actualizarListas()`, que vuelve a filtrar con el filtro actual.

---

## 5. Las 7 pruebas obligatorias (cómo hacerlas y qué capturar)

> Para reiniciar la app "desde cero" (primer inicio): en el emulador, *Settings → Apps → MiRuta → Storage → Clear storage*, o desinstálala y vuelve a correrla.

| # | Prueba | Pasos | Resultado esperado (captura) |
|---|---|---|---|
| 1 | Onboarding solo la primera vez | Instalar y abrir → pasar las 3 páginas → Empezar. Cerrar la app por completo y abrirla otra vez. | 1ª vez: bienvenida. 2ª vez: abre directo en Home. |
| 2 | Persistencia de nombre y tema | Preferencias → escribir "Sofía" → Guardar → activar Modo oscuro. Cerrar por completo (quitarla de recientes) y abrir. | Sale "Hola, Sofía" y sigue en modo oscuro. |
| 3 | Validación de título | Nueva → dejar el título vacío → Registrar. Luego escribir «Estudiar Kotlin», Académica, Alta → Registrar. | Vacío: mensaje "El título es obligatorio". Normal: aparece la tarjeta en Home. |
| 4 | Doble pulsación | Nueva → título → tocar Registrar dos veces rápido. | Solo se crea **una** actividad. |
| 5 | Cambio de estado | Tocar la tarjeta → "Marcar como completada" → Volver. | En Home la tarjeta dice "Completada" en verde. |
| 6 | Navegación y Atrás | Ir a Nueva, Detalle y Preferencias y volver con el botón Atrás del celular. | Siempre regresa a Home. Atrás en Home cierra la app, no vuelve al onboarding. |
| 7 | **Reto MOV1-04** | Crear 2 actividades → completar 1 → tocar Todas, Pendientes, Completadas. | Todas: 2. Pendientes: 1 (la no completada). Completadas: 1 (la completada). |

---

## 6. Preguntas de la guía (con respuesta)

**1. ¿Qué responsabilidad tiene el ViewModel respecto al repositorio?**
El ViewModel es el intermediario entre las pantallas y los datos. Valida lo que escribe el usuario (título, prioridad), le pide al repositorio que agregue, cambie o filtre, y publica el resultado en `StateFlow` para que las pantallas lo observen. Las pantallas nunca tocan el repositorio directamente. El repositorio solo guarda y entrega los datos, y el ViewModel decide cuándo y con qué reglas. Además, el ViewModel sobrevive a la rotación de pantalla, así que el estado no se pierde.

**2. ¿Por qué DataStore usa Flow y métodos suspendidos?**
- **Flow** porque los datos guardados pueden cambiar en cualquier momento. Con Flow, quien lo observa recibe automáticamente el valor nuevo. Por ejemplo, cuando cambio el modo oscuro, MainActivity lo recibe y cambia el tema sin recargar nada.
- **suspend** porque leer o escribir en el disco puede tardar. Si se hiciera en el hilo principal, la pantalla se congelaría. Las funciones suspend se ejecutan dentro de una corrutina (`viewModelScope.launch`) sin bloquear la interfaz.

**3. ¿Qué función cumple `delay` dentro de la corrutina y qué pasa sin él?**
`delay(1500)` **simula** que guardar tarda 1,5 segundos, como si se enviara a un servidor. Sirve para ver el estado de carga ("Guardando…" y el círculo) y para probar que la doble pulsación no crea dos actividades. `delay` no bloquea el hilo: la corrutina se pausa y la pantalla sigue respondiendo. **Sin delay** el guardado sería instantáneo y no se vería la carga, pero la app seguiría funcionando igual.

**4. ¿Cómo llega el id de Home a Detail?**
1. En Home, al tocar una tarjeta se llama `onDetalle(item.id)`.
2. AppNavigation hace `nav.navigate(Routes.detalle(id))`, que arma la ruta `"detail/3"`.
3. La ruta `Routes.DETAIL = "detail/{id}"` declara el argumento con `navArgument("id") { type = NavType.IntType }`.
4. Se lee con `entry.arguments?.getInt("id") ?: -1` y se pasa a `DetailScreen(id, ...)`.
5. DetailScreen busca la actividad con `lista.find { it.id == id }`. Si no la encuentra, o el id es -1, muestra "Actividad no encontrada".

**5. Muestra exactamente dónde implementaste la regla MOV1-04.**
La regla está en `ActivityRepository.kt`, en la función `filtrarPorEstado()`. El estado del filtro está en `ActivityViewModel.kt` (`_filtro`, `cambiarFiltro` y `actualizarListas`), el enum en `Actividad.kt` y la interfaz en `HomeScreen.kt` (`BotonFiltro` y la `LazyColumn` con `filtradas`). Ver la tabla de la sección 4.

**6. ¿Qué parte modificarías si cambian los requisitos de tu variante?**
Depende del cambio:
- **Agregar otro filtro** (por ejemplo "Alta prioridad"): agrego el valor al `enum FiltroEstado`, una rama nueva en el `when` de `filtrarPorEstado` y un `BotonFiltro` más en Home. El ViewModel no cambia.
- **Cambiar la regla** (qué cuenta como pendiente): solo cambio `filtrarPorEstado` en el repositorio.
- **Cambiar el diseño del selector**: solo `HomeScreen` / `BotonFiltro`.

Gracias a MVVM, cada cambio queda en una sola capa.

---

## 7. Otras preguntas posibles (con respuesta corta)

**Onboarding y DataStore**
- *¿Cómo sabe la app si debe mostrar el onboarding?* En MainActivity se lee `manager.onboardingCompletado.first()` dentro de `LaunchedEffect`. Si es true arranca en HOME, si no en ONBOARDING. Al tocar "Empezar" se llama `finalizarOnboarding()`, que guarda true.
- *¿Para qué sirve `first()`?* Toma solo el primer valor del Flow, que es lo guardado en ese momento, y termina. Se usa para decidir la pantalla inicial una sola vez.
- *¿Por qué `initial` empieza en null?* Para no dibujar ninguna pantalla hasta saber el valor real de DataStore. Si no, se vería el onboarding un instante aunque ya se hubiera completado.
- *¿Qué es `edit`?* La función de DataStore para escribir. Dentro se asigna `preferences[LLAVE] = valor`.
- *¿Qué hace `?:`?* Es el operador elvis: da un valor por defecto si lo de la izquierda es null. Por ejemplo `it[NOMBRE] ?: "Estudiante"`.
- *¿Por qué las actividades se pierden al cerrar y el nombre no?* Las actividades están en una lista en memoria (el repositorio). El nombre está en DataStore, que es un archivo en el celular.

**Corrutinas**
- *¿Qué es `viewModelScope`?* El alcance de corrutinas del ViewModel. Si el ViewModel se destruye, cancela sus corrutinas solo.
- *¿Para qué sirve `try / finally`?* El `finally` se ejecuta siempre, así `cargando` vuelve a false aunque haya un error.
- *¿Cómo evitaste la doble pulsación?* De dos formas: `if (_cargando.value) return` al inicio de `registrar()`, con `_cargando = true` puesto antes de lanzar la corrutina, y el botón tiene `enabled = !cargando`.

**StateFlow y estado**
- *¿Diferencia entre `_actividades` y `actividades`?* `_actividades` es privada y modificable (MutableStateFlow), y solo el ViewModel la cambia. `actividades` es pública y de solo lectura (`asStateFlow()`), y es la que leen las pantallas.
- *¿Qué hace `collectAsStateWithLifecycle()`?* Convierte un StateFlow en estado de Compose. Cuando el valor cambia, la pantalla se redibuja. Además deja de escuchar cuando la pantalla no está visible.
- *¿Qué es `remember` y `mutableStateOf`?* Estado local de una pantalla, por ejemplo lo que se escribe en el formulario. `remember` lo conserva entre recomposiciones.
- *¿Por qué los ViewModel se crean con un Factory?* Porque reciben parámetros (`repo` y `manager`). Android solo sabe crear ViewModels sin parámetros, así que el Factory le explica cómo.
- *¿Por qué se usa `copy()` para cambiar el estado?* Porque los campos de `Actividad` son `val` (inmutables). `copy` crea una actividad igual cambiando solo `completada`.

**Navegación**
- *¿Qué es el backstack?* La pila de pantallas abiertas. `navigate` pone una pantalla encima y `popBackStack` quita la de arriba y vuelve a la anterior.
- *¿Para qué sirve `popUpTo(ONBOARDING) { inclusive = true }`?* Borra el onboarding de la pila al ir a Home, así Atrás en Home no vuelve a la bienvenida.
- *¿Qué pasa si el id no existe?* Se usa -1 por defecto y DetailScreen muestra "Actividad no encontrada" sin que la app se cierre.

**Interfaz y estilos**
- *¿Cómo cambia el tema a oscuro?* El Switch guarda en DataStore, MainActivity observa `prefsVM.oscuro` y `MiRutaTheme` elige `darkColorScheme` o `lightColorScheme`. El `Surface` usa `colorScheme.background`.
- *¿Por qué los textos de las tarjetas son negros?* Porque el fondo de la tarjeta (color de prioridad) siempre es claro, también en modo oscuro.
- *¿Qué es `LazyColumn`?* Una lista vertical con scroll que solo dibuja los elementos visibles. `items(lista, key = { it.id })` crea una tarjeta por actividad.
- *¿Qué validaciones tiene el formulario?* Título obligatorio, categoría escogida y prioridad entre 1 y 3. Todas están en el ViewModel.
- *¿Y el nombre en Configuración?* No puede estar vacío.

---

## 8. Glosario rápido

| Término | Significado corto |
|---|---|
| `@Composable` | Función que dibuja interfaz en Jetpack Compose |
| `data class` | Clase para guardar datos (trae `copy`, `equals` y `toString`) |
| `enum class` | Lista cerrada de opciones |
| `when` | Como un switch: escoge una rama según el valor |
| `forEach` / `find` / `any` / `add` | Recorrer la lista / buscar uno / ¿existe alguno? / agregar |
| `Flow` | Flujo de datos que emite valores nuevos cuando cambian |
| `StateFlow` | Flow que siempre tiene un valor actual |
| `suspend` | Función que puede pausarse; se llama dentro de una corrutina |
| `viewModelScope.launch` | Inicia una corrutina ligada al ViewModel |
| `delay` | Pausa la corrutina sin bloquear la pantalla |
| `LaunchedEffect` | Ejecuta código de corrutina cuando aparece un Composable |
| `NavHost` / `composable` | Contenedor de navegación / declaración de cada pantalla |
| `navArgument` | Declara un parámetro de la ruta (por ejemplo el id) |
| `popBackStack` | Volver a la pantalla anterior |
| `DataStore` | Guardado de preferencias pequeñas que persiste al cerrar la app |

---

## 9. Antes de entregar (checklist)

- [ ] Abrir en Android Studio con **JDK 17** (*Settings → Build Tools → Gradle → Gradle JDK → jdk-17*). Con el Java 25 que trae Android Studio no compila.
- [ ] Correr en el emulador (API 26 o superior).
- [ ] Hacer las 7 pruebas y tomar las capturas.
- [ ] Llenar **Nombre** y **Fecha** en `README.md`.
- [ ] Copiar las respuestas de la sección 6 donde el profe las pida.
- [ ] Comprimir la carpeta `Estudiante_04_MiRutaApp` en ZIP (sin la carpeta `build` si aparece) junto con las capturas.
