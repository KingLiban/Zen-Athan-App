package com.example.athanapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.athanapp.R
import com.example.athanapp.ui.navigation.BottomNavigation
import com.example.athanapp.ui.theme.Typography
import kotlin.math.roundToInt

@Composable()
fun QiblaMenu(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    sensorViewModel: SensorViewModel,
) {
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()
    val cityName = preferencesUiState.cityName
    val direction = preferencesUiState.direction
    val sensorUiState by sensorViewModel.uiState.collectAsState()
    val azimuthValue = sensorUiState.azimuth
    val darkMode = preferencesUiState.isDarkMode

    val background = if (darkMode) {
        R.drawable.rectangle
    } else {
        R.drawable.light_background
    }

    val textColor = if (darkMode) {
        Color(156, 180, 216)
    } else {
        Color(31, 33, 56)
    }

    val image = if (darkMode) {
        R.drawable.compass
    } else {
        R.drawable.light_compass
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Row {
                Text(
                    text = "$cityName",
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

            val compassTextColor = if (darkMode) {
                Color(156, 180, 216)
            } else {
                Color(213, 182, 216)
            }
            Text(
                text = "${preferencesUiState.latitude}, ${preferencesUiState.longitude}",
                style = Typography.displayMedium,
                color = compassTextColor
            )
            Spacer(modifier = Modifier.padding(52.5.dp))
            Image(
                painter = painterResource(id = image),
                contentDescription = "",
                modifier = Modifier
                    .size(265.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))

            Box() {
                Image(
                    painter = painterResource(id = R.drawable.oval),
                    contentDescription = "",
                    modifier = Modifier.size(90.dp)
                )
                Text(
                    text = "${azimuthValue.roundToInt()}°",
                    style = Typography.displayLarge,
                    color = textColor,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.padding(20.dp))

            if (azimuthValue.toDouble() in (direction-5)..(direction+5)) {
                Text(
                    text = "You're facing Mecca!",
                    style = Typography.displayMedium,
                    color = textColor
                )
            }

        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            navController = navController
        )
    }
}


//@Preview
//@Composable
//private fun QiblaPreview() {
//    QiblaMenu(navController = rememberNavController(), sensorViewModel = sensorViewModel)
//}

