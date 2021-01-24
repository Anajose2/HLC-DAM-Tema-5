package com.pms.a03_ud5_llevamealli

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private var cbDireccion: CheckBox? = null
    private var etDireccion: EditText? = null
    private var tvInformacion: TextView? = null

    // objeto para describir una localizaicón en forma de un conjunto de cadenas
    private var objetoDireccion: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cbDireccion = findViewById<View>(R.id.cbDireccion) as CheckBox
        etDireccion = findViewById<View>(R.id.etDireccion) as EditText
        tvInformacion = findViewById<View>(R.id.tvInformacion) as TextView
    }

    /**
     * Método que devuelve un objeto Addres a partir de una localización en coordenadas físicas
     * o dirección en texto
     * @param d: dirección indicada (en modo texto o como coordenaddas)
     * @param esDirec: booleano para indicar si es modo texto o en coordenadas
     * @return un objeto Address
     */
    private fun convertirGeocode(d: String, esDirec: Boolean): Address? {
        // lista de direcciones obtenidas por Geocoder
        var direcciones: List<Address?>? = null
        // Construimos el objeto Geocoder: clase que transforma direcciones textuales
        // a coordenadas de Lat, Long y a la inversa
        val geocoder = Geocoder(this)
        // si es una dirección postal (o sea, si el usuario marcó la casilla)
        if (esDirec) {
            try {
                // obtenemos el array de direcciones por el nombre, indicándole
                // que no guarde nada más que una
                direcciones = geocoder.getFromLocationName(d, 1)
                if (direcciones.isEmpty()) //Si dirección no viable
                    Toast.makeText(baseContext, "Imposible obtener esa dirección", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) { // si no se introduce nada, ni dirección ni coordenadas
                Toast.makeText(baseContext, "Imposible obtener dirección", Toast.LENGTH_SHORT).show()
            }
        } else {
            //expresión regular que se ajusta al patrón de coordenadas de geolocalizicón correctas
            val pattern = "^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$"
            val pat = Pattern.compile(pattern)
            val mat = pat.matcher(d)
            //si el patrón de coordenadas es correcto
            if (mat.matches()) {
                // el patrón tiene la forma correcta (por ejemplo, "36.840326,-2.459256"), empezamos
                // por romper la cadena por la ','
                val coordenadas = d.split(",").toTypedArray()
                // si hemos obtenido dos partes (como se espera)
                if (coordenadas != null && coordenadas.size == 2) {
                    try {
                        // convertimos cada parte a Double antes de pasarlas
                        // al método que obtiene el array de direcciones por las
                        // coordenadas, indicándole que sólo se quede con una
                        direcciones = geocoder.getFromLocation(coordenadas[0].toDouble(), coordenadas[1].toDouble(), 1)
                    } catch (e: NumberFormatException) {
                        Toast.makeText(baseContext, "Error en Coordenadas", Toast.LENGTH_LONG).show()
                    } catch (e: IOException) {
                        Toast.makeText(baseContext, "Error Coordenadas", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(baseContext, "Coordenadas no válidas", Toast.LENGTH_LONG).show()
            }
        }

        // devolvemos la dirección obtenida (o sea, el primer y único elemento
        // del array)
        return if (direcciones != null && direcciones.size > 0) direcciones[0] else null
    }

    /**
     * Al pulsar el botón Geocoder, muestra las características o parámetros de esa dirección,o
     * bien null, si no se corresponde
     * @param view
     */
    fun onClickGeocode(view: View?) {
        // obtiene los datos del UI
        val esDireccion = cbDireccion!!.isChecked
        val direccion = etDireccion!!.text.toString()
        // asigna el objeto Address la información de la dirección indicada
        objetoDireccion = convertirGeocode(direccion, esDireccion)
        if (objetoDireccion != null) // muestra la información a lo bruto (si se desea, cada parte
        // de ella se puede extraer mediante alguno de los muchos métodos
        // get disponibles, como .getCountryName(), .getPostalCode(), etc.)
            tvInformacion!!.text = objetoDireccion.toString() else tvInformacion!!.text=null
    }

    /**
     * Al pulsar el botón llévame allí, lanza el intent para posicionar esa localización en el mapa
     * @param view
     */
    fun onClickLlevame(view: View?) {
        // si está asignado el objeto dirección
        if (objetoDireccion != null) {
            // obtiene la latitud y la longitud
            val latitud = objetoDireccion!!.latitude
            val longitud = objetoDireccion!!.longitude

            // crea y compone un Intent para mostrar esa ubicación en el mapa
            val uri = Uri.parse("geo:0,0?q=$latitud,$longitud")
            val i = Intent()
            i.action = Intent.ACTION_VIEW
            i.data = uri
            //garantizará que la aplicación de Google Maps para Android administre la intención
            i.setPackage("com.google.android.apps.maps")

            //lanza el Intent del mapa
            startActivity(i)
        }
    }
}