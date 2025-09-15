package com.example.aps_qualidade_do_ar

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient



    private lateinit var gps: Gps
    private val controleQualidadeAR = ControleQualidadeAR()
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.telaprincipal)



        gps = Gps(this)


        statusTextView = findViewById(R.id.status)
        buscardados()
        atualizar()
    }

    private fun avaliarQualidade(pm10: Double, pm25: Double): String {
        val nivelPM10 = when {
            pm10 <= 20 -> "Boa"
            pm10 <= 40 -> "Moderada"
            pm10 <= 50 -> "Ruim para grupos sensÃ­veis"
            pm10 <= 100 -> "Ruim"
            pm10 <= 150 -> "Muito ruim"
            else -> "Perigosa"
        }

        val nivelPM25 = when {
            pm25 <= 10 -> "Boa"
            pm25 <= 25 -> "Moderada"
            pm25 <= 50 -> "Ruim para grupos sensÃ­veis"
            pm25 <= 75 -> "Ruim"
            pm25 <= 100 -> "Muito ruim"
            else -> "Perigosa"
        }

        return " \nPM10: $pm10 $nivelPM10\nPM2.5: $pm25 $nivelPM25"
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        gps.onRequestPermissionsResult(requestCode, grantResults)
    }

    fun buscardados(){
        statusTextView.text = "Obtendo localização..."
        if (gps.checkLocationPermissions(this)) {
            gps.obterLocalizacao(this) { latitude, longitude, erro ->
                controleQualidadeAR.QualidadeAR(latitude, longitude) { pm10, pm25, erro ->
                    if (pm10 != null && pm25 !=null){
                        var valores =avaliarQualidade(pm10,pm25)
                        statusTextView.text= """Localização $latitude  $longitude $valores"""


                    }else{
                        statusTextView.text= "Erro ao obter dados"
                    }
                }
            }
        } else {
            gps.solicitarPermissoes(this)
        }

    }
    fun atualizar(){
        var botao = findViewById<Button>(R.id.botao_atualizar)
        botao.setOnClickListener {

            buscardados()
        }

    }
}