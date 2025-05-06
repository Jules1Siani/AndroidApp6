package com.louiserennick.countrytreasurehunt

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.louiserennick.countrytreasurehunt.ui.theme.CountryTreasureHuntTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountryTreasureHuntTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationsScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun LocationsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var locations by remember { mutableStateOf(dbHelper.getAllLocations()) }
    var rewardMessage by remember { mutableStateOf(getRewardMessage(dbHelper, locations)) }
    var showCongratsDialog by remember { mutableStateOf(false) }
    var hasPlayedSound by remember { mutableStateOf(false) }

    // Load the success sound
    val mediaPlayer = remember {
        MediaPlayer.create(context, R.raw.success)
    }

    val foundCount = dbHelper.countFoundLocations()

    // Watch for changes and trigger dialog/sound once
    LaunchedEffect(locations) {
        rewardMessage = getRewardMessage(dbHelper, locations)
        if (foundCount == locations.size && locations.isNotEmpty() && !hasPlayedSound) {
            showCongratsDialog = true
            hasPlayedSound = true
            mediaPlayer.start()
        }
    }

    Column(modifier = modifier.padding(20.dp)) {

        // Show dialog if all locations are found
        if (showCongratsDialog) {
            AlertDialog(
                onDismissRequest = { showCongratsDialog = false },
                confirmButton = {
                    TextButton(onClick = { showCongratsDialog = false }) {
                        Text("OK")
                    }
                },
                title = { Text("Congratulations!") },
                text = { Text("You've found all locations and completed the treasure hunt!") }
            )
        }

        // Display count of found locations
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Found: $foundCount / ${locations.size}",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = {
                dbHelper.resetAllLocations()
                locations = dbHelper.getAllLocations()
                rewardMessage = getRewardMessage(dbHelper, locations)
                hasPlayedSound = false
                showCongratsDialog = false
            }) {
                Text("Reset Game")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Show reward message
        Text(
            text = rewardMessage,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // List of locations
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(locations) { location ->
                LocationCard(location = location) {
                    dbHelper.markAsFound(location.id)
                    locations = dbHelper.getAllLocations()
                }
            }
        }
    }
}

// Determine reward message based on found locations
fun getRewardMessage(dbHelper: DatabaseHelper, locations: List<LocationEntity>): String {
    val foundCount = dbHelper.countFoundLocations()
    return when {
        foundCount == locations.size && locations.isNotEmpty() ->
            "You've earned a 10% discount and an entry to the weekend getaway!"
        foundCount >= 5 ->
            "You've earned a 10% discount!"
        else ->
            "Find more locations to earn rewards!"
    }
}

// UI for each individual location
@Composable
fun LocationCard(location: LocationEntity, onFoundClick: (LocationEntity) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleLarge
            )
            AsyncImage(
                model = location.imageUrl,
                contentDescription = location.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Button(
                onClick = { onFoundClick(location) },
                enabled = !location.isFound,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (location.isFound) "Found!" else "Mark as Found")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationsScreenPreview() {
    CountryTreasureHuntTheme {
        LocationsScreen()
    }
}
