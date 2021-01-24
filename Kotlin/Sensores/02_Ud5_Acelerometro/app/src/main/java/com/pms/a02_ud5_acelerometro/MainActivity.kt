package com.pms.a02_ud5_acelerometro

import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var ultima_actualizacion: Long = 0
    private var ultimo_movimiento: Long = 0
    private var X = 0f
    private var Y = 0f
    private var Z = 0f
    private var nuevoX = 0f
    private var nuevoY = 0f
    private var nuevoZ = 0f
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // establecer la orientación de la pantalla del móvil fija, siempre en horizontal
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // la aplicación puede interaccionar con el usuario
    override fun onResume() {
        super.onResume()
        // Creamos el objeto para acceder al servicio de sensores
        val sm = getSystemService(SENSOR_SERVICE) as SensorManager

        // obtener la lista de sensores de tipo acelerómetro
        val sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER)

        // comprobar si hay acelerómetro
        if (sensors.size > 0) {
            // hay acelerómetro => activar la escucha del sensor del acelerómetro con precisión para juegos
            sm.registerListener(this, sensors[0], SensorManager.SENSOR_DELAY_GAME)
        }
    }

    // La aplicación va a ser parada => dejar de escuchar el sensor para que no consuma recursos
    override fun onStop() {
        val sm = getSystemService(SENSOR_SERVICE) as SensorManager
        sm.unregisterListener(this)
        super.onStop()
    }

    /* Como estamos implementando el interfaz  SensorEventListener
    es obligatorio declarar el método onAccuracyChanged () aunque no se use
    Se llama cuando la precisión de un sensor ha cambiado.
    Parámetros
        sensor 	El ID del sensor se supervisa
        precisión 	La nueva precisión de este sensor. */  override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    // se activa cuando hay cambios en los valores que mide el sensor
    override fun onSensorChanged(event: SensorEvent) {
        synchronized(this) {

            // guardar el tiempo actual
            val tiempo_actual = event.timestamp

            // recoger los valores de X, Y, Z actuales
            nuevoX = event.values[0]
            nuevoY = event.values[1]
            nuevoZ = event.values[2]

            /* comprobar si este código ya se ha ejecutado alguna vez, es decir,
            si las variables que guardan los valores anteriores de X, Y y Z
            tiene valores diferentes de cero. Esto debería ejecutarse sólo la primera vez.*/if (X == 0f && Y == 0f && Z == 0f) {
            ultima_actualizacion = tiempo_actual
            ultimo_movimiento = tiempo_actual
            X = nuevoX
            Y = nuevoY
            Z = nuevoZ
        }
            val diferencia_tiempo = tiempo_actual - ultima_actualizacion

            // comprobar si hay movimiento en caso de haber pasado tiempo
            if (diferencia_tiempo > 0) {
                /* a la posición actual (con las 3 coordenadas) le restamos a la posición anterior,
                puede ser que el movimiento sea en distintas direcciones por eso se calcula el valor absoluto. */
                val movimiento = Math.abs(nuevoX + nuevoY + nuevoZ - (X - Y - Z)) / diferencia_tiempo
                val limite = 1500

                /* Para decidir en que momento mostramos una tostado indicando el movimiento
                vamos a usar como valor de frontera de movimiento mínimo 1×10^-6;
                este valor mientras mayor sea, se necesitará más movimiento y mientras menor sea más sensible
                será el aviso de que hay movimiento. */
                val min_movimiento = 1E-6f
                if (movimiento > min_movimiento) {
                    if (tiempo_actual - ultimo_movimiento >= limite) {
                        Toast.makeText(applicationContext, "Hay movimiento de $movimiento", Toast.LENGTH_SHORT).show()
                    }
                    ultimo_movimiento = tiempo_actual
                }

                // guardar los valores de X, Y, Z y el tiempo
                X = nuevoX
                Y = nuevoY
                Z = nuevoZ
                ultima_actualizacion = tiempo_actual
            }

            // poner en pantalla los nuevos valores X, Y, Z del acelerómetro
            (findViewById<View>(R.id.txtAccX) as TextView).text = "Acelerómetro X: $nuevoX"
            (findViewById<View>(R.id.txtAccY) as TextView).text = "Acelerómetro Y: $nuevoY"
            (findViewById<View>(R.id.txtAccZ) as TextView).text = "Acelerómetro Z: $nuevoZ"
        }
    }
}