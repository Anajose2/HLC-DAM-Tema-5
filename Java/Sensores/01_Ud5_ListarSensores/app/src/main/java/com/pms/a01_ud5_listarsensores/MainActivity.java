package com.pms.a01_ud5_listarsensores;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

  private TextView etiqueta;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    etiqueta = (TextView) findViewById(R.id.listado);
        /* Usar getSystemService() con la constante SENSOR_SERVICE y obtener
        el objeto sensorManager  que accederá a la lista de servicios del sistema.*/
    SensorManager sensorManager = (SensorManager)
        getSystemService(SENSOR_SERVICE);
    // obtener la lista de todos los sensores   (TYPE_ALL)
    List<Sensor> listaSensores = sensorManager.getSensorList(Sensor.TYPE_ALL);
    // recorrer todos los sensores
    for(Sensor sensor: listaSensores)
      // añadir el sensor al label o etiqueta
      etiqueta.append("\n-" + sensor.getName() + ", fabricante: " +sensor.getVendor());

  }
}
