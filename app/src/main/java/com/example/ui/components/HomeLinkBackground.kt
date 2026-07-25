package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun HomeLinkBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B192C), // Deep Tech Navy Blue top
                        Color(0xFF0F3460), // Royal Navy Blue
                        Color(0xFF1652F0), // Vibrant Electric Blue
                        Color(0xFF0066FF), // Brilliant Sapphire Blue
                        Color(0xFF4A90E2), // Sky Blue
                        Color(0xFF90CAF9), // Light Ice Blue
                        Color(0xFFEBF4EC), // Soft White Tint
                        Color(0xFFFFFFFF)  // Pure White bottom
                    )
                )
            )
    ) {
        // Decorative Canvas with ambient soft glowing white and blue orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top-right glowing white radiance
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.85f, h * 0.12f),
                    radius = w * 0.70f
                )
            )

            // Center-left vibrant electric blue glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f, h * 0.45f),
                    radius = w * 0.65f
                )
            )

            // Bottom crisp white light aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.88f),
                    radius = w * 0.80f
                )
            )
        }

        // Watermark Logo Backdrop in center
        Image(
            painter = painterResource(id = R.drawable.img_homelink_logo),
            contentDescription = "Home Link Network Logo Background",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize(0.70f)
                .align(Alignment.Center)
                .alpha(0.08f)
        )

        content()
    }
}
