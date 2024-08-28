package team.co2.medical_records.service.medical_record_api

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.co2.medical_records.service.device.DeviceInformation
import java.util.concurrent.TimeUnit


class MedicalRecordAPI(private val context: Context) {
    companion object {
        private const val SERVER_URL = "http://163.18.44.160:8001/"
    }

    private val apiService: ApiService
    private val deviceInformation: DeviceInformation = DeviceInformation(context)


    init {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) // Set the connect timeout
            .readTimeout(10, TimeUnit.SECONDS) // Set the read timeout
            .writeTimeout(60, TimeUnit.SECONDS) // Set the write timeout
            .build()

        val retrofitForRegister: Retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofitForRegister.create(ApiService::class.java)
    }

    fun status(ok: () -> Unit, error: (code: GenericError?) -> Unit) {
        apiService.status().enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                try{
                    when(response.body()?.code) {
                        "OK" -> ok()
                        else -> {
                            val failedBody = response.errorBody()?.string() ?: ""
                            val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                            error(GenericError.fromCode(failed.code))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MedicalRecordAPI", "status: ${e.message}")
                    error(GenericError.API_FAILED_FETCH_RESULT)
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                error(GenericError.NETWORK)
            }
        })
    }

    fun authRegister(username: String, password: String, conPassword: String, ok: () -> Unit, error: (code: AuthError?) -> Unit) {
        if (username.trim() == "") {
            error(AuthError.USERNAME_IS_EMPTY)
            return
        }
        if (password.trim() == "") {
            error(AuthError.PASSWORD_IS_EMPTY)
            return
        }
        if (conPassword.trim() == "" || conPassword != password) {
            error(AuthError.PASSWORD_MISMATCH)
            return
        }

        val request = RegisterRequest(username.trim(), password.trim())
        apiService.register(request).enqueue(object : Callback<GenericResponse> {
            override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                try {
                    when(response.body()?.code) {
                        "OK" -> ok()
                        else -> {
                            val failedBody = response.errorBody()?.string() ?: ""
                            val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                            error(AuthError.fromCode(failed.code))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MedicalRecordAPI", "register: ${e.message}")
                    error(AuthError.API_FAILED_FETCH_RESULT)
                }
            }

            override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                error(AuthError.NETWORK)
            }
        })
    }

    fun authLogin(username: String, password: String, ok: () -> Unit, error: (code: AuthError?) -> Unit) {
        if (username.trim() == "") {
            error(AuthError.USERNAME_IS_EMPTY)
            return
        }
        if (password.trim() == "") {
            error(AuthError.PASSWORD_IS_EMPTY)
            return
        }

        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(AuthError.MISSING_DEVICE_ID)
                return@getDeviceId
            }

            val request = LoginRequest(username.trim(), password.trim())
            apiService.login(request, deviceId).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    try {
                        when(response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
                                if (data != null) {
                                    context.getSharedPreferences(
                                        "session", Context.MODE_PRIVATE
                                    ).edit().putString("session", "Bearer ${data.token}").apply()
                                    ok()
                                } else {
                                    error(AuthError.API_FAILED_FETCH_RESULT)
                                }
                            }
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed = Gson().fromJson(failedBody, LoginResponse::class.java)
                                error(AuthError.fromCode(failed.code))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MedicalRecordAPI", "register: ${e.message}")
                        error(AuthError.API_FAILED_FETCH_RESULT)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    error(AuthError.NETWORK)
                }
            })
        }
    }

    fun authCheckSession(ok: () -> Unit, error: (code: AuthError) -> Unit) {
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""

        if (session.isEmpty()) {
            error("GENERIC/SESSION_NOT_FOUND")
        }

        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(AuthError.MISSING_DEVICE_ID)
                return@getDeviceId
            }

            apiService.checkSession(session, deviceId).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    try {
                        when(response.body()?.code) {
                            "OK" -> ok()
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(AuthError.fromCode(failed.code) ?: AuthError.NONE)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MedicalRecordAPI", "checkSession: ${e.message}")
                        error(AuthError.API_FAILED_FETCH_RESULT)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    error(AuthError.NETWORK)
                }
            })
        }

//        apiService.checkSession(session).enqueue(object : Callback<CheckSessionResponse> {
//            override fun onResponse(call: Call<CheckSessionResponse>, response: Response<CheckSessionResponse>) {
//                when(response.body()?.code) {
//                    "OK" -> ok()
//                    else -> {
//                        val failedBody = response.errorBody()?.string() ?: ""
//                        val failed = Gson().fromJson(failedBody, CheckSessionResponse::class.java)
//                        error(AuthError.valueOf(failed.code))
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<CheckSessionResponse>, t: Throwable) {
//                error(AuthError.NETWORK)
//            }
//        })
    }
}