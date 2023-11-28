package com.newapp.sensorapp

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
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

/**
 * SensorBallActivity is an interactive activity that uses device sensors, specifically the
 * accelerometer, to control the movement of a ball on the screen. This activity demonstrates
 * the integration of sensor data with UI components in a real-time and responsive manner.
 * It utilizes Jetpack Compose for its UI, showcasing a modern approach to Android UI development.
 *
 * Key Components and Functionalities:
 * - SensorManager and Accelerometer: Access and listen to the accelerometer sensor to get
 *   real-time motion data of the device.
 * - SensorEventListener: Implements SensorEventListener to respond to sensor data changes.
 * - BallArea Composable: A composable function that renders a ball on the canvas, whose position
 *   is controlled by the accelerometer data.
 * - updateBallPosition: A function to calculate and update the ball's position based on sensor
 *   data, ensuring the ball remains within the screen bounds.
 * - Screen Dimension Handling: Fetches screen dimensions to keep the ball's movement within
 *   the visible area.
 * - Lifecycle Handling: Registers and unregisters the sensor listener based on the Activity's lifecycle.
 *
 * This activity demonstrates the use of hardware sensors in conjunction with a dynamic and
 * reactive UI, providing an interactive experience. It's a perfect example of how sensor data
 * can be creatively used in Android applications.
 *
 * @see ComponentActivity - Base class for activities that use Android Jetpack's modern lifecycle.
 * @see SensorEventListener - Interface for receiving notifications from the SensorManager.
 * @see SensorManager - System service to access and manage sensors.
 * @see Canvas - Jetpack Compose UI element used for drawing the ball.
 * @see @Composable - Annotation for composable functions, defining UI elements in Jetpack Compose.
 *
 * @author Your Name
 */

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

