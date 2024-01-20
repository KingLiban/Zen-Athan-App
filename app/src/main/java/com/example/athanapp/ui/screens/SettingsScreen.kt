package com.example.athanapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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


@Composable()
fun SettingsPage(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    preferencesViewModel: PreferencesViewModel,
    preferencesUiState: PreferencesUiState,
) {

    val darkMode = preferencesUiState.isDarkMode

    val background = if (darkMode) {
        R.drawable.rectangle
    } else {
        R.drawable.light_background
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
                text = "Settings",
                style = Typography.displayLarge,
                color = Color.White
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(50.dp)
                .fillMaxSize(),
        ) {
            Spacer(modifier = Modifier.padding(40.dp))
            Image(
                painter = painterResource(id = R.drawable.my_logo),
                contentDescription = "",
                modifier = Modifier
                    .size(136.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.padding(20.dp))
            SettingCard("General Settings", false, containerColor, textColor)
            Spacer(modifier = Modifier.padding(3.dp))
            SettingCard("Prayer Times", false, containerColor, textColor)
//            Spacer(modifier = Modifier.padding(3.dp))
//            SettingCard("Notifications", false, preferencesViewModel::onNotificationsClicked)
            Spacer(modifier = Modifier.padding(20.dp))
            SettingCard("About", true, containerColor, textColor)
        }
        BottomNavigation(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            navController = navHostController
        )
    }

}

@Composable
private fun SettingCard(content: String, boolean: Boolean, containerColor: Color, textColor: Color) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
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
                    color = textColor,
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

        if (expanded) {
            when (content) {
                "General Settings" -> GeneralSettingsExpanded()
                "Prayer Times" -> PrayerTimesExpanded()
                "Notifications" -> NotificationsExpanded()
            }
        }
    }
}


@Composable
private fun GeneralSettingsExpanded() {
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()

    val checked = preferencesUiState.isDarkMode
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { preferencesViewModel.onGeneralSettingsClicked(checked) }
        )
        Text(
            text = "Dark Mode",
        )
    }
}

@Composable
private fun PrayerTimesExpanded() {
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()

    val checked = preferencesUiState.is12Hour
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { preferencesViewModel.onPrayerTimesClicked(checked) }
        )
        Text(
            text = "12 Hour Time",
        )
    }
}

//@Composable
//private fun AboutExpanded() {
//    Text(text =  )
//}

@Composable
private fun NotificationsExpanded() {
    val preferencesViewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModel.Factory)
    val preferencesUiState by preferencesViewModel.uiState.collectAsState()

    var checked by remember { mutableStateOf(false) }
    Row {
        Checkbox(checked = checked, onCheckedChange = { checked = it })
        Text(text = "Notifications")
    }
}

//@Preview
//@Composable
//private fun SettingsPreview() {
//    SettingsPage(navHostController = rememberNavController())
//}