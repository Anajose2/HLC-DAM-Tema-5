package com.pms.a01_ud5_localizacion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

//la Activity implementa LocationListener
class MainActivity : AppCompatActivity(), LocationListener {
    private var manejador: LocationManager? = null
    private var salida: TextView? = null
    private var proveedor: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        salida = findViewById<View>(R.id.salida) as TextView
        //petición de permisos para APIs >=23 (versión 6.0, 6.0.1 Marshmallow)

        //si API de dispositivo >= API 23 -- pido permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Si los permisos han sido concedidos
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permisos", "Se tienen los permisos!")
                myProveedores()
            } else { //si no se conceden permisos: la primera vez que se ejecute la App o las siguientes si no se concede el permiso
                //muestra el diálogo pidiendo permisos y  se llamará al método onRequestPermissionsResult
                ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 122)
            }
        } else {
            myProveedores()
            // manejador.requestLocationUpdates(proveedor, 60000, 5, this);
        }
    }

    private fun myProveedores() {
        // crea un objeto de tipo LocationManager para manejar los servicios de localización
        // mediante la llamada a getSystemService(LOCATION_SERVICE)
        manejador = getSystemService(LOCATION_SERVICE) as LocationManager

        //muestra información en pantalla
        vista("Proveedores de localización: \n")

        // Lista todos los proveedores de localización disponibles
        muestraProveedores()
        val criteria = Criteria()
        // selecciona uno de los proveedores de localización según un criterio
        // de selección
        // En este ejemplo no se indica ninguna restricción (podría ser:coste,
        // energía, precisión et.)
        proveedor = manejador!!.getBestProvider(criteria, true)
        vista("Mejor proveedor: $proveedor\n")

        // Dependiendo del proveedor puede tardar cierto tiempo en darnos una
        // primera posición.
        // No obstante, Android recuerda la última posición que fue devuelta por
        // este proveedor
        vista("Comenzamos con la última localización conocida:")
        try {
            val localizacion = manejador!!.getLastKnownLocation(proveedor)
            // muestra una determinada localización
            muestraLocaliza(localizacion)
        } catch (se: SecurityException) {
            se.printStackTrace()
        }
    }

    /**
     * procede según la respuesta del usuario a la solicitud del permiso en el diálogo
     *
     * @param requestCode:  Código de petición del permiso (el entero asignado a esa petición)
     * @param permissions:  vector de permisos requeridos en la petición mediante requestPermissions
     * @param grantResults: vector de enteros asociado a la concesión o no de los permisos
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 122) {
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                myProveedores()
            } else {
                // Si el usuario no conccde el Permiso o lo rechaza, muestra un mensaje.
                Toast.makeText(this, "No hay permisos de Localización", Toast.LENGTH_LONG)
                        .show()
            }
        }
    }

    // Métodos del ciclo de vida de la Activity
    override fun onResume() {
        super.onResume()
        try {
            if (manejador != null) // Activamos notificaciones de localización:
            //(nombre proveedor, mínimo  tiempo en miliseg. entre actualizaciones,
            //distancia mínima entre localizaciones en metros,
            //el propio LocationListener)
                manejador!!.requestLocationUpdates(proveedor, 60000, 5f, this)
        } catch (se: SecurityException) {
            se.printStackTrace()
        }
        /** */
    }

    override fun onPause() {
        super.onPause()
        // Desactivamos notificaciones para ahorrar batería en caso de que el manejador este activo
        if (manejador != null) manejador!!.removeUpdates(this)
    }

    // Métodos de la interfaz LocationListener
    override fun onLocationChanged(location: Location) {
        vista("Nueva localización:")
        // para el objeto location recibido
        muestraLocaliza(location)
    }

    override fun onProviderDisabled(proveedor: String) {
        vista("Proveedor deshabilitado: $proveedor\n")
    }

    override fun onProviderEnabled(proveedor: String) {
        vista("Proveedor habilitado: $proveedor\n")
    }

    override fun onStatusChanged(provider: String, estado: Int, extras: Bundle) {
        //mediante E[] puede avisar 'fuera de servicio'
        vista("""
    Cambia estado proveedor: $proveedor, estado=${estado}, extras=$extras
    
    """.trimIndent())
    }
    // Métodos para mostrar información
    /**
     * Muestra información en el TextView de pantalla
     *
     * @param cadena: texto a mostrar
     */
    private fun vista(cadena: String) {
        salida!!.append("""
    $cadena
    
    """.trimIndent())
    }

    /**
     * Muestra la localización
     *
     * @param localizacion
     */
    private fun muestraLocaliza(localizacion: Location?) {
        if (localizacion == null) vista("Localización desconocida \n") else vista("""
    $localizacion
    
    """.trimIndent())
    }

    /**
     * Muestra los proveedores de localización encontrados
     */
    private fun muestraProveedores() {
        val proveedores = manejador!!.allProviders
        for (proveedor in proveedores) {
            muestraProveedor(proveedor)
        }
    }

    /**
     * Muestra información sobre el proveedor localizado
     *
     * @param proveedor
     */
    private fun muestraProveedor(proveedor: String) {
        // http://developer.android.com/intl/es/reference/android/location/LocationProvider.html
        val info = manejador!!.getProvider(proveedor)
        vista("""LocationProvider[getName=${info.name}, isProviderEnabled=${manejador!!.isProviderEnabled(proveedor)}, Precisión=${A[Math.max(0, info.accuracy)]}, Requerimiento energía=${P[Math.max(0, info.powerRequirement)]}, CosteMonetario=${info.hasMonetaryCost()}, RequiereIDCell=${info.requiresCell()}, RequiereNetwork=${info.requiresNetwork()}, RequiereSatelite=${info.supportsAltitude()}, InformaRumbo=${info.supportsBearing()}, InformaVelocidad=${info.supportsSpeed()} ]
""")
    }

    companion object {
        //Para la precisión del proveedor
        private val A = arrayOf("n/d", "preciso", "impreciso")

        //Para el requerimiento de energía
        private val P = arrayOf("n/d", "bajo", "medio", "alto")

        //Para indicar si está o no fuera de servicio
        private val E = arrayOf("fuera de servicio")
    }
}