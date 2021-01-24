package com.pms.mapasgoogle

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil

//paquete que suministra diferentes utilidades para mapas, como computeLength()
//se apoya en la clase MathUtil
/* ******************************************************************************************* */ //si importo librería mediante el Gradle
//Si incluyo en proyecto las clases necesarias: MathUtil.java, SphericalUtil.java
//import static com.pms.mapasgoogle.SphericalUtil.*;
/* ******************************************************************************************* */
class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    //el objeto GoogleMap es una representación interna del mapa
    private var mMap: GoogleMap? = null

    //objeto polilínea para marcar los segmentos en mapa
    private var linea: PolylineOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        // CÓDIGO APP *********************************************************************
        // ¡Atención!: el enlace de los eventos del mapa ya no se realiza aquí, sino en el
        // callback 'onMapReady' que se llama justo cuando el mapa está listo
    }

    /**
     * Acciones a realizar cuando el mapa está listo:
     * sitúa la cámara del mapa sobre España con zoom 5
     *
     * @param googleMap: objeto que representa un mapa
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //inicia el objeto CameraUpdate con las coordenas y zoom que interesan (España con zoom 5)
        val camUpd = CameraUpdateFactory.newLatLngZoom(LatLng(40.41, -3.69), 5f)
        //mueve la cámara del mapa de forma animada sobre los parametros antes indicados
        mMap!!.animateCamera(camUpd)
        /**
         * Gestiona el evento onMapClick (click sobre el mapa)
         */
        mMap!!.setOnMapClickListener { point ->
            /**
             * Al hacer un click corto sobre el mapa, añade una nueva línea hasta ese punto
             * o pone un marcador si es el primer click
             * @param point: coordenadas o punto (Latitud, Longitud)
             */
            //si no hay ninguna marca
            if (linea == null) {
                // inicia un nuevo objeto Polyline
                linea = PolylineOptions()
                // establece las propiedades del trazo:ancho y color
                linea!!.width(8f)
                linea!!.color(Color.RED)
                // añade un marcador distintivo en el mapa en el punto pulsado
                mMap!!.addMarker(MarkerOptions().position(point).title(
                        "Salida (" + point.latitude + "," + point.longitude
                                + ")"))
            }

            // agrega el punto al objeto PolyLine
            linea!!.add(point)
            // dibuja el segmento o línea en el mapa si procede
            mMap!!.addPolyline(linea)
        }
    }

    /**
     * Gestiona el menú de la App
     *
     * @param menu
     * @return
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.ruta_mapa_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Gestiona la pulsación de un elemento del menú
     *
     * @param item: elemento del menú
     * @return
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear -> {
                //límpia de marcas el mapa
                mMap!!.clear()
                // anula los segmentos de líneas
                if (linea != null) linea = null else Toast.makeText(this, "Ninguna marca en mapa", Toast.LENGTH_SHORT).show()
            }
            R.id.menu_longitud ->                 // muestra la longitud de las líneas o segmento trazado
                if (linea != null) Toast.makeText(this, "Longitud segmento trazado: " + distanciaEnMetros()
                        + " m.", Toast.LENGTH_LONG).show() else Toast.makeText(this, "Ninguna línea trazada", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
    /* ******************************************************************************************* */
    /**
     * Calcula y devuelve el perímetro o distancia del segmento trazado
     *
     * @return: longitud en metros del segmento
     */
    private fun distanciaEnMetros(): Double {
        //CLASE IMPORTADA SphericalUtil
        return Math.round(SphericalUtil.computeLength(linea!!.points)).toDouble()
    }
}