package com.pms.a02_ud5_acelerometro;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
  private long ultima_actualizacion = 0, ultimo_movimiento = 0;
  private float X = 0, Y = 0, Z = 0;
  private float nuevoX = 0, nuevoY = 0, nuevoZ = 0;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // establecer la orientación de la pantalla del móvil fija, siempre en horizontal
    this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  @Override
  // la aplicación puede interaccionar con el usuario
  protected void onResume() {
    super.onResume();
    // Creamos el objeto para acceder al servicio de sensores
    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);

    // obtener la lista de sensores de tipo acelerómetro
    List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

    // comprobar si hay acelerómetro
    if (sensors.size() > 0) {
      // hay acelerómetro => activar la escucha del sensor del acelerómetro con precisión para juegos
      sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
    }
  }

  @Override
  // La aplicación va a ser parada => dejar de escuchar el sensor para que no consuma recursos
  protected void onStop() {
    SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
    sm.unregisterListener(this);
    super.onStop();
  }

  @Override
    /* Como estamos implementando el interfaz  SensorEventListener
    es obligatorio declarar el método onAccuracyChanged () aunque no se use
    Se llama cuando la precisión de un sensor ha cambiado.
    Parámetros
        sensor 	El ID del sensor se supervisa
        precisión 	La nueva precisión de este sensor. */
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @Override
  // se activa cuando hay cambios en los valores que mide el sensor
  public void onSensorChanged(SensorEvent event) {
    synchronized (this) {
      // guardar el tiempo actual
      long tiempo_actual = event.timestamp;

      // recoger los valores de X, Y, Z actuales
      nuevoX = event.values[0];
      nuevoY = event.values[1];
      nuevoZ = event.values[2];

            /* comprobar si este código ya se ha ejecutado alguna vez, es decir,
            si las variables que guardan los valores anteriores de X, Y y Z
            tiene valores diferentes de cero. Esto debería ejecutarse sólo la primera vez.*/
      if (X == 0 && Y == 0 && Z == 0) {
        ultima_actualizacion = tiempo_actual;
        ultimo_movimiento = tiempo_actual;
        X = nuevoX;
        Y = nuevoY;
        Z = nuevoZ;
      }

      long diferencia_tiempo = tiempo_actual - ultima_actualizacion;

      // comprobar si hay movimiento en caso de haber pasado tiempo
      if (diferencia_tiempo > 0) {
                /* a la posición actual (con las 3 coordenadas) le restamos a la posición anterior,
                puede ser que el movimiento sea en distintas direcciones por eso se calcula el valor absoluto. */
        float movimiento = Math.abs((nuevoX + nuevoY + nuevoZ) - (X - Y - Z)) / diferencia_tiempo;

        int limite = 1500;

                /* Para decidir en que momento mostramos una tostado indicando el movimiento
                vamos a usar como valor de frontera de movimiento mínimo 1×10^-6;
                este valor mientras mayor sea, se necesitará más movimiento y mientras menor sea más sensible
                será el aviso de que hay movimiento. */

        float min_movimiento = 1E-6f;
        if (movimiento > min_movimiento) {
          if (tiempo_actual - ultimo_movimiento >= limite) {
            Toast.makeText(getApplicationContext(), "Hay movimiento de " + movimiento, Toast.LENGTH_SHORT).show();
          }
          ultimo_movimiento = tiempo_actual;
        }

        // guardar los valores de X, Y, Z y el tiempo
        X = nuevoX;
        Y = nuevoY;
        Z = nuevoZ;
        ultima_actualizacion = tiempo_actual;
      }

      // poner en pantalla los nuevos valores X, Y, Z del acelerómetro
      ((TextView) findViewById(R.id.txtAccX)).setText("Acelerómetro X: " + nuevoX);
      ((TextView) findViewById(R.id.txtAccY)).setText("Acelerómetro Y: " + nuevoY);
      ((TextView) findViewById(R.id.txtAccZ)).setText("Acelerómetro Z: " + nuevoZ);
    }

  }

}

