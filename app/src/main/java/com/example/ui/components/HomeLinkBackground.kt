package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
            .background(Color.Black)
    ) {
        // Watermark Logo Backdrop in center
        Image(
            painter = painterResource(id = R.drawable.img_homelink_logo),
            contentDescription = "Home Link Network Logo Background",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize(0.65f)
                .align(Alignment.Center)
                .alpha(0.06f)
        )

        content()
    }
}

