package com.example.sensors

import android.content.Intent
import android.hardware.Sensor
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sensors.MainActivity.Companion.mSensorManager


class SensorCheck : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor_check)
    }

    fun goback(view:View){
        val intent = Intent(this@SensorCheck, MainActivity::class.java)
        startActivity(intent)
    }

    fun debug(view: View) {
        printCapabilities()
    }

    private fun printCapabilities() {
        val croll_view = findViewById<TextView>(R.id.info)
        val content = StringBuilder()
        val sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        for (sensor in sensors) {
            content.append("&#8226;Sensor Name: ${sensor.name} <br/>")
            println(sensor.name)
        }
        croll_view.setText(Html.fromHtml(content.toString()))
//        ((TextView)findViewById(R.id.my_text_view)).setText(
    }
}