package com.pms.a02_ud5_miubicacion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {
  // Referencia al servicio de posicionamiento
  LocationManager locationManager;
  // Para almacenar la latitud y longitud
  double lat;
  double lon;
  TextView latitud, longitud;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // referencias a las View
    latitud = (TextView) findViewById(R.id.tvLatitud2);
    longitud = (TextView) findViewById(R.id.tvLongitud2);


    //petición de permisos para APIs >=23 (versión 6.0, 6.0.1 Marshmallow)

    //si API de dispositivo >= API 23 -- pido permisos
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

      //si los permisos han sido concedidos
      if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        Log.i("Permisos", "Se tienen los permisos!");
        abreMiUbicacion(); // abre MiUbicacion para poder activar GPS
        myLocalizacion();

      } else {//la primera vez que se ejecute la App o las siguientes si no se concede el permiso
        //muestra el diálogo pidiendo permisos y  se llamará al método onRequestPermissionsResult
        ActivityCompat.requestPermissions(
            this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 122);
      }
    } else { //API < 23
      abreMiUbicacion(); // Abre Mi Ubicación para poder activar GPS *3*
      myLocalizacion();
    }

  }

  /**
   * procede según la respuesta del usuario a la solicitud del permiso en el diálogo
   * @param requestCode: Código de petición del permiso (el entero asignado a esa petición)
   * @param permissions: vector de permisos requeridos en la petición mediante requestPermissions
   * @param grantResults: vector de enteros asociado a la concesión o no de los permisos
   */
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    if (requestCode == 122) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        abreMiUbicacion(); // Abre Mi Ubicación para poder activar GPS
        myLocalizacion();
      } else {
        // Si el usuario no concede el Permiso o lo rechaza, muestra un mensaje.
        Toast.makeText(this, "No hay permisos de Localización", Toast.LENGTH_LONG)
            .show();
      }
    }
  }

  /**
   * Obtiene el Servicio de posicionamiento y registra la localización
   */
  private void myLocalizacion(){
    // obtenemos el servicio de posicionamiento
    this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    //Si el GPS (Mi Ubicación) no está activado en dispositivo
    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

      Toast.makeText(this, "Error al recuperar el GPS. Active el GPS", Toast.LENGTH_LONG)
          .show();
    }else {//Si el GPS (Mi Ubicación) está activado
      try {
        // registramos la recepción de datos del GPS
        this.locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 30000, 20, (LocationListener) this);
      } catch (SecurityException se) {
        se.printStackTrace();
      }
    }
  }

  /**
   * Al pulsar sobre el botón 'Donde estoy' muestra en el mapa la ubicación actual
   *
   * @param view
   */
  public void onClickDondeEstoy(View view) {
    // Creamos y componemos el Intent para abrir la aplicación de mapas
    Intent i = new Intent();
    i.setAction(Intent.ACTION_VIEW);
    Uri uri = Uri.parse("geo:0,0?q=" + this.lat + "," + this.lon);
    i.setData(uri);
    //lanza el Intent que abre la aplicación de mapas
    startActivity(i);
  }


  /**
   * Cuando cambia la localización muestra las nuevas coordenadas en la pantalla
   *
   * @param location
   */
  @Override
  public void onLocationChanged(Location location) {
    // guardamos los valores de latitud y longitud
    this.lat = location.getLatitude();
    this.lon = location.getLongitude();
    // mostramos la posición en pantalla
    this.latitud.setText("Latitud: " + String.valueOf(lat));
    this.longitud.setText("Longitud: " + String.valueOf(lon));

  }

  @Override
  public void onProviderDisabled(String provider) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onProviderEnabled(String provider) {
    // TODO Auto-generated method stub

  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    // TODO Auto-generated method stub

  }

  /*****************Abrir ventana de Ubicación para poder activar GPS ************************/
  /**
   * comprueba el estado del servicio de Ubicacion del dispositivo. Y si no está disponible,
   * muestra un cuadro de diálogo para activarlo
   *
   */
  private void abreMiUbicacion() {
    LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    //si no está activada MiUbicaion en el dispositivo
    if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      // muestra un diálogo de alerta para poder activarlo
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle("Ubicación desactivada");
      builder.setMessage("Por favor, actívela a continuación");
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogInterface, int i) {
          // muestra la configuración de MyUbicación del dispositivo
          Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
          startActivity(intent);
        }
      });
      Dialog alertDialog = builder.create();
      alertDialog.setCanceledOnTouchOutside(false);
      alertDialog.show();
    }
  }

}


