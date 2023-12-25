package com.example.athanapp.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.unit.sp
import com.example.athanapp.R
import com.example.athanapp.ui.theme.Typography


@Composable
fun HomeBody(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.blue_background),
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
                text = "Isha.",
                style = Typography.displayLarge,
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Text(
                text = "Next Prayer in 2:00:19",
                style = Typography.displayMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.padding(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PrayerTimeList()

                // City and Location Icon
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(text = "Today - Boston", color = Color.White, style = Typography.displayMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(painter = painterResource(id = R.drawable.location_symbol), contentDescription = "", Modifier.size(29.dp))
                }
            }
        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PrayerTimeList() {
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
                text = "Friday 22nd December",
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
                PrayerRow("Fajr", "5:44 am")
                PrayerRow("Sunrise", "7:09 am")
                PrayerRow("Dhuhr", "11:42 am")
                PrayerRow("Asr", "1:56 pm")
                PrayerRow("Maghrib", "4:14 pm")
                PrayerRow("Isha", "5:40 pm")
            }
        }

        // Notification bells
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
            // Add your content for Notification bells here
        }
    }
}

@Composable
private fun PrayerRow(name: String, time: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(text = name, modifier = Modifier.weight(1f), style = Typography.displayMedium)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = time, style = Typography.displayMedium)
    }
}

@Composable
fun BottomNavigation(modifier: Modifier) {
    BottomAppBar(
        modifier = modifier
            .height(56.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 3.dp, bottom = 3.dp)
        ) {
        Image(
            painter = painterResource(R.drawable.home_icon),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.qibla_icon),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.filler_icon),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Image(painter = painterResource(id = R.drawable.settings_icon),
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        }
    }

}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeBody()
}
