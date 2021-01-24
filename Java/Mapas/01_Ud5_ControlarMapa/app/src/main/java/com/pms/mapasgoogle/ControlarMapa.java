package com.pms.mapasgoogle;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ControlarMapa extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    // añadidas
    private GoogleApiClient client;
    private int vistaId;
    private CameraPosition camPos;
    private CameraUpdate camUpd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlar_mapa);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Activa el menú
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.controlar_mapa_menu, menu);
        return true;
    }


    /**
     * Responde a la pulsación del item del menú
     * @param item opción del menú
     * @return true para opción seleccionada
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_alcazaba:
                alcazaba();
                return true;
            case R.id.menu_posicion:
                posicion();
                return true;
            case R.id.menu_spain:
                spain();
                return true;
            case R.id.menu_spain_zoom:
                spainx5();
                return true;
            case R.id.menu_vista:
                alternarVista();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Centra en el mapa y muestra la Alcazaba de Almería con ciertas opciones
     */
    private void alcazaba() {
        // centra la Alcazaba de Almería en vistaId híbrida
        LatLng alcazaba = new LatLng(36.840835, -2.471172);
        camPos = new CameraPosition.Builder().target(alcazaba)
                // con zoom x19
                .zoom(19)
                // orientación noreste (o giro de 45 grados sexagesimales
                // este -ver la brújula arriba a la izquierda)
                .bearing(45)
                // punto de vistaId bajo para ver más suelo (70 grados
                // sexagesimales con respecto a la vertical)
                .tilt(80).build();
        camUpd = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd);
    }


    /**
     * Gestiona la laternancia de vistas del mapa
     * Al pulsar sobre 'vista' cambia a la siguiente
     */
    private void alternarVista() {
        String vista=null;
        // establece la siguiente a la actual
        vistaId =++vistaId %5;
        switch (vistaId) {
            case 0:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                vista="MAP_TYPE_HYBRID";
                break;
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                vista="MAP_TYPE_NONE";
                break;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                vista="MAP_TYPE_NORMAL";
                break;
            case 3:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                vista="MAP_TYPE_SATELLITE";
                break;
            case 4:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                vista="MAP_TYPE_TERRAIN";
                break;
        }
        //muestra su nombre
        Toast.makeText(ControlarMapa.this, "Vista: "+ vista, Toast.LENGTH_SHORT).show();
    }



    /**
     * Muestra en una tostada las coordenadas de la posición actual de la cámara
     */
    private void posicion()
    {
        camPos = mMap.getCameraPosition();
        LatLng pos = camPos.target;
        Toast.makeText(this, "(Lat: " + pos.latitude + ", Lng: " + pos.longitude + ")",
                Toast.LENGTH_LONG).show();
    }

    /**
     * Centra y muestra en el mapa a España con zoom x6 en vista normal animada
     */
    private void spain()
    {
        camUpd = CameraUpdateFactory.newLatLngZoom(new LatLng(40.41, -3.69), 6f);
        mMap.moveCamera(camUpd);
    }


    /**
     * Centra y muestra en el mapa a España con zoom x5 y vista normal animada
     */
    private void spainx5()
    {
        camUpd = CameraUpdateFactory.newLatLngZoom(new LatLng(40.41, -3.69), 5f);
        mMap.animateCamera(camUpd);
    }

}
