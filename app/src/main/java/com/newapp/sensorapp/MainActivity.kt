package com.newapp.sensorapp


import SensorViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Math.abs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.unit.sp

/**
 * MainActivity is the entry point of the SensorApp, designed to interact with sensors and
 * location services. It integrates with the FusedLocationProviderClient for location services
 * and manages permission requests for location access. The main functionality includes fetching
 * and updating location data, and navigation to other activities like GestureActivity and
 * SensorBallActivity. This activity uses Jetpack Compose for its UI, showcasing a modern
 * declarative UI approach.
 *
 * Key Components and Functionalities:
 * - FusedLocationProviderClient: Used for accessing the device's last known location.
 * - SensorViewModel: A ViewModel for managing sensor data and location updates.
 * - Permission Handling: Requests and checks location permissions to ensure proper functionality.
 * - SensorScreen: A composable function that creates the UI with buttons for navigating
 *   to GestureActivity and SensorBallActivity.
 * - Dynamic UI Update: LiveData is observed to update the UI with sensor and location data.
 *
 * Permission Handling:
 * The app requests ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions to fetch the
 * device's location. The permission request results are handled in onRequestPermissionsResult.
 *
 * Navigation:
 * The app provides navigation to GestureActivity and SensorBallActivity through buttons
 * in the SensorScreen composable function.
 *
 * @see ComponentActivity - Base class for activities that use Android Jetpack's modern lifecycle.
 * @see @Composable - Annotation for composable functions, defining the app's UI components.
 * @see FusedLocationProviderClient - For accessing location services.
 * @see ViewModelProvider - For instantiating and obtaining the SensorViewModel instance.
 * @see LiveData - Used to observe data changes and update the UI accordingly.
 *
 * @author Your Name
 */


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorViewModel: SensorViewModel
    private val locationRequestCode = 100
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorViewModel =
            ViewModelProvider(this).get(SensorViewModel::class.java) // Corrected ViewModel instantiation


        checkAndRequestLocationPermissions {
            fetchLocation()
        }

        setContent {
            SensorScreen(
                sensorViewModel,
                navigateToGestureActivity = {
                    val intent = Intent(this@MainActivity, GestureActivity::class.java)
                    startActivity(intent)
                },
                navigateToSensorBallActivity = {
                    val ballIntent = Intent(this@MainActivity, SensorBallActivity::class.java)
                    startActivity(ballIntent)
                }
            )
        }



        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SensorScreen(
                        sensorViewModel,
                        navigateToGestureActivity = {
                            val intent = Intent(this@MainActivity, GestureActivity::class.java)
                            startActivity(intent)
                        },
                        navigateToSensorBallActivity = {
                            val ballIntent =
                                Intent(this@MainActivity, SensorBallActivity::class.java)
                            startActivity(ballIntent)
                        }
                    )
                }
            }
        }
    }


    private fun checkAndRequestLocationPermissions(onPermissionGranted: () -> Unit) {
        if (locationPermissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }) {
            onPermissionGranted()
        } else {
            ActivityCompat.requestPermissions(this, locationPermissions, locationRequestCode)
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                sensorViewModel.updateLocation(location)
            } else {
                // Update LiveData when location is null
                sensorViewModel.locationLiveData.postValue("Location not available")
            }
        }.addOnFailureListener {
            // Handle the failure case
            sensorViewModel.locationLiveData.postValue("Error fetching location")
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationRequestCode && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            fetchLocation()
        }
    }
}

@Composable
fun SensorScreen(viewModel: SensorViewModel, navigateToGestureActivity: () -> Unit, navigateToSensorBallActivity: () -> Unit) {
    Column {
        // Title centered
        Text(
            text = "Sensors Playground",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))

        // Left-aligned content with margin
        Column(modifier = Modifier.padding(start = 16.dp)) {
            val temperature = viewModel.temperatureLiveData.observeAsState()
            val pressure = viewModel.pressureLiveData.observeAsState()
            val city = viewModel.cityLiveData.observeAsState("Fetching city...")
            val state = viewModel.stateLiveData.observeAsState("Fetching state...")

            Text(text = "Location", fontWeight = FontWeight.Bold)
            Text(text = "City: ${city.value}", fontSize = 16.sp)
            Text(text = "State: ${state.value}", fontSize = 16.sp)

            Spacer(Modifier.height(8.dp))

            Text(text = "Temperature: ${temperature.value ?: "N/A"}", fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text(text = "Air Pressure: ${pressure.value ?: "N/A"}", fontSize = 16.sp)
        }

        Spacer(Modifier.height(16.dp))

        // Centered button
        Button(
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 32.dp)
                .align(Alignment.CenterHorizontally)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        val dx = dragAmount
                        if (abs(dx) > 0) {
                            navigateToGestureActivity()
                        }
                    }
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,  // Light gray background
                contentColor = Color(0xFF7B1FA2)  // Purple text
            ),
            shape = RoundedCornerShape(0.dp)  // Square shape
        ) {
            Text("Gesture Playground", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {  },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 32.dp)
                .align(Alignment.CenterHorizontally)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        val dx = dragAmount
                        if (abs(dx) > 0) {
                            navigateToSensorBallActivity()
                        }
                    }
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,  // Light gray background
                contentColor = Color(0xFF7B1FA2)  // Purple text
            ),
            shape = RoundedCornerShape(0.dp)  // Square shape
        ) {
            Text("Sensor Playground", fontWeight = FontWeight.Bold)
        }

    }
}
