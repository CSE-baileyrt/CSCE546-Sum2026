package com.example.familytasks.ui.screens

import android.os.SystemClock
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.familytasks.viewmodel.FamilyViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: FamilyViewModel,
    onEditUsers: () -> Unit
) {
    var tapCount by rememberSaveable { mutableIntStateOf(0) }
    var lastTapTime by remember { mutableLongStateOf(0L) }

    LaunchedEffect(tapCount) {
        if (tapCount > 0) {
            // Wait to see if the user taps again
            delay(800L)
            if (tapCount in 3..5) {
                viewModel.authenticateWithTapCode(tapCount)
            }
            tapCount = 0
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Family Tasks",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap 3 times for Son, 4 times for Wife, 5 times for Dad.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        val now = SystemClock.elapsedRealtime()
                        // If it's been too long since the last tap, start over at 1
                        tapCount = if (now - lastTapTime > 800L) 1 else tapCount + 1
                        lastTapTime = now
                    })
                },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Tap Here", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current count: $tapCount", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        ElevatedButton(onClick = onEditUsers) {
            Icon(imageVector = Icons.Filled.Edit, contentDescription = null)
            Spacer(modifier = Modifier.height(0.dp))
            Text(text = "Edit profiles")
        }
    }
}
