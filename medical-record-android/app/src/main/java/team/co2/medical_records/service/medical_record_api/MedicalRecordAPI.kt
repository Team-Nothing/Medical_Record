package team.co2.medical_records.service.medical_record_api

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import team.co2.medical_records.ui.screen.LeftRight
import team.co2.medical_records.ui.screen.Message
import team.co2.medical_records.Reminder
import team.co2.medical_records.Task
import team.co2.medical_records.service.device.DeviceInformation
import team.co2.medical_records.ui.screen.BluetoothDevice
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
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .serializeNulls()
                        .create()
                )
            )
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
                                    if (data.device_register_id != null) {
                                        context.getSharedPreferences(
                                            "session", Context.MODE_PRIVATE
                                        ).edit().putString(
                                            "device-register-id",
                                            data.device_register_id
                                        ).apply()
                                    }
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
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        if (session.isEmpty()) {
            error(AuthError.SESSION_NOT_FOUND)
        }

        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(AuthError.MISSING_DEVICE_ID)
                return@getDeviceId
            }

            apiService.authCheckSession(session, deviceId).enqueue(object : Callback<CheckSessionResponse> {
                override fun onResponse(
                    call: Call<CheckSessionResponse>,
                    response: Response<CheckSessionResponse>
                ) {
                    try {
                        when (response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
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
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""

        if (session.isEmpty()) {
            error(AuthError.SESSION_NOT_FOUND)
            return
        }
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(AuthError.MISSING_DEVICE_ID)
                return@getDeviceId
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
    }

    fun deviceAdd(bluetoothDevice: BluetoothDevice?, ok: () -> Unit, error: (code: DeviceError?) -> Unit){
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        if (session.isEmpty()) {
            error(DeviceError.SESSION_NOT_FOUND)
            return
        }
        if (bluetoothDevice == null || bluetoothDevice.mac.isEmpty()) {
            error(DeviceError.MISSING_BLUETOOTH_MAC)
            return
        }
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(DeviceError.MISSING_DEVICE_ID)
                return@getDeviceId
            }

            deviceInformation.getIPAddress { ipv4, ipv6 ->
                val request = DeviceAddRequest("1", bluetoothDevice.mac, ipv6, ipv4)
                apiService.deviceAdd(session, deviceId, request)
                    .enqueue(object : Callback<DeviceAddResponse> {
                        override fun onResponse(
                            call: Call<DeviceAddResponse>,
                            response: Response<DeviceAddResponse>
                        ) {
                            try {
                                when (response.body()?.code) {
                                    "OK" -> {
                                        val data = response.body()?.data
                                        if (data != null) {
                                            context.getSharedPreferences(
                                                "session", Context.MODE_PRIVATE
                                            ).edit().putString(
                                                "device-register-id",
                                                data.device_register_id
                                            ).apply()
                                            ok()
                                        }
                                    }

                                    else -> {
                                        val failedBody = response.errorBody()?.string() ?: ""
                                        val failed =
                                            Gson().fromJson(failedBody, GenericResponse::class.java)
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
            val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "")
                ?: ""
            deviceInformation.getDeviceId { deviceId ->
                if (deviceId == null) {
                    error(DeviceError.MISSING_DEVICE_ID)
                    return@getDeviceId
                }
                apiService.bedDeviceLink(
                    session,
                    deviceId,
                    deviceRegisterId,
                    BedDeviceLinkRequest(bedDevice.trim())
                ).enqueue(object : Callback<GenericResponse> {
                    override fun onResponse(
                        call: Call<GenericResponse>,
                        response: Response<GenericResponse>
                    ) {
                        try {
                            when (response.body()?.code) {
                                "OK" -> ok()
                                else -> {
                                    val failedBody = response.errorBody()?.string() ?: ""
                                    val failed =
                                        Gson().fromJson(failedBody, GenericResponse::class.java)
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
            }
        }, {
            error(it)
        })
    }

    fun bedDeviceGetReminder(ok: (reminders: List<Reminder>) -> Unit, error: (code: DeviceError?) -> Unit) {
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        val deviceRegisterId = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("device-register-id", "") ?: ""
        if (deviceRegisterId.isEmpty()) {
            error(DeviceError.MISSING_FIELDS)
            return
        }
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(DeviceError.MISSING_DEVICE_ID)
                return@getDeviceId
            }
            apiService.bedDeviceGetPatientReminders(session, deviceId, deviceRegisterId)
                .enqueue(object : Callback<BedDevicePatientReminders> {
                    override fun onResponse(
                        call: Call<BedDevicePatientReminders>,
                        response: Response<BedDevicePatientReminders>
                    ) {
                        try {
                            when (response.body()?.code) {
                                "OK" -> {
                                    val data = response.body()?.data
                                    if (data != null) {
                                        ok(data.map { reminder ->
                                            Reminder(reminder.title, reminder.finished)
                                        })
                                    }
                                }

                                else -> {
                                    val failedBody = response.errorBody()?.string() ?: ""
                                    val failed =
                                        Gson().fromJson(failedBody, GenericResponse::class.java)
                                    error(DeviceError.fromCode(failed.code))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MedicalRecordAPI", "bedDeviceGetReminder: ${e.message}")
                            error(DeviceError.API_FAILED_FETCH_RESULT)
                        }
                    }

                    override fun onFailure(call: Call<BedDevicePatientReminders>, t: Throwable) {
                        error(DeviceError.NETWORK)
                    }
                })
        }
    }

    fun bedDeviceGetRoutine(ok: (reminders: List<Task>) -> Unit, error: (code: DeviceError?) -> Unit, filter: FilterType = FilterType.CURRENT) {
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        val deviceRegisterId = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("device-register-id", "") ?: ""

        if (deviceRegisterId.isEmpty()) {
            error(DeviceError.MISSING_FIELDS)
            return
        }
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(DeviceError.MISSING_DEVICE_ID)
                return@getDeviceId
            }
            apiService.bedDeviceGetPatientRoutine(session, deviceId, deviceRegisterId, filter.type)
                .enqueue(object : Callback<BedDevicePatientRoutine> {
                    override fun onResponse(
                        call: Call<BedDevicePatientRoutine>,
                        response: Response<BedDevicePatientRoutine>
                    ) {

                        try {
                            when (response.body()?.code) {
                                "OK" -> {
                                    val data = response.body()?.data
                                    if (data != null) {
                                        ok(data.map { routine ->
                                            Task(
                                                routine.time
                                                    .replace("T", " ")
                                                    .replace("-", "/"),
                                                routine.title, routine.description, routine.finished
                                            )
                                        })
                                    }
                                }

                                else -> {
                                    val failedBody = response.errorBody()?.string() ?: ""
                                    Log.e("MedicalRecordAPI", failedBody)
                                    val failed =
                                        Gson().fromJson(failedBody, GenericResponse::class.java)
                                    error(DeviceError.fromCode(failed.code))
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MedicalRecordAPI", "bedDeviceGetReminder: ${e.message}")
                            error(DeviceError.API_FAILED_FETCH_RESULT)
                        }
                    }

                    override fun onFailure(call: Call<BedDevicePatientRoutine>, t: Throwable) {
                        error(DeviceError.NETWORK)
                    }
                })
        }
    }

    fun bedDeviceGetMedicalTranscript(ok: (totalPages: Int, messages: List<Message>) -> Unit, error: (code: DeviceError?) -> Unit, page: Int = 1) {
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        val deviceRegisterId = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("device-register-id", "") ?: ""

        if (deviceRegisterId.isEmpty()) {
            error(DeviceError.MISSING_FIELDS)
            return
        }
        val request = BedDeviceMedicalTranscriptRequest(page = page)
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(DeviceError.MISSING_DEVICE_ID)
                return@getDeviceId
            }
            apiService.bedDeviceGetMedicalTranscript(session, deviceId, deviceRegisterId, request)
                .enqueue(object : Callback<BedDeviceMedicalTranscriptResponse> {
                    override fun onResponse(
                        call: Call<BedDeviceMedicalTranscriptResponse>,
                        response: Response<BedDeviceMedicalTranscriptResponse>
                    ) {
                        when (response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
                                if (data != null) {
                                    ok(data.total_pages, data.items.map { item ->
                                        Message(
                                            item.id,
                                            item.datetime,
                                            if (item.name == null || item.name.startsWith("病人")) LeftRight.RIGHT else LeftRight.LEFT,
                                            item.name ?: "未知者",
                                            item.content
                                        )
                                    })
                                }
                            }

                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed =
                                    Gson().fromJson(failedBody, GenericResponse::class.java)
                                Log.e("MedicalRecordAPI", failed.message)
                                error(DeviceError.fromCode(failed.code))
                            }
                        }
                        try {

                        } catch (e: Exception) {
                            Log.e("MedicalRecordAPI", "bedDeviceGetMedicalTranscript: ${e.message}")
                            error(DeviceError.API_FAILED_FETCH_RESULT)
                        }
                    }

                    override fun onFailure(
                        call: Call<BedDeviceMedicalTranscriptResponse>,
                        t: Throwable
                    ) {
                        error(DeviceError.NETWORK)
                    }
                })
        }
    }

    fun bedDevicePatientInfo(ok: (info: BedDevicePatientInfoResponse.Data) -> Unit, error: (code: DeviceError?) -> Unit) {
        val session = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("session", "") ?: ""
        val deviceRegisterId = context.getSharedPreferences("session", Context.MODE_PRIVATE).getString("device-register-id", "") ?: ""

        if (deviceRegisterId.isEmpty()) {
            error(DeviceError.MISSING_FIELDS)
            return
        }
        deviceInformation.getDeviceId { deviceId ->
            if (deviceId == null) {
                error(DeviceError.MISSING_DEVICE_ID)
                return@getDeviceId
            }
            apiService.bedDevicePatientInfo(session, deviceId, deviceRegisterId)
                .enqueue(object : Callback<BedDevicePatientInfoResponse> {
                    override fun onResponse(
                        call: Call<BedDevicePatientInfoResponse>,
                        response: Response<BedDevicePatientInfoResponse>
                    ) {
                        when (response.body()?.code) {
                            "OK" -> {
                                val data = response.body()?.data
                                if (data != null) {
                                    ok(data)
                                }
                            }

                            else -> {
                                val failedBody = response.errorBody()?.string() ?: ""
                                val failed =
                                    Gson().fromJson(failedBody, GenericResponse::class.java)
                                error(DeviceError.fromCode(failed.code))
                            }
                        }
                    }

                    override fun onFailure(call: Call<BedDevicePatientInfoResponse>, t: Throwable) {
                        error(DeviceError.NETWORK)
                    }
                })
        }
    }

}