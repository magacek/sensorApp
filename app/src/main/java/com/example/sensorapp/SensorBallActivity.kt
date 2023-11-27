package com.example.sensorapp

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import kotlin.math.max
import kotlin.math.min

class SensorBallActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val ballSpeed = 5f
    private val ballPosition = mutableStateOf(Offset(200f, 200f)) // Ball position state
    private var screenWidth = 0f
    private var screenHeight = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Get screen dimensions
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels.toFloat()
        screenHeight = displayMetrics.heightPixels.toFloat()

        setContent {
            SensorBallActivityContent()
        }
    }

    @Composable
    fun SensorBallActivityContent() {
        BallArea(ballPosition)

        LaunchedEffect(Unit) {
            accelerometer?.also { sensor ->
                sensorManager.registerListener(this@SensorBallActivity, sensor, SensorManager.SENSOR_DELAY_GAME)
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                sensorManager.unregisterListener(this@SensorBallActivity)
            }
        }
    }

    @Composable
    fun BallArea(ballPosition: State<Offset>) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            drawCircle(
                color = Color.Blue,
                center = ballPosition.value,
                radius = 50f // Size of the ball
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]

        // Update the ball position based on accelerometer data
        updateBallPosition(x, y)
    }

    private fun updateBallPosition(x: Float, y: Float) {
        val currentBallPosition = ballPosition.value
        val newX = currentBallPosition.x - x * ballSpeed
        val newY = currentBallPosition.y + y * ballSpeed

        // Ensure the ball stays within the screen bounds
        ballPosition.value = Offset(
            x = max(min(newX, screenWidth - 50f), 50f),
            y = max(min(newY, screenHeight - 50f), 50f)
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not used in this context
    }
}

