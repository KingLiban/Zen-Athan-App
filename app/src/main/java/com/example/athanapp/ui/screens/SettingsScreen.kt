package com.example.athanapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun SettingsPage(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.rectangle),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // This makes the image scale to the size of the Box
        )
        Column(
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = "Settings",
                style = Typography.displayMedium,
                color = Color.White
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.padding(30.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_placeholder),
                contentDescription = "",
                modifier = Modifier.size(136.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            SettingCards()
            Spacer(modifier = Modifier.padding(20.dp))
            SettingCard("About", {}, true)
        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SettingCard(content: String, clickAction: () -> Unit, boolean: Boolean) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable { clickAction.invoke() }, // Adding clickable modifier
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(156, 180, 216)
        )
    ) {
        if (boolean) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.down_arrow),
                    contentDescription = "",
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.CenterStart)
                        .padding(start = 15.dp)
                )
                Text(
                    text = content,
                    style = Typography.displayMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }


        } else {
            Text(
                text = content,
                style = Typography.displayMedium,
                modifier = Modifier
                    .padding(16.dp)
            )
        }

    }
}

@Composable
private fun SettingCards() {
    val contents = listOf("General Settings", "Prayer Times", "Notifications")

    contents.forEach { content ->
        SettingCard(content = content, clickAction = {
            // Handle the click action here
            // You can navigate to another screen or perform any action you need
        }, false)
        Spacer(modifier = Modifier.padding(3.dp))
    }
}

@Preview
@Composable
private fun SettingsPreview() {
    SettingsPage()
}