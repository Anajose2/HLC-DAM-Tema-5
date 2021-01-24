package com.pms.mapasgoogle

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class ControlarMapa : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    // añadidas
    private val client: GoogleApiClient? = null
    private var vistaId = 0
    private var camPos: CameraPosition? = null
    private var camUpd: CameraUpdate? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controlar_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng((-34).toDouble(), (151).toDouble())
        mMap!!.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    /**
     * Activa el menú
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.controlar_mapa_menu, menu)
        return true
    }

    /**
     * Responde a la pulsación del item del menú
     * @param item opción del menú
     * @return true para opción seleccionada
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_alcazaba -> {
                alcazaba()
                true
            }
            R.id.menu_posicion -> {
                posicion()
                true
            }
            R.id.menu_spain -> {
                spain()
                true
            }
            R.id.menu_spain_zoom -> {
                spainx5()
                true
            }
            R.id.menu_vista -> {
                alternarVista()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Centra en el mapa y muestra la Alcazaba de Almería con ciertas opciones
     */
    private fun alcazaba() {
        // centra la Alcazaba de Almería en vistaId híbrida
        val alcazaba = LatLng(36.840835, -2.471172)
        camPos = CameraPosition.Builder().target(alcazaba) // con zoom x19
                .zoom(19f) // orientación noreste (o giro de 45 grados sexagesimales
                // este -ver la brújula arriba a la izquierda)
                .bearing(45f) // punto de vistaId bajo para ver más suelo (70 grados
                // sexagesimales con respecto a la vertical)
                .tilt(80f).build()
        camUpd = CameraUpdateFactory.newCameraPosition(camPos)
        mMap!!.animateCamera(camUpd)
    }

    /**
     * Gestiona la laternancia de vistas del mapa
     * Al pulsar sobre 'vista' cambia a la siguiente
     */
    private fun alternarVista() {
        var vista: String? = null
        // establece la siguiente a la actual
        vistaId = ++vistaId % 5
        when (vistaId) {
            0 -> {
                mMap!!.mapType = GoogleMap.MAP_TYPE_HYBRID
                vista = "MAP_TYPE_HYBRID"
            }
            1 -> {
                mMap!!.mapType = GoogleMap.MAP_TYPE_NONE
                vista = "MAP_TYPE_NONE"
            }
            2 -> {
                mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
                vista = "MAP_TYPE_NORMAL"
            }
            3 -> {
                mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
                vista = "MAP_TYPE_SATELLITE"
            }
            4 -> {
                mMap!!.mapType = GoogleMap.MAP_TYPE_TERRAIN
                vista = "MAP_TYPE_TERRAIN"
            }
        }
        //muestra su nombre
        Toast.makeText(this@ControlarMapa, "Vista: $vista", Toast.LENGTH_SHORT).show()
    }

    /**
     * Muestra en una tostada las coordenadas de la posición actual de la cámara
     */
    private fun posicion() {
        val camPos = mMap!!.cameraPosition
        val pos = camPos.target
        Toast.makeText(this, "(Lat: " + pos.latitude + ", Lng: " + pos.longitude + ")",
                Toast.LENGTH_LONG).show()
    }

    /**
     * Centra y muestra en el mapa a España con zoom x6 en vista normal animada
     */
    private fun spain() {
        camUpd = CameraUpdateFactory.newLatLngZoom(LatLng(40.41, -3.69), 6f)
        mMap!!.moveCamera(camUpd)
    }

    /**
     * Centra y muestra en el mapa a España con zoom x5 y vista normal animada
     */
    private fun spainx5() {
        camUpd = CameraUpdateFactory.newLatLngZoom(LatLng(40.41, -3.69), 5f)
        mMap!!.animateCamera(camUpd)
    }
}