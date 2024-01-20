package com.example.athanapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import java.text.SimpleDateFormat
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    athanUiState: AthanViewModel.AthanUiState,
    preferencesUiState: PreferencesUiState,
    cityName: String?
) {
    val currentPrayer = athanUiState.currentPrayer
    val timeLeft = athanUiState.timeLeft

    val darkMode = preferencesUiState.isDarkMode

    val background = if (darkMode) {
        R.drawable.blue_background
    } else {
        R.drawable.light_background_main
    }

    val textColor = if (darkMode) {
        Typography.displayMedium.color
    } else {
        Color(31, 33, 56)
    }

    val containerColor = if (darkMode) {
        Color(156, 180, 216)
    } else {
        Color(213, 182, 216)
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
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = currentPrayer,
                style = Typography.displayLarge,
                color = textColor
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = "Next Prayer in $timeLeft",
                style = Typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrayerTimeList(athanUiState, textColor, containerColor)

                // City and Location Icon
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Today - $cityName", color = Color.White, style = Typography.displayMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = locationIndicator), contentDescription = "", Modifier.size(29.dp))
                }
            }
        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            navController = navHostController
        )

    }
}

@Composable
private fun PrayerTimeList(
    athanUiState: AthanViewModel.AthanUiState,
    textColor: Color,
    containerColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Current Date
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            )
        ) {
            Text(
                text = athanUiState.prayerEntity.readable,
                style = Typography.displayMedium,
                color = textColor,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        // Prayer times
        Card(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                PrayerRow("Fajr", athanUiState.prayerEntity.fajr, textColor)
                PrayerRow("Sunrise", athanUiState.prayerEntity.sunrise, textColor)
                PrayerRow("Dhuhr", athanUiState.prayerEntity.dhuhr, textColor)
                PrayerRow("Asr", athanUiState.prayerEntity.asr, textColor)
                PrayerRow("Maghrib", athanUiState.prayerEntity.maghrib, textColor)
                PrayerRow("Isha", athanUiState.prayerEntity.isha, textColor)
            }
        }

    }

}

@Composable
private fun PrayerRow(name: String, time: String, textColor: Color) {
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()
    val timeWithoutTimeZone = time.substringBefore(" (")

    val is12Hour = preferencesUiState.is12Hour

    val timeFormat24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeFormat12 = SimpleDateFormat("h:mm a", Locale.getDefault())
    var formattedTime = timeWithoutTimeZone

    if (timeWithoutTimeZone.isNotEmpty()) {
        val date = timeFormat24.parse(timeWithoutTimeZone)
        if (is12Hour && date != null) {
            formattedTime = timeFormat12.format(date)
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = name, modifier = Modifier.weight(1f), style = Typography.displayMedium, color = textColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = formattedTime, style = Typography.displayMedium)
    }
}


//@Preview
//@Composable
//fun HomeScreenPreview() {
//    HomeBody(navHostController = rememberNavController())
//}
