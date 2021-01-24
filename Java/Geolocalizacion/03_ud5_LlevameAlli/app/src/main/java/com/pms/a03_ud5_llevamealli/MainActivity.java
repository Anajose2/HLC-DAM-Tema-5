package com.pms.a03_ud5_llevamealli;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

  private CheckBox cbDireccion;
  private EditText etDireccion;
  private TextView tvInformacion;

  // objeto para describir una localizaicón en forma de un conjunto de cadenas
  private Address objetoDireccion = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cbDireccion = (CheckBox) findViewById(R.id.cbDireccion);
    etDireccion = (EditText) findViewById(R.id.etDireccion);
    tvInformacion = (TextView) findViewById(R.id.tvInformacion);
  }


  /**
   * Método que devuelve un objeto Addres a partir de una localización en coordenadas físicas
   * o dirección en texto
   * @param d: dirección indicada (en modo texto o como coordenaddas)
   * @param esDirec: booleano para indicar si es modo texto o en coordenadas
   * @return un objeto Address
   */
  private Address convertirGeocode(String d, boolean esDirec) {
    // lista de direcciones obtenidas por Geocoder
    List<Address> direcciones = null;
    // Construimos el objeto Geocoder: clase que transforma direcciones textuales
    // a coordenadas de Lat, Long y a la inversa
    Geocoder geocoder = new Geocoder(this);
    // si es una dirección postal (o sea, si el usuario marcó la casilla)
    if (esDirec) {
      try {
        // obtenemos el array de direcciones por el nombre, indicándole
        // que no guarde nada más que una
        direcciones = geocoder.getFromLocationName(d, 1);
        if(direcciones.isEmpty())//Si dirección no viable
          Toast.makeText(getBaseContext(), "Imposible obtener esa dirección", Toast.LENGTH_SHORT).show();
      } catch (IOException e) {// si no se introduce nada, ni dirección ni coordenadas
        Toast.makeText(getBaseContext(), "Imposible obtener dirección", Toast.LENGTH_SHORT).show();
      }
    }
    // si no es una dirección postal (lo que nos indica el propio usuario
    // cuando no marca la casilla)
    else {
      //expresión regular que se ajusta al patrón de coordenadas de geolocalizicón correctas
      String pattern = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$";
      Pattern pat = Pattern.compile(pattern);
      Matcher mat = pat.matcher(d);
      //si el patrón de coordenadas es correcto
      if(mat.matches()){
        // el patrón tiene la forma correcta (por ejemplo, "36.840326,-2.459256"), empezamos
        // por romper la cadena por la ','
        String[] coordenadas = d.split(",");
        // si hemos obtenido dos partes (como se espera)
        if (coordenadas != null && coordenadas.length == 2) {
          try {
            // convertimos cada parte a Double antes de pasarlas
            // al método que obtiene el array de direcciones por las
            // coordenadas, indicándole que sólo se quede con una
            direcciones = geocoder.getFromLocation(
                Double.parseDouble(coordenadas[0]),
                Double.parseDouble(coordenadas[1]), 1);
          } catch (NumberFormatException e) {
            Toast.makeText(getBaseContext(), "Error en Coordenadas", Toast.LENGTH_LONG).show();
          } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error Coordenadas", Toast.LENGTH_LONG).show();
          }
        }
      }else
      {
        Toast.makeText(getBaseContext(), "Coordenadas no válidas", Toast.LENGTH_LONG).show();
      }


    }

    // devolvemos la dirección obtenida (o sea, el primer y único elemento
    // del array)
    if (direcciones != null && direcciones.size() > 0)
      return direcciones.get(0);
    else
      return null;
  }

  /**
   * Al pulsar el botón Geocoder, muestra las características o parámetros de esa dirección,o
   * bien null, si no se corresponde
   * @param view
   */

  public void onClickGeocode(View view) {
    // obtiene los datos del UI
    boolean esDireccion = cbDireccion.isChecked();
    String direccion = etDireccion.getText().toString();
    // asigna el objeto Address la información de la dirección indicada
    objetoDireccion = convertirGeocode(direccion, esDireccion);
    if (objetoDireccion != null)
      // muestra la información a lo bruto (si se desea, cada parte
      // de ella se puede extraer mediante alguno de los muchos métodos
      // get disponibles, como .getCountryName(), .getPostalCode(), etc.)
      tvInformacion.setText(objetoDireccion.toString());
    else
      tvInformacion.setText(null);
  }

  /**
   * Al pulsar el botón llévame allí, lanza el intent para posicionar esa localización en el mapa
   * @param view
   */
  public void onClickLlevame(View view) {
    // si está asignado el objeto dirección
    if (objetoDireccion != null) {
      // obtiene la latitud y la longitud
      double latitud = objetoDireccion.getLatitude();
      double longitud = objetoDireccion.getLongitude();

      // crea y compone un Intent para mostrar esa ubicación en el mapa
      Uri uri = Uri.parse("geo:0,0?q=" + latitud + "," + longitud);
      Intent i = new Intent();
      i.setAction(Intent.ACTION_VIEW);
      i.setData(uri);
      //garantizará que la aplicación de Google Maps para Android administre la intención
      i.setPackage("com.google.android.apps.maps");

      //lanza el Intent del mapa
      startActivity(i);
    }
  }

}
