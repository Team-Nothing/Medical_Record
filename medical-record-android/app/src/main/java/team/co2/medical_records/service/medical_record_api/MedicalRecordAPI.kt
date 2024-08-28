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
        private const val SERVER_URL = "http://163.18.44.160:8001/"
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiServiceLogin = retrofit.create(LoginService::class.java)
        apiServiceCheckSession = retrofit.create(CheckSessionService::class.java)
        apiServiceRenewSession = retrofit.create(RenewSessionService::class.java)
        apiServiceResetPassword = retrofit.create(resetPasswordService::class.java)
        apiServiceAuthenticate = retrofit.create(authenticateService::class.java)

        val retrofitForRegister: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofitForRegister.create(ApiService::class.java)
    }

    private var apiServiceLogin: LoginService
    private var apiServiceCheckSession: CheckSessionService
    private var apiServiceRenewSession: RenewSessionService
    private var apiServiceResetPassword: resetPasswordService
    private var apiServiceAuthenticate: authenticateService

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