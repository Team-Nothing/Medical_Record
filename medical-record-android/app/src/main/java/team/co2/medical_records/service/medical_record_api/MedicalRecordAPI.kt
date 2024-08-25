package team.co2.medical_records.service.medical_record_api

import android.util.Log
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MedicalRecordAPI {
    companion object {
//        private const val SERVER_URL = "http://192.168.0.130:8000/"
        private const val SERVER_URL = "http://163.18.44.160:8001/"
    }

    private var apiService: ApiService

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun register(username: String, password: String, ok: () -> Unit, error: (code: String) -> Unit) {
        val request = RegisterRequest(username, password)
        apiService.register(request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                when(val code = response.body()?.code) {
                    "OK" -> ok()
                    else -> {
                        val failedBody = response.errorBody()?.string() ?: ""
                        val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                        error(failed.code)
                    }
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                error("GENERIC/NETWORK_ERROR")
            }
        })
    }
}