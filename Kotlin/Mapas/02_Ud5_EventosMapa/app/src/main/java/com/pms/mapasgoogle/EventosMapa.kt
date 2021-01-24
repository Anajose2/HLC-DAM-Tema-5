package com.pms.mapasgoogle

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions

class EventosMapa : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mapa)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.eventos_mapa_menu, menu)
        return true
    }

    /**
     * gestiona la pulsación de un elemento del menú
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear ->                 //limpia el mapa
                mMap!!.clear()
            R.id.menu_lineas ->                 // recuadra España
                reuadraSpain()
            R.id.menu_marcadores ->                 // marca España
                marcaSpain()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Coloca un marcador sobre España
     */
    private fun marcaSpain() {
        mMap!!.addMarker(MarkerOptions().position(LatLng(40.5, -3.5))
                .title("País: España"))
    }

    /**
     * Dibuja sobre el mapa las líneas especificadas: un cuadrado
     */
    private fun reuadraSpain() {

        // Dibujo con Lineas: PolylineOptions
        /*
        PolylineOptions lineas = new PolylineOptions()
                .add(new LatLng(45.0, -12.0)).add(new LatLng(45.0, 5.0))
                .add(new LatLng(34.5, 5.0)).add(new LatLng(34.5, -12.0))
                .add(new LatLng(45.0, -12.0));

        lineas.width(8);
        lineas.color(Color.RED);

        mMap.addPolyline(lineas);
*/
        // Dibujo con polígonos cerrados: PolygonOptions
        val rectangulo = PolygonOptions().add(LatLng(45.0,
                -12.0), LatLng(45.0, 5.0), LatLng(34.5, 5.0),
                LatLng(34.5, -12.0))
        rectangulo.strokeWidth(8f)
        rectangulo.strokeColor(Color.RED)
        mMap!!.addPolygon(rectangulo)
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

        // CÓDIGO APP *****************************************************************
        // Enlaza los eventos del mapa
        /**
         * gestiona el evento OnMapClick
         */
        mMap!!.setOnMapClickListener { point -> //Obtiene las coordenadas de pantalla (x,y) correspondientes a las coordenadas LatLng
            //objeto Projection para convertir entre latitud-longitud en grados y coordenadas x-y en pixeles
            val proj = mMap!!.projection
            //objeto que permite expresar coordenadas de pantalla
            val coord = proj.toScreenLocation(point)
            Toast.makeText(
                    this@EventosMapa,
                    """Click
Lat: ${point.latitude}
Lng: ${point.longitude}
X: ${coord.x} - Y: ${coord.y}""", Toast.LENGTH_SHORT)
                    .show()
        }
        /**
         * Gestiona el evento onMapLongClick
         * Muestra una tostada con el punto terrestre y coordenadas de pantalla
         */
        mMap!!.setOnMapLongClickListener { point ->
            val proj = mMap!!.projection
            val coord = proj.toScreenLocation(point)
            Toast.makeText(
                    this@EventosMapa,
                    """Click Largo
Lat: ${point.latitude}
Lng: ${point.longitude}
X: ${coord.x} - Y: ${coord.y}""",
                    Toast.LENGTH_SHORT).show()
        }
        /**
         * Gestiona el evento onCameraChange
         * Muestra las coordenadas del punto terrestre, zoom, orientación y ángulo
         */
        mMap!!.setOnCameraChangeListener { position ->
            Toast.makeText(
                    this@EventosMapa,
                    """
                         Cambio Cámara
                         Lat: ${position.target.latitude}
                         Lng: ${position.target.longitude}
                         Zoom: ${position.zoom}
                         Orientación: ${position.bearing}
                         Ángulo: ${position.tilt}
                         """.trimIndent(),
                    Toast.LENGTH_SHORT).show()
        }
        /**
         * Gestiona el evento onMarkerClick
         * Al pulsar sobre el marcador muestra el título asociado
         */
        mMap!!.setOnMarkerClickListener { marker ->
            Toast.makeText(this@EventosMapa,
                    """
                         Marcador pulsado:
                         ${marker.title}
                         """.trimIndent(),
                    Toast.LENGTH_SHORT).show()
            false
        }
    }
}