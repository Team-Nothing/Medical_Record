package team.co2.medical_records.service.medical_record_api

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.co2.medical_records.service.device.DeviceInformation
import team.co2.medical_records.ui.screen.BluetoothDevice
import java.util.concurrent.TimeUnit


class MedicalRecordAPI(private val context: Context) {
    companion object {
        private const val SERVER_URL = "http://163.18.44.160:8001/"
    }

    private val apiService: ApiService
    private val deviceInformation: DeviceInformation = DeviceInformation(context)
    private var deviceId: String = ""
    private var session: String = ""


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
        apiService.authRegister(request).enqueue(object : Callback<GenericResponse> {
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

    fun authLogin(username: String, password: String, ok: (accountType: AccountType) -> Unit, error: (code: AuthError?) -> Unit) {
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
            this.deviceId = deviceId

            val request = LoginRequest(username.trim(), password.trim())
            apiService.authLogin(request, deviceId).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    try {
                        when(response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
                                if (data != null) {
                                    context.getSharedPreferences(
                                        "session", Context.MODE_PRIVATE
                                    ).edit().putString("session", "Bearer ${data.token}").apply()
                                    this@MedicalRecordAPI.session = data.token
                                    ok(AccountType.fromType(data.type))
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

    fun authCheckSession(ok: (accountType: AccountType) -> Unit, error: (code: AuthError?) -> Unit) {
        val session =
            context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "")
                ?: ""

        if (session.isEmpty()) {
            error(AuthError.SESSION_NOT_FOUND)
        }

        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(AuthError.MISSING_DEVICE_ID)
                return@getDeviceId
            }
            this.deviceId = deviceId

            apiService.authCheckSession(session, deviceId).enqueue(object : Callback<CheckSessionResponse> {
                override fun onResponse(
                    call: Call<CheckSessionResponse>,
                    response: Response<CheckSessionResponse>
                ) {
                    try {
                        when (response.body()?.code) {
                            "OK" -> {
                                this@MedicalRecordAPI.session = session
                                val data = response.body()?.data
                                Log.d("MedicalRecordAPI", "checkSession: ${data?.type}")
                                ok(AccountType.fromType(response.body()?.data?.type ?: ""))
                            }
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed =
                                    Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(AuthError.fromCode(failed.code))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MedicalRecordAPI", "checkSession: ${e.message}")
                        error(AuthError.API_FAILED_FETCH_RESULT)
                    }
                }

                override fun onFailure(call: Call<CheckSessionResponse>, t: Throwable) {
                    error(AuthError.NETWORK)
                }
            })
        }
    }

    fun authLogout(ok: () -> Unit, error: (code: AuthError?) -> Unit) {
        if (session.isEmpty()) {
            error(AuthError.SESSION_NOT_FOUND)
            return
        }
        if (deviceId.isEmpty()) {
            error(AuthError.MISSING_DEVICE_ID)
            return
        }

        apiService.authLogout(session, deviceId)
            .enqueue(object : Callback<GenericResponse> {
                override fun onResponse(
                    call: Call<GenericResponse>,
                    response: Response<GenericResponse>
                ) {
                    try {
                        when (response.body()?.code) {
                            "OK" -> {
                                context.getSharedPreferences("session", Context.MODE_PRIVATE)
                                    .edit().remove("session").apply()
                                context.getSharedPreferences("session", Context.MODE_PRIVATE)
                                    .edit().remove("device-register-id").apply()
                                session = ""
                                ok()
                            }
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed =
                                    Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(AuthError.fromCode(failed.code))
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

    fun deviceAdd(bluetoothDevice: BluetoothDevice?, ok: () -> Unit, error: (code: DeviceError?) -> Unit){
        if (session.isEmpty()) {
            error(DeviceError.SESSION_NOT_FOUND)
            return
        }
        if (deviceId.isEmpty()) {
            error(DeviceError.MISSING_DEVICE_ID)
            return
        }
        if (bluetoothDevice == null || bluetoothDevice.mac.isEmpty()) {
            error(DeviceError.MISSING_BLUETOOTH_MAC)
            return
        }

        context.getSharedPreferences(
            "bluetooth-scanner", Context.MODE_PRIVATE
        ).edit().putString("device-id", bluetoothDevice.deviceId.toString()).apply()
        context.getSharedPreferences(
            "bluetooth-scanner", Context.MODE_PRIVATE
        ).edit().putString("vendor-id", bluetoothDevice.vendorId.toString()).apply()

        deviceInformation.getIPAddress{ ipv4, ipv6 ->
            val request = DeviceAddRequest("1", bluetoothDevice.mac, ipv6, ipv4)
            apiService.deviceAdd(session, deviceId, request).enqueue(object : Callback<DeviceAddResponse> {
                override fun onResponse(call: Call<DeviceAddResponse>, response: Response<DeviceAddResponse>) {
                    try {
                        when(response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
                                if (data != null) {
                                    context.getSharedPreferences(
                                        "session", Context.MODE_PRIVATE
                                    ).edit().putString("device-register-id", data.device_register_id).apply()
                                    ok()
                                }
                            }
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(DeviceError.fromCode(failed.code))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MedicalRecordAPI", "deviceAdd: ${e.message}")
                        error(DeviceError.API_FAILED_FETCH_RESULT)
                    }
                }

                override fun onFailure(call: Call<DeviceAddResponse>, t: Throwable) {
                    error(DeviceError.NETWORK)
                }
            })
        }
    }

    fun bedDeviceAdd(bluetoothDevice: BluetoothDevice?, bedDevice: String, ok: () -> Unit, error: (code: DeviceError?) -> Unit) {
        if (bedDevice.trim().isEmpty()) {
            error(DeviceError.MISSING_FIELDS)
            return
        }
        deviceAdd(bluetoothDevice, {
            val deviceRegisterId =
                context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("device-register-id", "")
                    ?: ""
            apiService.bedDeviceLink(session, deviceId, deviceRegisterId, BedDeviceLinkRequest(bedDevice.trim())).enqueue(object : Callback<GenericResponse> {
                override fun onResponse(call: Call<GenericResponse>, response: Response<GenericResponse>) {
                    try {
                        when(response.body()?.code) {
                            "OK" -> ok()
                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed = Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(DeviceError.fromCode(failed.code))
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MedicalRecordAPI", "bedDeviceAdd: ${e.message}")
                        error(DeviceError.API_FAILED_FETCH_RESULT)
                    }
                }

                override fun onFailure(call: Call<GenericResponse>, t: Throwable) {
                    error(DeviceError.NETWORK)
                }
            })
        }, {
            error(it)
        })
    }
}