package com.example.sensorapp

import android.os.Bundle


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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInteropFilter
import android.view.MotionEvent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.ExperimentalComposeUiApi
import java.lang.Math.abs


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
    val ballPosition = remember { mutableStateOf(Offset(200f, 200f)) }
    val gestureLog = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top Fragment - Ball Movement Area
        BallMovementArea(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            ballPosition = ballPosition,
            gestureLog = gestureLog
        )

        // Bottom Fragment - Gesture Log Area
        GestureLogArea(
            modifier = Modifier.weight(1f).fillMaxWidth().background(Color.LightGray),
            gestureLog = gestureLog
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BallMovementArea(modifier: Modifier, ballPosition: MutableState<Offset>, gestureLog: MutableList<String>) {
    var startDragPosition by remember { mutableStateOf(Offset.Zero) }
    var isDragged by remember { mutableStateOf(false) }

    Canvas(modifier = modifier
        .pointerInteropFilter { motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    startDragPosition = Offset(motionEvent.x, motionEvent.y)
                    isDragged = false
                    true // Consume the event
                }
                MotionEvent.ACTION_MOVE -> {
                    ballPosition.value = Offset(motionEvent.x, motionEvent.y)
                    isDragged = true
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (isDragged) {
                        // Log swipe direction on finger lift
                        val xDiff = motionEvent.x - startDragPosition.x
                        val yDiff = motionEvent.y - startDragPosition.y
                        when {
                            abs(xDiff) > abs(yDiff) && xDiff > 0 -> gestureLog.add("Swiped Right")
                            abs(xDiff) > abs(yDiff) && xDiff < 0 -> gestureLog.add("Swiped Left")
                            abs(yDiff) > abs(xDiff) && yDiff > 0 -> gestureLog.add("Swiped Down")
                            abs(yDiff) > abs(xDiff) && yDiff < 0 -> gestureLog.add("Swiped Up")
                        }
                    }
                    true
                }
                else -> false
            }
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    gestureLog.add("Double Tapped")
                },
                onTap = {
                    if (!isDragged) {
                        gestureLog.add("Tapped")
                    }
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

