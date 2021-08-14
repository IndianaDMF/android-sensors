package com.example.sensors


import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {

    companion object {
        lateinit var mSensorManager: SensorManager
    }

    private lateinit var db: MainActivity.SensorDatabase
    private var mAccelerometer : Sensor ?= null
    private var lightSensor : Sensor ?= null
    private var gyro : Sensor ?= null
    private var resume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        lightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        gyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

//        db_init()
    }

    private fun db_init() {
         db = Room.databaseBuilder(
            applicationContext,
            SensorDatabase::class.java,
            "sensor.db").build()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && resume) {
            val metric = event.values[0].toString()

            val time = DateTimeFormatter
                .ofPattern("mm:ss")
                .withZone(ZoneOffset.UTC)
                .format(Instant.now())

            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    for(entry: Int in event.values.indices){
                        if(entry==0){
                            displayMetric(R.id.x_motion, event.values[0].toString())
                        }
                        if(entry==1){
                            displayMetric(R.id.y_motion, event.values[1].toString())
                        }
                        else{
                            displayMetric(R.id.z_motion, event.values[2].toString())
                        }
                    }
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

    override fun onResume() {
        super.onResume()
        registerListener(mAccelerometer)
        registerListener(lightSensor)
        registerListener(gyro)
    }

    private fun registerListener(sensor: Sensor?) {
        if(sensor != null){
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    fun checkSensors(view: View){
        val intent = Intent(this@MainActivity, SensorCheck::class.java)
        startActivity(intent)
    }

    fun resumeReading(view: View) {
        this.resume = true
    }

    fun pauseReading(view: View) {
        this.resume = false
    }

    @Database(entities = [Entry::class], version = 1)
    abstract class SensorDatabase : RoomDatabase(){
        abstract fun entryDao(): EntryDao
    }

    @Dao
    interface EntryDao{
        @Query("SELECT * FROM Entry")
        fun getAll(): List<Entry>

        @Query("SELECT * FROM Entry WHERE session in (:session)")
        fun getAllBySession(session: UUID): List<Entry>

        @Insert
        fun insertEntry(vararg entry: Entry)
    }

    @Entity
    data class Entry(
        @PrimaryKey val id: UUID,
        @ColumnInfo(name="session") val session: UUID,
        @ColumnInfo(name="time_stamp") val timestamp: Timestamp,
        @ColumnInfo(name="x") val x_motion: Float,
        @ColumnInfo(name="y") val y_motion: Float,
        @ColumnInfo(name="z") val z_motion: Float
    )
}