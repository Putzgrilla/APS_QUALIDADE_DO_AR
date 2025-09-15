package com.example.aps_qualidade_do_ar

import okhttp3.*
import kotlinx.coroutines.*
import android.util.Log
import org.json.JSONObject

class ControleQualidadeAR {

    private val client = OkHttpClient()

    fun QualidadeAR(latitude: Double?, longitude: Double?, callback: (Double?, Double?, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("https://air-quality-api.open-meteo.com/v1/air-quality?latitude=${latitude}&longitude=${longitude}&current=pm10,pm2_5")
                    .build()

                val response = client.newCall(request).execute()
                val dados = response.body?.string()

                withContext(Dispatchers.Main) {
                    Log.d("API_DADOS", dados ?: "Nenhum dado")

                    try {
                        if (dados != null) {
                            val jsonObject = JSONObject(dados)
                            val current = jsonObject.getJSONObject("current")

                            val pm10 = current.getDouble("pm10")
                            val pm25 = current.getDouble("pm2_5")

                            Log.d("API_PM10", "PM10: $pm10 μg/m³")
                            Log.d("API_PM25", "PM2.5: $pm25 μg/m³")

                            // Chama o callback com sucesso
                            callback(pm10, pm25, null)
                        } else {
                            callback(null, null, "Dados vazios da API")
                        }
                    } catch (e: Exception) {
                        Log.e("JSON_ERRO", "Erro ao extrair PM10/PM2.5: ${e.message}")
                        callback(null, null, "Erro ao processar dados: ${e.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e("API_ERRO", "Erro: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(null, null, "Erro na requisição: ${e.message}")
                }
            }
        }
    }
}