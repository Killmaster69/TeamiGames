package com.example.teamigames.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import com.example.teamigames.R

enum class GameColor(val imageRes: Int) {
    RED(R.drawable.rojo),
    BLUE(R.drawable.azul),
    GREEN(R.drawable.verde),
    LILAC(R.drawable.rosa),
    DARK_BLUE(R.drawable.azul_rey),
    ORANGE(R.drawable.naranja)
}

@SuppressLint("Range")
@Composable
fun PatternGameScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale("es", "ES")
            }
        }
        onDispose {
            ttsEngine?.stop()
            ttsEngine?.shutdown()
        }
    }

    fun speak(text: String, isSuccess: Boolean? = null) {
        val engine = ttsEngine ?: return
        when (isSuccess) {
            true -> { engine.setPitch(1.3f); engine.setSpeechRate(1.1f) }
            false -> { engine.setPitch(0.9f); engine.setSpeechRate(0.9f) }
            else -> { engine.setPitch(1.0f); engine.setSpeechRate(1.0f) }
        }
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "feedback")
    }

    val successPhrases = listOf(
        "¡Excelente trabajo!", "¡Muy bien!", "¡Lo estás logrando!", "¡Qué gran acierto!",
        "¡Sigue así!", "¡Perfecto!", "¡Eres un campeón!", "¡Vas muy bien!", "¡Impresionante!"
    )
    val failPhrases = listOf(
        "Casi, pero no ahí.", "Intenta otra vez.", "Ups, prueba en otro lugar.",
        "No te preocupes, sigue intentando.", "Ahí no va.", "Casi lo logras."
    )

    var correctCount by remember { mutableStateOf(0) }
    var failCount by remember { mutableStateOf(0) }
    var streakCount by remember { mutableStateOf(0) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var elapsedTime by remember { mutableStateOf(0L) }

    fun playSuccess() {
        correctCount++
        streakCount++
        elapsedTime = System.currentTimeMillis() - startTime
        speak(successPhrases.random(), true)
    }

    fun playFailure() {
        failCount++
        streakCount = 0
        speak(failPhrases.random(), false)
    }

    fun generatePattern(): List<GameColor> = GameColor.values().toList().shuffled()

    var pattern by remember { mutableStateOf(generatePattern()) }
    var placed by remember { mutableStateOf(List(pattern.size) { null as GameColor? }) }
    var selectedColor by remember { mutableStateOf<GameColor?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun resetGame() {
        pattern = generatePattern()
        placed = List(pattern.size) { null }
        selectedColor = null
        correctCount = 0
        failCount = 0
        streakCount = 0
        startTime = System.currentTimeMillis()
    }

    fun checkComplete() {
        if (placed.all { it != null }) {
            showDialog = true
            speak("¡Felicidades, completaste el patrón!", true)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color.White)
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFEBEE), Color(0xFFFFFDE7))
                )
            }
        )
    ) {
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            PlayeraAndSlots(
                pattern = pattern,
                placed = placed,
                selectedColor = selectedColor,
                onSlotClick = { index: Int ->
                    if (selectedColor == pattern[index]) {
                        placed = placed.toMutableList().also { it[index] = selectedColor }
                        playSuccess()
                        checkComplete()
                    } else {
                        playFailure()
                    }
                },
                slotSize = 120.dp,
                slotSpacing = 14.dp
            )
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(modifier = Modifier.fillMaxSize()) {
                PlayeraAndSlots(
                    pattern = pattern,
                    placed = placed,
                    selectedColor = selectedColor,
                    onSlotClick = { index: Int ->
                        if (selectedColor == pattern[index]) {
                            placed = placed.toMutableList().also { it[index] = selectedColor }
                            playSuccess()
                            checkComplete()
                        } else {
                            playFailure()
                        }
                    },
                    slotSize = 90.dp,
                    slotSpacing = 2.dp,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFFFFDE7)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    for (row in 0 until 2) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                if (index < GameColor.values().size) {
                                    val gc = GameColor.values()[index]
                                    ColorButton(
                                        color = gc,
                                        isSelected = (selectedColor == gc),
                                        onClick = { selectedColor = gc }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { resetGame() }) {
                        Text("Reiniciar")
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Felicidades!") },
            text = { Text("Completaste el patrón.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false; resetGame() }) { Text("Volver a jugar") }
            }
        )
    }
}

@Composable
fun PlayeraAndSlots(
    pattern: List<GameColor>,
    placed: List<GameColor?>,
    selectedColor: GameColor?,
    onSlotClick: (Int) -> Unit,
    slotSize: Dp,
    slotSpacing: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.playera),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight(0.95f)
                .fillMaxWidth(1.2f)
                .align(Alignment.Center),
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 121.dp),
            verticalArrangement = Arrangement.spacedBy(slotSpacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (row in 0 until 3) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (col in 0 until 2) {
                        val index = row * 2 + col
                        val canClickSlot = selectedColor != null && placed[index] == null
                        SlotWithPattern(
                            patternColor = pattern[index],
                            placedColor = placed[index],
                            canClick = canClickSlot,
                            onSlotClick = { onSlotClick(index) },
                            size = slotSize
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlotWithPattern(
    patternColor: GameColor,
    placedColor: GameColor?,
    canClick: Boolean,
    onSlotClick: () -> Unit,
    size: Dp
) {
    var pressed by remember { mutableStateOf(false) }
    var placedAnim by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(if (pressed) 1.15f else 1f, tween(150))
    val fallScale by animateFloatAsState(if (placedAnim) 1f else 1.8f, tween(350))
    val alpha by animateFloatAsState(if (placedAnim) 1f else 0f, tween(350))

    Box(
        modifier = Modifier
            .size(size)
            .scale(scale)
            .clickable(enabled = isClickable && canClick) {
                isClickable = false
                pressed = true
                placedAnim = true
                onSlotClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = patternColor.imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(size)
                .alpha(0.35f),
            contentScale = ContentScale.Fit
        )
        if (placedColor != null) {
            Image(
                painter = painterResource(id = placedColor.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(size)
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

@Composable
fun ColorButton(color: GameColor, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(80.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.LightGray.copy(alpha = 0.5f) else Color.White
        )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = color.imageRes),
                contentDescription = null,
                modifier = Modifier.size(65.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}
