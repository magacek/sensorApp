package com.newapp.sensorapp

import android.content.res.Configuration
import android.os.Bundle

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.lang.Math.abs

/**
 * GestureActivity is a specialized ComponentActivity for handling user gestures.
 * It primarily focuses on demonstrating different gesture detections in an interactive
 * and responsive manner, using various Jetpack Compose components and gesture detection
 * functionalities. The activity includes a dynamically changing UI that adapts to screen orientation,
 * displaying a ball that responds to drag and tap gestures, and a log area to track these gestures.
 *
 * Key Components and Features:
 * - GestureActivityContent: A composable function that sets up the UI, manages the state for
 *   the ball's position and gesture logs, and adapts the layout based on screen orientation.
 * - BallMovementArea: A composable that provides a canvas for the ball, handling drag gestures
 *   (swipe directions) and updating the ball's position accordingly.
 * - GestureLogArea: A composable that displays a list of detected gestures using LazyColumn,
 *   showing the interaction history.
 * - Gesture detection: Utilizes detectDragGestures and detectTapGestures to track user interactions.
 * - Orientation handling: Adjusts the layout (Row for landscape, Column for portrait) dynamically
 *   based on the current screen orientation, ensuring a responsive and adaptable UI.
 *
 * @see ComponentActivity - Base class for activities that use Jetpack Compose for their UI.
 * @see @Composable - Annotation for composable functions, used to define UI components.
 * @see Canvas - Used for drawing the ball and handling gestures within its area.
 * @see LazyColumn - Efficiently displays a scrollable list, used for showing gesture logs.
 * @see detectDragGestures, detectTapGestures - Functions for detecting various user gestures.
 * @see MutableState, mutableStateOf - Used for managing and observing state changes in composables.
 *
 * @author Your Name
 */

class GestureActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestureActivityContent()
        }
    }
}

@Composable
fun GestureActivityContent() {
    // Shared state for ball position and gesture log
    val ballPosition = remember { mutableStateOf(Offset(200f, 200f)) }
    val gestureLog = remember { mutableStateListOf<String>() }

    // Obtain the current configuration to determine screen orientation
    val configuration = LocalConfiguration.current
    // Determine if the current orientation is landscape
    val context = LocalContext.current
    val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Conditional layout based on the screen orientation
    if (isLandscape) {
        // Layout for landscape orientation
        // Arranging components in a horizontal row
        Row(modifier = Modifier.fillMaxSize()) {
            // Left Fragment - Ball Movement Area (On the left side in landscape mode)
            BallMovementArea(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                ballPosition = ballPosition,
                gestureLog = gestureLog
            )

            // Right Fragment - Gesture Log Area (On the right side in landscape mode)
            GestureLogArea(
                modifier = Modifier.weight(1f).fillMaxHeight().background(Color.LightGray),
                gestureLog = gestureLog
            )
        }
    } else {
        // Layout for portrait orientation
        // Arranging components in a vertical column
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Fragment - Ball Movement Area (On the top in portrait mode)
            BallMovementArea(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                ballPosition = ballPosition,
                gestureLog = gestureLog
            )

            // Bottom Fragment - Gesture Log Area (On the bottom in portrait mode)
            GestureLogArea(
                modifier = Modifier.weight(1f).fillMaxWidth().background(Color.LightGray),
                gestureLog = gestureLog
            )
        }
    }
}

@Composable
fun BallMovementArea(modifier: Modifier, ballPosition: MutableState<Offset>, gestureLog: MutableList<String>) {
    var startDragPosition by remember { mutableStateOf(Offset.Zero) }

    Canvas(modifier = modifier
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { startDragPosition = it },
                onDragEnd = {
                    val endDragPosition = ballPosition.value
                    val xDiff = endDragPosition.x - startDragPosition.x
                    val yDiff = endDragPosition.y - startDragPosition.y
                    when {
                        abs(xDiff) > abs(yDiff) && xDiff > 0 -> gestureLog.add("You Swiped Right")
                        abs(xDiff) > abs(yDiff) && xDiff < 0 -> gestureLog.add("You Swiped Left")
                        abs(yDiff) > abs(xDiff) && yDiff > 0 -> gestureLog.add("You Swiped Down")
                        abs(yDiff) > abs(xDiff) && yDiff < 0 -> gestureLog.add("You Swiped Up")
                    }
                },
                onDrag = { _, dragAmount ->
                    val newX = ballPosition.value.x + dragAmount.x
                    val newY = ballPosition.value.y + dragAmount.y
                    ballPosition.value = Offset(newX, newY)
                }
            )
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = { offset ->
                    ballPosition.value = offset
                    gestureLog.add("You Double Tapped")
                },
                onTap = { offset ->
                    ballPosition.value = offset
                    gestureLog.add("You Tapped")
                }
            )
        }
    ) {
        drawCircle(
            color = Color.Red,
            center = ballPosition.value,
            radius = 50f // Size of the ball
        )
    }
}


@Composable
fun GestureLogArea(modifier: Modifier, gestureLog: List<String>) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(gestureLog) { gesture ->
            Text(gesture)
        }
    }
}

