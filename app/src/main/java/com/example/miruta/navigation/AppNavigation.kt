package com.example.miruta.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.miruta.ui.screens.*
import com.example.miruta.viewmodel.ActivityViewModel
import com.example.miruta.viewmodel.PreferencesViewModel

// nav: el que cambia de pantalla (navigate = ir, popBackStack = volver a la anterior).
// initial: pantalla de inicio (ONBOARDING o HOME, lo decide MainActivity).
@Composable
fun AppNavigation(nav:NavHostController, initial:String, activityVM:ActivityViewModel, prefsVM:PreferencesViewModel) {
    // TODO-13: evaluar navegación y backstack de acuerdo con las pruebas.
    // - Onboarding: al terminar se borra del backstack (popUpTo inclusive), así Atrás desde Home no regresa a la bienvenida.
    // - Add, Detail y Settings vuelven con popBackStack, regresando al Home que ya estaba abierto.
    // - Detail recibe el id como entero (NavType.IntType); si no llega se usa -1 y la pantalla muestra "no encontrada".
    // BACKSTACK = pila de pantallas abiertas. navigate pone una encima; popBackStack quita la de arriba.
    NavHost(navController=nav,startDestination=initial) {
        // 1. ONBOARDING: al tocar "Empezar" se guarda en DataStore que ya se vio y se va a Home.
        composable(Routes.ONBOARDING) {
            OnboardingScreen { prefsVM.finalizarOnboarding(); nav.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING){inclusive=true} } }
        }
        // 2. HOME: recibe las funciones para ir a Nueva, Detalle (con el id) y Preferencias.
        composable(Routes.HOME) { HomeScreen(activityVM,prefsVM,onAgregar={nav.navigate(Routes.ADD)},onDetalle={nav.navigate(Routes.detalle(it))},onAjustes={nav.navigate(Routes.SETTINGS)}) }
        // 3. NUEVA ACTIVIDAD: al guardar (o cancelar) vuelve a Home.
        composable(Routes.ADD) { AddActivityScreen(activityVM){nav.popBackStack()} }
        // 4. DETALLE: la ruta "detail/{id}" trae el id. navArgument dice que es un Int
        //    (como en clase7dependencias). Se lee con arguments?.getInt("id").
        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { entry ->
            val id=entry.arguments?.getInt("id") ?: -1
            DetailScreen(id,activityVM){nav.popBackStack()}
        }
        // 5. CONFIGURACIÓN: nombre y modo oscuro.
        composable(Routes.SETTINGS) { SettingsScreen(prefsVM){nav.popBackStack()} }
    }
}
