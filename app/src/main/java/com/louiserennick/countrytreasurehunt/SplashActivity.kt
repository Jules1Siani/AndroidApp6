package com.louiserennick.countrytreasurehunt

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.louiserennick.countrytreasurehunt.ui.theme.CountryTreasureHuntTheme

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up the splash screen using Jetpack Compose and apply the app theme
        setContent {
            CountryTreasureHuntTheme {
                SplashScreen {
                    // Navigate to MainActivity when the button is clicked
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // Close splash screen so user can't return
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onStartClick: () -> Unit) {
    // Background container for splash content
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Vertical layout centered on screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // App title
            Text(
                text = "City Treasure Hunt",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App introduction
            Text(
                text = "Explore the city. Find all 10 locations. Win a prize!",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Start game button
            Button(
                onClick = onStartClick,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Start Hunt", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
