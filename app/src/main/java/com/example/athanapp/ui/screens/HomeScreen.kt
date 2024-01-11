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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.athanapp.R
import com.example.athanapp.ui.navigation.BottomNavigation
import com.example.athanapp.ui.theme.Typography


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    val athanViewModel: AthanViewModel = viewModel(factory = AthanViewModel.Factory)
    val athanUiState by athanViewModel.uiState.collectAsState()
    val currentPrayer = athanUiState.currentPrayer
    val timeLeft = athanUiState.timeLeft
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()
    val cityName = preferencesUiState.cityName

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.blue_background),
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
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = "Next Prayer in $timeLeft",
                style = Typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrayerTimeList(athanUiState)

                // City and Location Icon
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Today - $cityName", color = Color.White, style = Typography.displayMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = R.drawable.location_symbol), contentDescription = "", Modifier.size(29.dp))
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
private fun PrayerTimeList(athanUiState: AthanViewModel.AthanUiState) {
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
                containerColor = Color(156, 180, 216)
            )
        ) {
            Text(
                text = athanUiState.prayerEntity.readable,
                style = Typography.displayMedium,
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
                containerColor = Color(156, 180, 216)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                PrayerRow("Fajr", athanUiState.prayerEntity.fajr)
                PrayerRow("Sunrise", athanUiState.prayerEntity.sunrise)
                PrayerRow("Dhuhr", athanUiState.prayerEntity.dhuhr)
                PrayerRow("Asr", athanUiState.prayerEntity.asr)
                PrayerRow("Maghrib", athanUiState.prayerEntity.maghrib)
                PrayerRow("Isha", athanUiState.prayerEntity.isha)
            }
        }

    }

}

@Composable
private fun PrayerRow(name: String, time: String) {
    val timeWithoutTimeZone = time.substringBefore(" (")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = name, modifier = Modifier.weight(1f), style = Typography.displayMedium)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = timeWithoutTimeZone, style = Typography.displayMedium)
    }
}



//@Preview
//@Composable
//fun HomeScreenPreview() {
//    HomeBody(navHostController = rememberNavController())
//}
