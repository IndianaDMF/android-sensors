package com.example.sensors


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager : SensorManager
    private var mAccelerometer : Sensor ?= null
    private var lightSensor : Sensor ?= null
    private var gyro : Sensor ?= null
    private var resume = false;

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && resume) {
            val metric = event.values[0].toString()
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    displayMetric(R.id.accelerometer, metric)
                }
                Sensor.TYPE_LIGHT -> {
                    displayMetric(R.id.lightSensor, metric)
                }
                Sensor.TYPE_GYROSCOPE -> {
                    displayMetric(R.id.gyroSensor, metric)
                }
            }
        }
    }

    private  fun displayMetric(id: Int, score: String) {
        findViewById<TextView>(id).text = score
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    }

    override fun onResume() {
        super.onResume()
        registerListener(this, mAccelerometer)
        registerListener(this, lightSensor)
        registerListener(this, gyro)
    }

    private fun registerListener(mainActivity: MainActivity, sensor: Sensor?) {
        if(sensor != null){
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    fun resumeReading(view: View) {
        this.resume = true
    }

    fun pauseReading(view: View) {
        this.resume = false
    }

    fun debug(view: View) {
        printCapabilities()
    }

    private fun printCapabilities() {
        val sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        val sensorText = StringBuilder()
        for (sensor in sensors) {
            println(sensor.name)
        }
    }
}