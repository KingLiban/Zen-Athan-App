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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
    preferencesUiState: PreferencesUiState,
    cityName: String?,
) {

    val direction = preferencesUiState.direction
    val sensorUiState by sensorViewModel.uiState.collectAsState()
    val azimuthValue = sensorUiState.azimuth
    val darkMode = preferencesUiState.isDarkMode

    val background = if (darkMode) {
        R.drawable.rectangle
    } else {
        R.drawable.light_background
    }

    val color = if (darkMode) {
        Typography.displayMedium.color
    } else {
        Color(31, 33, 56)
    }

    val textColor = if (darkMode) {
        Color(156, 180, 216)
    } else {
        Color(213, 182, 216)
    }

    val image = if (darkMode) {
        R.drawable.compass
    } else {
        R.drawable.light_compass
    }

    val locationIndicator = if(darkMode) {
        R.drawable.location_symbol
    } else {
        R.drawable.location_light
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
                    painter = painterResource(id = locationIndicator),
                    contentDescription = "",
                    modifier = Modifier
                        .size(29.dp)
                        .align(Alignment.CenterVertically)
                )   
            }

            Spacer(modifier = Modifier.padding(7.dp))

            val truncatedLatitude = String.format("%.6f", preferencesUiState.latitude)
            val truncatedLongitude = String.format("%.6f", preferencesUiState.longitude)

            Text(
                text = "$truncatedLatitude, $truncatedLongitude",
                style = Typography.displayMedium,
                color = textColor
            )

            Spacer(modifier = Modifier.padding(52.5.dp))
            Image(
                painter = painterResource(id = image),
                contentDescription = "",
                modifier = Modifier
                    .size(265.dp)
                    .rotate(azimuthValue)
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
                    color = color,
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

