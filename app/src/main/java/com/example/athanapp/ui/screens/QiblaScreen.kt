package com.example.athanapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.athanapp.R
import com.example.athanapp.ui.theme.Typography
import com.example.compose.md_theme_light_tertiaryContainer

@Composable()
fun QiblaMenu(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.rectangle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // This makes the image scale to the size of the Box
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Row {
                Text(
                    text = "Boston",
                    style = Typography.displayLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.padding(7.dp))
                Image(
                    painter = painterResource(id = R.drawable.location_symbol),
                    contentDescription = "",
                    modifier = Modifier
                        .size(29.dp)
                        .align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.padding(7.dp))
            Text(
                text = "40.741895, -73.989308",
                style = Typography.displayMedium,
                color = Color(156, 180, 216)
            )
            Spacer(modifier = Modifier.padding(52.5.dp))
            Image(
                painter = painterResource(id = R.drawable.compass),
                contentDescription = "",
                modifier = Modifier.size(265.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            Box() {
                Image(
                    painter = painterResource(id = R.drawable.oval),
                    contentDescription = "",
                    modifier = Modifier.size(90.dp)
                )
                Text(
                    text = "0°",
                    style = Typography.displayLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview
@Composable
private fun QiblaPreview() {
    QiblaMenu()
}