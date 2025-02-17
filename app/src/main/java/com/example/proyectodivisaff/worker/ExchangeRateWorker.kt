import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.proyectodivisaff.data.RetrofitCliente
import com.example.proyectodivisaff.data.RetrofitService
import com.example.proyectodivisaff.model.ExchangeRateResponse
import com.google.gson.Gson

import retrofit2.Response
import java.io.IOException

class ExchangeRateWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        // Crear instancia del servicio de la API
        val service = RetrofitCliente.instance.create(RetrofitService::class.java)
        val apiKey = "fb2e294992766ce64e11aa61"  // clave de registro de la api
        val baseCurrency = "USD"   //

        return try {
            // Realizar la llamada de forma sincrónica
            val response: Response<ExchangeRateResponse> = service.getLatestRates(apiKey, baseCurrency).execute()
            if (response.isSuccessful && response.body() != null) {
                val exchangeResponse = response.body()!!
                val rates = exchangeResponse.conversionRates
                Log.d("ExchangeRateWorker", "Tasas actualizadas: $rates")

                // Guardar los datos en SharedPreferences
                val sharedPreferences = applicationContext.getSharedPreferences("ExchangeRates", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                val ratesJson = Gson().toJson(rates)
                editor.putString("rates", ratesJson)
                editor.apply()

                Result.success()  // Trabajo completado correctamente
            } else {
                Log.e("ExchangeRateWorker", "Respuesta fallida: ${response.errorBody()?.string()}")
                Result.retry()  // Reintentar en caso de error en la respuesta
            }
        } catch (e: IOException) {
            Log.e("ExchangeRateWorker", "Error de red", e)
            Result.retry()  // Reintentar en caso de excepción de red
        }
    }
}
