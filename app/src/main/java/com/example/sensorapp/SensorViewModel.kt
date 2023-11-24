import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SensorViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private var sensorManager: SensorManager = application.getSystemService(SensorManager::class.java)
    val temperatureLiveData = MutableLiveData<Float>()
    val pressureLiveData = MutableLiveData<Float>()

    init {
        // Initialize and register the temperature sensor
        val temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Initialize and register the pressure sensor
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                    temperatureLiveData.postValue(it.values[0])
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

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}
