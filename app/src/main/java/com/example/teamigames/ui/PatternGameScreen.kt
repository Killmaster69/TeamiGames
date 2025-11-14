package com.example.teamigames.ui

import android.annotation.SuppressLint
import android.speech.tts.TextToSpeech
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import com.example.teamigames.R

// ⭐ Ahora cada color usa la imagen real
enum class GameColor(val label: String, val imageRes: Int) {
    RED("Rojo", R.drawable.rojo),
    BLUE("Azul", R.drawable.azul),
    GREEN("Verde", R.drawable.verde),
    LILAC("Rosa", R.drawable.rosa),
    DARK_BLUE("Azul Rey", R.drawable.azul_rey),
    ORANGE("Naranja", R.drawable.naranja)
}

@SuppressLint("Range")
@Composable
fun PatternGameScreen() {

    // ⭐ Inicialización del TTS
    val context = LocalContext.current
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        var localEngine: TextToSpeech? = null
        localEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                localEngine?.language = Locale("es", "ES")
            }
        }
        ttsEngine = localEngine

        onDispose {
            localEngine?.stop()
            localEngine?.shutdown()
        }
    }


    fun speak(text: String, isSuccess: Boolean? = null) {
        val engine = ttsEngine ?: return

        when (isSuccess) {
            true -> {
                engine.setPitch(1.3f)
                engine.setSpeechRate(1.1f)
            }
            false -> {
                engine.setPitch(0.9f)
                engine.setSpeechRate(0.9f)
            }
            else -> {
                engine.setPitch(1.0f)
                engine.setSpeechRate(1.0f)
            }
        }

        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "feedback")
    }

    // Respuestas
    val successPhrases = listOf(
        "¡Excelente trabajo!", "¡Muy bien!", "¡Lo estás logrando!",
        "¡Qué gran acierto!", "¡Sigue así!", "¡Perfecto!",
        "¡Eres un campeón!", "¡Vas muy bien!", "¡Impresionante!",
        "¡Cada vez mejor!", "¡Qué bonito color elegiste!",
        "¡Esa era la correcta!", "¡Buen ojo!", "¡Te salió genial!",
        "¡Increíble!", "¡Esa fue muy buena!", "¡Tu memoria es excelente!",
        "¡Lo hiciste con mucha precisión!", "¡Súper trabajo!",
        "¡Me encanta cómo lo haces!"
    )

    val failPhrases = listOf(
        "Casi, pero no ahí.", "Intenta otra vez.", "Ups, prueba en otro lugar.",
        "No te preocupes, sigue intentando.", "Ahí no va.", "Casi lo logras.",
        "Piensa bien, puedes hacerlo.", "No pasa nada, inténtalo de nuevo.",
        "Esa no era, pero vas bien.", "Estás muy cerca.", "No te rindas.",
        "Sigue probando.", "Esa no era, pero estás aprendiendo.",
        "Vamos, tú puedes.", "Trata de recordar el patrón.",
        "Tómate tu tiempo, no hay prisa.", "Respira y vuelve a intentarlo.",
        "Esa no coincidía, busca otra.", "Recuerda los colores anteriores.",
        "Confía en ti, puedes hacerlo."
    )

    // Estado del juego
    var correctCount by remember { mutableStateOf(0) }
    var failCount by remember { mutableStateOf(0) }
    var streakCount by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedTime by remember { mutableStateOf(0L) }

    fun playSuccess(): String {
        correctCount++
        streakCount++
        elapsedTime = System.currentTimeMillis() - startTime

        val phrase = when {
            streakCount >= 5 -> listOf(
                "¡Racha impresionante!", "¡Cinco aciertos seguidos!", "¡Estás en fuego!", "¡No paras de acertar!"
            ).random()

            correctCount >= 10 -> listOf(
                "¡Eres increíble!", "¡Qué memoria tan buena!", "¡Ya casi completas todo!", "¡Brillante!"
            ).random()

            correctCount >= 5 -> listOf(
                "¡Vas genial!", "¡Muy bien hecho!", "¡Esa fue muy buena!", "¡Sigue así!"
            ).random()

            else -> successPhrases.random()
        }

        speak(phrase, true)
        return phrase
    }

    fun playFailure(): String {
        failCount++
        streakCount = 0

        val phrase = when {
            failCount >= 10 -> listOf(
                "Está bien equivocarse, lo estás intentando muy bien.",
                "No te rindas, cada intento te acerca más.",
                "Sigue con calma, lo lograrás."
            ).random()

            failCount >= 5 -> listOf(
                "A veces cuesta, pero puedes hacerlo.",
                "No pasa nada, inténtalo de nuevo.",
                "Vamos, tú puedes lograrlo."
            ).random()

            else -> failPhrases.random()
        }

        speak(phrase, false)
        return phrase
    }

    fun generatePattern(): List<GameColor> = GameColor.values().toList().shuffled()

    var pattern by remember { mutableStateOf(generatePattern()) }
    var placed by remember { mutableStateOf(List<GameColor?>(pattern.size) { null }) }
    var selectedColor by remember { mutableStateOf<GameColor?>(null) }
    var feedbackText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    fun resetGame() {
        pattern = generatePattern()
        placed = List(pattern.size) { null }
        selectedColor = null
        feedbackText = ""
        correctCount = 0
        failCount = 0
        streakCount = 0
        startTime = System.currentTimeMillis()
    }

    fun checkComplete() {
        if (placed.all { it != null }) {
            showDialog = true
            val seconds = (elapsedTime / 1000).coerceAtLeast(1)
            speak("¡Felicidades, completaste el patrón en $seconds segundos!", null)
        }
    }

    // ⭐ LAYOUT PRINCIPAL
    Box(modifier = Modifier.fillMaxSize()) {

        // 🧥 Playera como fondo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.playera),
                contentDescription = "Playera de fondo",
                modifier = Modifier
                    .fillMaxWidth(1.2f)
                    .fillMaxHeight(0.95f)
                    .align(Alignment.Center),
                contentScale = ContentScale.FillHeight
            )

            // ⭐ Slots sobre la playera
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 70.dp, vertical = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until 2) {
                            val index = row * 2 + col
                            val canClickSlot = selectedColor != null && placed[index] == null

                            SlotWithPattern(
                                patternColor = pattern[index],
                                placedColor = placed[index],
                                canClick = canClickSlot,
                                onSlotClick = {
                                    if (selectedColor == null) return@SlotWithPattern

                                    if (selectedColor == pattern[index]) {
                                        placed = placed.toMutableList().also { it[index] = selectedColor }
                                        feedbackText = playSuccess()
                                        checkComplete()
                                    } else {
                                        feedbackText = playFailure()
                                        scope.launch {
                                            delay(900)
                                            feedbackText = ""
                                        }
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        // ⭐ Panel inferior
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.9f))
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = feedbackText, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // 🎨 Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GameColor.values().forEach { gc ->
                    ColorButton(
                        color = gc,
                        isSelected = (selectedColor == gc),
                        onClick = { selectedColor = gc }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { resetGame() }) { Text("Reiniciar") }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Aciertos: $correctCount | Errores: $failCount | Racha: $streakCount",
                fontSize = 14.sp
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Felicidades!") },
            text = { Text("Completaste el patrón en ${(elapsedTime / 1000)} segundos.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    resetGame()
                }) {
                    Text("Volver a jugar")
                }
            }
        )
    }
}

