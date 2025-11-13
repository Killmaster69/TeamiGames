package com.example.teamigames.ui

import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teamigames.R
import java.util.*

data class LetterItem(
    val letter: String,
    val word: String,
    val imageRes: Int
)

@Composable
fun AlphabetGameScreen() {
    val context = LocalContext.current

    // 🗣️ Inicializa TextToSpeech correctamente
    val tts = remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.value?.language = Locale("es", "ES")
            }
        }
        tts.value = ttsInstance

        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
        }
    }

    // 🔤 Lista del abecedario (ejemplo)
    val alphabetList = listOf(
        LetterItem("A", "Árbol", R.drawable.arbol),
       /* LetterItem("B", "Barco", R.drawable.barco),
        LetterItem("C", "Casa", R.drawable.casa),
        LetterItem("D", "Dado", R.drawable.dado),
        LetterItem("E", "Elefante", R.drawable.elefante),
        LetterItem("F", "Flor", R.drawable.flor),
        LetterItem("G", "Gato", R.drawable.gato),
        LetterItem("H", "Helado", R.drawable.helado),
        LetterItem("I", "Isla", R.drawable.isla),
        LetterItem("J", "Jirafa", R.drawable.jirafa),
        LetterItem("K", "Koala", R.drawable.koala),
        LetterItem("L", "Libro", R.drawable.libro),
        LetterItem("M", "Manzana", R.drawable.manzana),
        LetterItem("N", "Nube", R.drawable.nube),
        LetterItem("Ñ", "Ñandú", R.drawable.nandu),
        LetterItem("O", "Oso", R.drawable.oso),
        LetterItem("P", "Pato", R.drawable.pato),
        LetterItem("Q", "Queso", R.drawable.queso),
        LetterItem("R", "Ratón", R.drawable.raton),
        LetterItem("S", "Sol", R.drawable.sol),
        LetterItem("T", "Tigre", R.drawable.tigre),
        LetterItem("U", "Uva", R.drawable.uva),
        LetterItem("V", "Vaca", R.drawable.vaca),
        LetterItem("W", "Waffle", R.drawable.waffle),
        LetterItem("X", "Xilófono", R.drawable.xilofono),
        LetterItem("Y", "Yate", R.drawable.yate),
        LetterItem("Z", "Zorro", R.drawable.zorro)*/
    )

    // 📱 Detecta ancho de pantalla para hacer el grid más flexible
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val columns = if (screenWidth < 600) 3 else 4 // 3 columnas en teléfono, 4 en tablet

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text(
            text = "Aprendamos el abecedario",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(alphabetList) { item ->
                AlphabetCard(item) {
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

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "card_scale_anim"
    )

    Card(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .aspectRatio(1f) // cuadrado perfecto
            .clickable(
                onClick = {
                    isPressed = true
                    onClick()
                },
                onClickLabel = "Tocar imagen de ${item.word}"
            ),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔹 Imagen que se redimensiona sola dentro de la card
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = "${item.word} - ${item.letter}",
                modifier = Modifier
                    .fillMaxSize(0.75f), // 75% del tamaño de la card
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.word,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }

    // 🔁 Animación pequeña al presionar
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}
