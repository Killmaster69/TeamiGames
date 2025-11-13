package com.example.teamigames.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teamigames.ui.HomeScreen
import com.example.teamigames.ui.PatternGameScreen
import com.example.teamigames.ui.AlphabetGameScreen

// 📌 Definición de rutas de navegación
sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Pattern : Routes("pattern_game")
    object Alphabet : Routes("alphabet_game")
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.route
    ) {
        // 🏠 Menú principal
        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        // 👕 Juego de patrones
        composable(Routes.Pattern.route) {
            PatternGameScreen()
        }

        // 🔤 Juego del abecedario
        composable(Routes.Alphabet.route) {
            AlphabetGameScreen()
        }
    }
}
