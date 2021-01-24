package com.pms.mapasgoogle;

import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class EventosMapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.eventos_mapa_menu, menu);
        return true;
    }

    /**
     * gestiona la pulsación de un elemento del menú
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_clear:
                //limpia el mapa
                mMap.clear();
                break;
            case R.id.menu_lineas:
                // recuadra España
                reuadraSpain();
                break;
            case R.id.menu_marcadores:
                // marca España
                marcaSpain();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Coloca un marcador sobre España
     */
    private void marcaSpain() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.5, -3.5))
                .title("País: España"));
    }

    /**
     * Dibuja sobre el mapa las líneas especificadas: un cuadrado
     */
    private void reuadraSpain() {

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
        PolygonOptions rectangulo = new PolygonOptions().add(new LatLng(45.0,
                        -12.0), new LatLng(45.0, 5.0), new LatLng(34.5, 5.0),
                new LatLng(34.5, -12.0));

        rectangulo.strokeWidth(8);
        rectangulo.strokeColor(Color.RED);

        mMap.addPolygon(rectangulo);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // CÓDIGO APP *****************************************************************
        // Enlaza los eventos del mapa

        /**
         * gestiona el evento OnMapClick
         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                //Obtiene las coordenadas de pantalla (x,y) correspondientes a las coordenadas LatLng
                //objeto Projection para convertir entre latitud-longitud en grados y coordenadas x-y en pixeles
                Projection proj = mMap.getProjection();
                //objeto que permite expresar coordenadas de pantalla
                Point coord = proj.toScreenLocation(point);

                Toast.makeText(
                        EventosMapa.this,
                        "Click\n" + "Lat: " + point.latitude + "\n" + "Lng: "
                                + point.longitude + "\n" + "X: " + coord.x
                                + " - Y: " + coord.y, Toast.LENGTH_SHORT)
                        .show();
            }
        });

        /**
         * Gestiona el evento onMapLongClick
         * Muestra una tostada con el punto terrestre y coordenadas de pantalla
         */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                Projection proj = mMap.getProjection();
                Point coord = proj.toScreenLocation(point);

                Toast.makeText(
                        EventosMapa.this,
                        "Click Largo\n" + "Lat: " + point.latitude + "\n"
                                + "Lng: " + point.longitude + "\n" + "X: "
                                + coord.x + " - Y: " + coord.y,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Gestiona el evento onCameraChange
         * Muestra las coordenadas del punto terrestre, zoom, orientación y ángulo
         */
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                Toast.makeText(
                        EventosMapa.this,
                        "Cambio Cámara\n" + "Lat: " + position.target.latitude
                                + "\n" + "Lng: " + position.target.longitude
                                + "\n" + "Zoom: " + position.zoom + "\n"
                                + "Orientación: " + position.bearing + "\n"
                                + "Ángulo: " + position.tilt,
                        Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Gestiona el evento onMarkerClick
         * Al pulsar sobre el marcador muestra el título asociado
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(EventosMapa.this,
                        "Marcador pulsado:\n" + marker.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


}
