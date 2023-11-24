package com.example.sensorapp

import SensorViewModel
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import java.lang.Math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sensorViewModel: SensorViewModel = viewModel()
            SensorScreen(sensorViewModel) {
                val intent = Intent(this, GestureActivity::class.java)
                startActivity(intent)
            }
        }
    }
}


@Composable
fun SensorScreen(viewModel: SensorViewModel, navigateToGestureActivity: () -> Unit) {
    Column {
        val temperature = viewModel.temperatureLiveData.observeAsState()
        val pressure = viewModel.pressureLiveData.observeAsState()

        Text(text = "Temperature: ${temperature.value ?: "N/A"}")
        Text(text = "Pressure: ${pressure.value ?: "N/A"}")

        Button(
            onClick = { navigateToGestureActivity() }
            ,
            modifier = Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    val dx = dragAmount
                    if (abs(dx) > 0) { // Detects any horizontal drag
                        navigateToGestureActivity()
                    }
                }
            }
        ) {
            Text("Gesture Playground")
        }
        // Add more UI components as needed
    }
}
