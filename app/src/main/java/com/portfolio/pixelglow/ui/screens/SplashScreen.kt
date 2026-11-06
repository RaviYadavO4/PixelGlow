package com.portfolio.pixelglow.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun AnimatedSplashScreenPreview() {
    AnimatedSplashScreen()
}

@Composable
fun AnimatedSplashScreen() {
    // 🌟 Infinite Logo Pulse Scaling Animation Logic
    val infiniteTransition = rememberInfiniteTransition(label = "SplashScale")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070709)), // Premium Pitch Black
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glowing Neon AI Icon Logo Container
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(scale)
                    .background(Color(0xFF00ADB5).copy(alpha = 0.15f), CircleShape)
                    .border(1.5.dp, Color(0xFF00ADB5), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Logo",
                    tint = Color(0xFF00ADB5),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Premium Animated Branding Text
            Text(
                text = "PixelGlow",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Creative Studio Pro",
                color = Color.Gray,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
