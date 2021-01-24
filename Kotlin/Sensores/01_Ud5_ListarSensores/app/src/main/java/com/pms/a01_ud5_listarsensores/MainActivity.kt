package com.pms.a01_ud5_listarsensores

import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private var etiqueta: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etiqueta = findViewById<View>(R.id.listado) as TextView
        /* Usar getSystemService() con la constante SENSOR_SERVICE y obtener
        el objeto sensorManager  que accederá a la lista de servicios del sistema.*/
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // obtener la lista de todos los sensores   (TYPE_ALL)
        val listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL)
        // recorrer todos los sensores
        for (sensor in listaSensores)  // añadir el sensor al label o etiqueta
            etiqueta!!.append("""
    
    -${sensor.name}, fabricante: ${sensor.vendor}
    """.trimIndent())
    }
}