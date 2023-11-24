package com.example.sensorapp

import SensorViewModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Math.abs

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
        sensorViewModel = ViewModelProvider(this).get(SensorViewModel::class.java) // Corrected ViewModel instantiation

        checkAndRequestLocationPermissions {
            fetchLocation()
        }

        setContent {
            SensorScreen(sensorViewModel) {
                // Note the change here from 'this' to 'this@MainActivity'
                val intent = Intent(this@MainActivity, GestureActivity::class.java)
                startActivity(intent)
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
fun SensorScreen(viewModel: SensorViewModel, navigateToGestureActivity: () -> Unit) {
    Column {
        val temperature = viewModel.temperatureLiveData.observeAsState()
        val pressure = viewModel.pressureLiveData.observeAsState()
        val city = viewModel.cityLiveData.observeAsState("Fetching city...")
        val state = viewModel.stateLiveData.observeAsState("Fetching state...")
        Text(text = "")
        Text(text = "Location")
        Text(text = "City: ${city.value}")
        Text(text = "State: ${state.value}")
        Text(text = "")
        Text(text = "Temperature: ${temperature.value ?: "N/A"}")
        Text(text = "")
        Text(text = "Air Pressure: ${pressure.value ?: "N/A"}")


        Button(
            onClick = { navigateToGestureActivity() },
            modifier = Modifier.pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    val dx = dragAmount
                    if (abs(dx) > 0) {
                        navigateToGestureActivity()
                    }
                }
            }
        ) {
            Text("Gesture Playground")
        }
    }
}