/////////////////////////////////////////////////////////////////////////////////////
// ⭐ SLOT SIN CARD — solo imagen transparente + animación de caída real
/////////////////////////////////////////////////////////////////////////////////////

@Composable
fun SlotWithPattern(
    patternColor: GameColor,
    placedColor: GameColor?,
    canClick: Boolean,
    onSlotClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    var placedAnim by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(if (pressed) 1.15f else 1f, tween(150))
    val fallScale by animateFloatAsState(if (placedAnim) 1f else 1.8f, tween(350))
    val alpha by animateFloatAsState(if (placedAnim) 1f else 0f, tween(350))

    Box(
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clickable(enabled = isClickable && canClick) {
                isClickable = false
                pressed = true
                placedAnim = true
                onSlotClick()
            },
        contentAlignment = Alignment.Center
    ) {
        // 🎯 Patrón tenue (slot vacío)
        Image(
            painter = painterResource(id = patternColor.imageRes),
            contentDescription = patternColor.label,
            modifier = Modifier
                .size(120.dp)
                .alpha(0.50f),
            contentScale = ContentScale.Fit
        )
        // 🟢 Botón colocado con animación de caída
        if (placedColor != null) {
            Image(
                painter = painterResource(id = placedColor.imageRes),
                contentDescription = placedColor.label,
                modifier = Modifier
                    .size(120.dp)
                    .scale(fallScale)
                    .alpha(alpha),
                contentScale = ContentScale.Fit
            )
        }
    }

    LaunchedEffect(pressed, placedColor) {
        if (pressed) {
            delay(300)
            pressed = false
            delay(200)
            if (placedColor == null) isClickable = true
        } else if (placedColor == null) {
            isClickable = true
        }
    }
}

/////////////////////////////////////////////////////////////////////////////////////
// ⭐ BOTONES INFERIORES
/////////////////////////////////////////////////////////////////////////////////////

@Composable
fun ColorButton(color: GameColor, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Card(
            modifier = Modifier
                .size(60.dp)
                .clickable { onClick() },
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    Color.LightGray.copy(alpha = 0.5f)
                else Color.White
            )
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = color.imageRes),
                    contentDescription = color.label,
                    modifier = Modifier.size(35.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(text = color.label, fontSize = 11.sp)
    }
}
