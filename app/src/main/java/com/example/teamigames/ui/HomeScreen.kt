package com.example.teamigames.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.teamigames.R
import com.example.teamigames.nav.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Juegos Educativos") }
            )
        },
        containerColor = androidx.compose.ui.graphics.Color.White
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(androidx.compose.ui.graphics.Color.White),
            contentAlignment = Alignment.Center
        ) {
            // Siempre horizontal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageButton(
                    resId = R.drawable.patron,
                    onClick = { navController.navigate(Routes.Pattern.route) },
                    modifier = Modifier.weight(1f)
                )
                ImageButton(
                    resId = R.drawable.abecedario,
                    onClick = { navController.navigate(Routes.Alphabet.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ImageButton(resId: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        modifier = modifier
            .aspectRatio(1.6f)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    )
}
