package com.pms.a01_ud5_localizacion;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

//la Activity implementa LocationListener
public class MainActivity extends AppCompatActivity implements LocationListener {

    //Para la precisión del proveedor
    private static final String[] A = {"n/d", "preciso", "impreciso"};
    //Para el requerimiento de energía
    private static final String[] P = {"n/d", "bajo", "medio", "alto"};
    //Para indicar si está o no fuera de servicio
    private static final String[] E = {"fuera de servicio"};

    private LocationManager manejador;
    private TextView salida;
    private String proveedor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salida = (TextView) findViewById(R.id.salida);
        //petición de permisos para APIs >=23 (versión 6.0, 6.0.1 Marshmallow)

        //si API de dispositivo >= API 23 -- pido permisos

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Si los permisos han sido concedidos
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Permisos", "Se tienen los permisos!");

                myProveedores();
            } else {//si no se conceden permisos: la primera vez que se ejecute la App o las siguientes si no se concede el permiso
                //muestra el diálogo pidiendo permisos y  se llamará al método onRequestPermissionsResult
                ActivityCompat.requestPermissions(
                        this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 122);
            }
        }else {
            myProveedores();
            // manejador.requestLocationUpdates(proveedor, 60000, 5, this);
        }



    }

    private void myProveedores() {
        // crea un objeto de tipo LocationManager para manejar los servicios de localización
        // mediante la llamada a getSystemService(LOCATION_SERVICE)
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);

        //muestra información en pantalla
        vista("Proveedores de localización: \n");

        // Lista todos los proveedores de localización disponibles
        muestraProveedores();

        Criteria criteria = new Criteria();
        // selecciona uno de los proveedores de localización según un criterio
        // de selección
        // En este ejemplo no se indica ninguna restricción (podría ser:coste,
        // energía, precisión et.)
        proveedor = manejador.getBestProvider(criteria, true);
        vista("Mejor proveedor: " + proveedor + "\n");

        // Dependiendo del proveedor puede tardar cierto tiempo en darnos una
        // primera posición.
        // No obstante, Android recuerda la última posición que fue devuelta por
        // este proveedor
        vista("Comenzamos con la última localización conocida:");
        try {
            Location localizacion = manejador.getLastKnownLocation(proveedor);
            // muestra una determinada localización
            muestraLocaliza(localizacion);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }


    /**
     * procede según la respuesta del usuario a la solicitud del permiso en el diálogo
     *
     * @param requestCode:  Código de petición del permiso (el entero asignado a esa petición)
     * @param permissions:  vector de permisos requeridos en la petición mediante requestPermissions
     * @param grantResults: vector de enteros asociado a la concesión o no de los permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 122) {
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                myProveedores();
            } else {
                // Si el usuario no conccde el Permiso o lo rechaza, muestra un mensaje.
                Toast.makeText(this, "No hay permisos de Localización", Toast.LENGTH_LONG)
                        .show();
            }


        }
    }


    // Métodos del ciclo de vida de la Activity
    @Override
    protected void onResume() {
        super.onResume();

        try {
            if(manejador!=null)
                // Activamos notificaciones de localización:
                //(nombre proveedor, mínimo  tiempo en miliseg. entre actualizaciones,
                //distancia mínima entre localizaciones en metros,
                //el propio LocationListener)
                manejador.requestLocationUpdates(proveedor, 60000, 5, this);
        }catch(SecurityException se){
            se.printStackTrace();
        }
        /*******************************/


    }

    @Override
    protected void onPause() {
        super.onPause();
        // Desactivamos notificaciones para ahorrar batería en caso de que el manejador este activo
        if(manejador!=null)
            manejador.removeUpdates(this);

    }

    // Métodos de la interfaz LocationListener
    @Override
    public void onLocationChanged(Location location) {
        vista("Nueva localización:");
        // para el objeto location recibido
        muestraLocaliza(location);
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        vista("Proveedor deshabilitado: " + proveedor + "\n");

    }

    @Override
    public void onProviderEnabled(String proveedor) {
        vista("Proveedor habilitado: " + proveedor + "\n");

    }

    @Override
    public void onStatusChanged(String provider, int estado, Bundle extras) {
        //mediante E[] puede avisar 'fuera de servicio'
        vista("Cambia estado proveedor: " + proveedor + ", estado="
                + estado + ", extras=" + extras + "\n");
    }

    // Métodos para mostrar información

    /**
     * Muestra información en el TextView de pantalla
     *
     * @param cadena: texto a mostrar
     */
    private void vista(String cadena) {
        salida.append(cadena + "\n");
    }

    /**
     * Muestra la localización
     *
     * @param localizacion
     */
    private void muestraLocaliza(Location localizacion) {
        if (localizacion == null)
            vista("Localización desconocida \n");
        else
            vista(localizacion.toString() + "\n");
    }

    /**
     * Muestra los proveedores de localización encontrados
     */
    private void muestraProveedores() {
        List<String> proveedores = manejador.getAllProviders();
        for (String proveedor : proveedores) {
            muestraProveedor(proveedor);
        }
    }

    /**
     * Muestra información sobre el proveedor localizado
     *
     * @param proveedor
     */
    private void muestraProveedor(String proveedor) {
        // http://developer.android.com/intl/es/reference/android/location/LocationProvider.html
        LocationProvider info = manejador.getProvider(proveedor);
        vista("LocationProvider[" + "getName=" + info.getName()
                + ", isProviderEnabled="
                + manejador.isProviderEnabled(proveedor) + ", Precisión="
                + A[Math.max(0, info.getAccuracy())] + ", Requerimiento energía="
                + P[Math.max(0, info.getPowerRequirement())]
                + ", CosteMonetario=" + info.hasMonetaryCost()
                + ", RequiereIDCell=" + info.requiresCell()
                + ", RequiereNetwork=" + info.requiresNetwork()
                + ", RequiereSatelite=" + info.supportsAltitude()
                + ", InformaRumbo=" + info.supportsBearing()
                + ", InformaVelocidad=" + info.supportsSpeed() + " ]\n");

    }

}

