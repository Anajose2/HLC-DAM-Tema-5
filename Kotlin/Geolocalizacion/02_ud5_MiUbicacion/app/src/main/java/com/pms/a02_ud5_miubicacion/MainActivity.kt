package com.pms.a02_ud5_miubicacion

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity(), LocationListener {
    // Referencia al servicio de posicionamiento
    var locationManager: LocationManager? = null

    // Para almacenar la latitud y longitud
    var lat = 0.0
    var lon = 0.0
    var latitud: TextView? = null
    var longitud: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // referencias a las View
        latitud = findViewById<View>(R.id.tvLatitud2) as TextView
        longitud = findViewById<View>(R.id.tvLongitud2) as TextView


        //petición de permisos para APIs >=23 (versión 6.0, 6.0.1 Marshmallow)

        //si API de dispositivo >= API 23 -- pido permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            //si los permisos han sido concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permisos", "Se tienen los permisos!")
                abreMiUbicacion() // abre MiUbicacion para poder activar GPS
                myLocalizacion()
            } else { //la primera vez que se ejecute la App o las siguientes si no se concede el permiso
                //muestra el diálogo pidiendo permisos y  se llamará al método onRequestPermissionsResult
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 122)
            }
        } else { //API < 23
            abreMiUbicacion() // Abre Mi Ubicación para poder activar GPS *3*
            myLocalizacion()
        }
    }

    /**
     * procede según la respuesta del usuario a la solicitud del permiso en el diálogo
     * @param requestCode: Código de petición del permiso (el entero asignado a esa petición)
     * @param permissions: vector de permisos requeridos en la petición mediante requestPermissions
     * @param grantResults: vector de enteros asociado a la concesión o no de los permisos
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 122) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abreMiUbicacion() // Abre Mi Ubicación para poder activar GPS
                myLocalizacion()
            } else {
                // Si el usuario no concede el Permiso o lo rechaza, muestra un mensaje.
                Toast.makeText(this, "No hay permisos de Localización", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    /**
     * Obtiene el Servicio de posicionamiento y registra la localización
     */
    private fun myLocalizacion() {
        // obtenemos el servicio de posicionamiento
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        //Si el GPS (Mi Ubicación) no está activado en dispositivo
        if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Error al recuperar el GPS. Active el GPS", Toast.LENGTH_LONG)
                    .show()
        } else { //Si el GPS (Mi Ubicación) está activado
            try {
                // registramos la recepción de datos del GPS
                locationManager!!.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 30000, 20f, this as LocationListener)
            } catch (se: SecurityException) {
                se.printStackTrace()
            }
        }
    }

    /**
     * Al pulsar sobre el botón 'Donde estoy' muestra en el mapa la ubicación actual
     *
     * @param view
     */
    fun onClickDondeEstoy(view: View?) {
        // Creamos y componemos el Intent para abrir la aplicación de mapas
        val i = Intent()
        i.action = Intent.ACTION_VIEW
        val uri = Uri.parse("geo:0,0?q=" + lat + "," + lon)
        i.data = uri
        //lanza el Intent que abre la aplicación de mapas
        startActivity(i)
    }

    /**
     * Cuando cambia la localización muestra las nuevas coordenadas en la pantalla
     *
     * @param location
     */
    override fun onLocationChanged(location: Location) {
        // guardamos los valores de latitud y longitud
        lat = location.latitude
        lon = location.longitude
        // mostramos la posición en pantalla
        latitud!!.text = "Latitud: $lat"
        longitud!!.text = "Longitud: $lon"
    }

    override fun onProviderDisabled(provider: String) {
        // TODO Auto-generated method stub
    }

    override fun onProviderEnabled(provider: String) {
        // TODO Auto-generated method stub
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // TODO Auto-generated method stub
    }
    /*****************Abrir ventana de Ubicación para poder activar GPS  */
    /**
     * comprueba el estado del servicio de Ubicacion del dispositivo. Y si no está disponible,
     * muestra un cuadro de diálogo para activarlo
     *
     */
    private fun abreMiUbicacion() {
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        //si no está activada MiUbicaion en el dispositivo
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // muestra un diálogo de alerta para poder activarlo
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Ubicación desactivada")
            builder.setMessage("Por favor, actívela a continuación")
            builder.setPositiveButton("OK") { dialogInterface, i -> // muestra la configuración de MyUbicación del dispositivo
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            val alertDialog: Dialog = builder.create()
            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        }
    }
}