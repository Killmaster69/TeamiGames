package com.example.teamigames.ui

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.teamigames.R
import java.util.*
import kotlinx.coroutines.delay
import androidx.compose.foundation.ExperimentalFoundationApi

data class LetterItem(
    val letter: String,
    val word: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlphabetGameScreen() {
    val context = LocalContext.current

    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // aquí no ponemos nada, porque ttsInstance aún se construye
            }
        }

        // Configura el idioma después de crear la instancia
        ttsInstance.language = Locale("es", "ES")
        tts.value = ttsInstance

        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }



    val alphabetList = listOf(
        LetterItem("A", "Abuelos", R.drawable.arbol),
        LetterItem("B", "Balón", R.drawable.balon),
        LetterItem("C", "Coche", R.drawable.coche),
        LetterItem("D", "Dragón", R.drawable.dragon),
        LetterItem("E", "Enfermera", R.drawable.enfermera),
        LetterItem("F", "Flor", R.drawable.flor),
        LetterItem("G", "Gato", R.drawable.gato),
        LetterItem("H", "Hada", R.drawable.hada),
        LetterItem("I", "Iglú", R.drawable.iglu),
        LetterItem("J", "Jirafa", R.drawable.jirafa),
        LetterItem("K", "Koala", R.drawable.koala),
        LetterItem("L", "Libro", R.drawable.libro),
        LetterItem("M", "Mariposa", R.drawable.mariposa),
        LetterItem("N", "Naranja", R.drawable.naranja2),
        LetterItem("O", "Oveja", R.drawable.oveja),
        LetterItem("P", "Paraguas", R.drawable.paraguas),
        LetterItem("Q", "Queso", R.drawable.queso),
        LetterItem("R", "Ratón", R.drawable.raton),
        LetterItem("S", "Silla", R.drawable.silla),
        LetterItem("U", "Uvas", R.drawable.uvas),
        LetterItem("V", "Vela", R.drawable.vela),
        LetterItem("W", "Windsurf", R.drawable.windsurf),
        LetterItem("X", "Xilófono", R.drawable.xilofono),
        LetterItem("Y", "Yogurt", R.drawable.yogurt),
        LetterItem("Z", "Zapatos", R.drawable.zapatos)
    )

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth < 600) 3 else 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDE7),
                        Color(0xFFFFEBEE),
                        Color(0xFFFFF3E0)
                    )
                )
            )
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            items(alphabetList) { item ->
                AlphabetCard(item = item) {
                    tts.value?.speak(
                        "${item.word} se escribe con ${item.letter}",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )
                }
            }
        }
    }
}

@Composable
fun AlphabetCard(item: LetterItem, onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1f, label = "cardScale")

    Card(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                isPressed = true
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "${item.word} - ${item.letter}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            delay(140)
            isPressed = false
        }
    }
}
