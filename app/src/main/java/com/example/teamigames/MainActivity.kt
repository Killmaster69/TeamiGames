package com.example.teamigames

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.teamigames.nav.NavGraph
import com.example.teamigames.ui.theme.TeamiGamesTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeamiGamesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavGraph() // 🔹 Aquí se carga la navegación de la app
                }
            }
        }
    }
}
