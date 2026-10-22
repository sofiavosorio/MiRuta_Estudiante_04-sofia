// MainActivity: punto de entrada de la app (la única Activity). Aquí se crean los objetos
// principales y se arma el tema y la navegación.
package com.example.miruta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.miruta.data.local.PreferencesManager
import com.example.miruta.data.repository.ActivityRepository
import com.example.miruta.navigation.AppNavigation
import com.example.miruta.navigation.Routes
import com.example.miruta.ui.theme.MiRutaTheme
import com.example.miruta.viewmodel.ActivityViewModel
import com.example.miruta.viewmodel.PreferencesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity:ComponentActivity() {
    // onCreate: se ejecuta cuando se abre la app.
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        // Se crean UNA vez: el manejador de DataStore y el repositorio de actividades.
        val manager=PreferencesManager(applicationContext)
        val repo=ActivityRepository()
        // setContent: aquí empieza la interfaz hecha con Jetpack Compose.
        setContent {
            // FACTORY: como los ViewModel reciben parámetros (repo y manager), hay que decirle a Android
            // cómo crearlos. viewModel(...) crea el ViewModel una sola vez y lo conserva al girar la pantalla.
            // Los dos ViewModel se comparten con todas las pantallas (todas ven las mismas actividades).
            val activityVM:ActivityViewModel = viewModel(factory=object:ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T = ActivityViewModel(repo) as T
            })
            val prefsVM:PreferencesViewModel = viewModel(factory=object:ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST") override fun <T:ViewModel> create(modelClass:Class<T>):T = PreferencesViewModel(manager) as T
            })
            // Se observa el modo oscuro: si cambia en Configuración, el tema cambia en toda la app.
            val oscuro by prefsVM.oscuro.collectAsStateWithLifecycle()
            // Se decide destino solo al obtener el primer valor real de DataStore.
            // initial empieza en null (todavía no se sabe). first() lee el primer valor guardado:
            // si el onboarding ya se completó se abre HOME, si no, ONBOARDING (solo la primera vez).
            // LaunchedEffect(Unit): ejecuta ese código una sola vez dentro de una corrutina.
            var initial by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(Unit) { initial=if(manager.onboardingCompletado.first()) Routes.HOME else Routes.ONBOARDING }
            MiRutaTheme(oscuro=oscuro) {
                // Surface usa el color de fondo del tema, así el fondo cambia con el modo oscuro
                // (igual que en el proyecto DataStore).
                Surface(modifier=Modifier.fillMaxSize(), color=MaterialTheme.colorScheme.background) {
                    // ?.let: solo se dibuja la navegación cuando initial ya NO es null.
                    initial?.let { AppNavigation(rememberNavController(),it,activityVM,prefsVM) }
                }
            }
        }
    }
}
