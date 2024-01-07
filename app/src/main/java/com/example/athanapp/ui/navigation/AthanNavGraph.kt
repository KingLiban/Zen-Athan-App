package com.example.athanapp.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.athanapp.R
import com.example.athanapp.ui.screens.HomeBody
import com.example.athanapp.ui.screens.QiblaMenu
import com.example.athanapp.ui.screens.SettingsPage

enum class Athan {
    Home,
    Qibla,
//    Mystery(title = R.string.mystery),
    Settings
}

@Composable
fun AthanApp(
) {
    val navController: NavHostController = rememberNavController()
    var startDestination = Athan.Home.name

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Athan.Home.name) {
            HomeBody(
                modifier = Modifier,
                navController
            )
        }

        composable(route = Athan.Qibla.name) {
            QiblaMenu(
                modifier = Modifier,
                navController
            )
        }

        composable(route = Athan.Settings.name) {
            SettingsPage(
                modifier = Modifier,
                navController
            )
        }
    }
}

@Composable
fun BottomNavigation(navController: NavHostController, modifier: Modifier) {
    BottomAppBar(
        modifier = modifier
            .height(56.dp),
        containerColor = Color.White
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 3.dp, bottom = 3.dp)
        ) {
            // Home icon
            ClickableImageWithText(
                imageResId = R.drawable.home_icon,
                text = "Home",
                onClick = { navController.navigate(Athan.Home.name) }
            )

            // Qibla icon
            ClickableImageWithText(
                imageResId = R.drawable.qibla_icon,
                text = "Qibla",
                onClick = { navController.navigate(Athan.Qibla.name) }
            )

            // Filler icon (Replace with the actual functionality)
            ClickableImageWithText(
                imageResId = R.drawable.filler_icon,
                text = "Filler",
                onClick = { /* Handle filler click action */ }
            )

            // Settings icon
            ClickableImageWithText(
                imageResId = R.drawable.settings_icon,
                text = "Settings",
                onClick = { navController.navigate(Athan.Settings.name) }
            )
        }
    }
}

@Composable
fun ClickableImageWithText(
    imageResId: Int,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(imageResId),
            contentDescription = "",
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = text,
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        )
    }
}