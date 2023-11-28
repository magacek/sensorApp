import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.util.*

/**
 * SensorBallActivity is an Android activity that demonstrates the use of the device's accelerometer
 * sensor to control the movement of a graphical ball on the screen. This activity provides an
 * interactive way to understand how sensor data can be integrated with graphical elements in an app.
 *
 * Key Features:
 * - Sensor Management: Utilizes the SensorManager to access the accelerometer sensor, which
 *   provides data on device movement.
 * - Sensor Event Handling: Implements the SensorEventListener interface to handle changes in
 *   accelerometer data.
 * - Dynamic Ball Movement: Translates accelerometer data into movement of a ball on the screen,
 *   demonstrating real-time interaction with sensor data.
 * - Screen Boundaries: Calculates screen dimensions to ensure the ball's movement is confined
 *   within the screen's bounds.
 * - Composable UI: Uses Jetpack Compose for modern, declarative UI development, making UI code
 *   more concise and readable.
 *
 * Lifecycle Management:
 * - The sensor listener is registered in the LaunchedEffect block of the SensorBallActivityContent
 *   composable function, ensuring that the listener is only registered once during the composable's
 *   lifecycle.
 * - The sensor listener is unregistered in the DisposableEffect block, ensuring proper resource
 *   management and avoiding potential memory leaks.
 *
 * @see ComponentActivity for activity lifecycle methods.
 * @see SensorManager for accessing the device sensors.
 * @see SensorEventListener for receiving notifications from the SensorManager.
 * @see Canvas for drawing the ball on the screen.
 * @see LaunchedEffect and DisposableEffect for managing effects in Jetpack Compose.
 */

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private var sensorManager: SensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val temperatureLiveData = MutableLiveData<String>()
    val pressureLiveData = MutableLiveData<Float>()
    val locationLiveData = MutableLiveData<String>()
    val cityLiveData = MutableLiveData<String>()
    val stateLiveData = MutableLiveData<String>()

    init {
        // Temperature sensor
        val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            temperatureLiveData.postValue("Temperature sensor not available")
        }

        // Pressure sensor
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    temperatureLiveData.postValue("${it.values[0]}°C")
                }
                Sensor.TYPE_PRESSURE -> {
                    pressureLiveData.postValue(it.values[0])
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Implement if needed
    }

    fun updateLocation(location: Location) {
        val geocoder = Geocoder(getApplication(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    cityLiveData.postValue(address.locality ?: "Unknown City")
                    stateLiveData.postValue(address.adminArea ?: "Unknown State")
                } else {
                    cityLiveData.postValue("City not found")
                    stateLiveData.postValue("State not found")
                }
            }
        } catch (e: IOException) {
            cityLiveData.postValue("Error finding city")
            stateLiveData.postValue("Error finding state")
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
