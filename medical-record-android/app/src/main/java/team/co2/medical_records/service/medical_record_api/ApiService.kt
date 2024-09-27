package team.co2.medical_records.service.medical_record_api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @GET("/")
    fun status(): Call<GenericResponse>

    @POST("auth/register")
    fun authRegister(@Body request: RegisterRequest): Call<GenericResponse>

    @POST("auth/login")
    fun authLogin(
        @Body request: LoginRequest,
        @Header("X-Device-ID") deviceID: String
    ): Call<LoginResponse>

    @POST("auth/check-session")
    fun authCheckSession(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String
    ): Call<CheckSessionResponse>

    @POST("auth/logout")
    fun authLogout(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String
    ): Call<GenericResponse>

    @POST("device/add")
    fun deviceAdd(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Body request: DeviceAddRequest
    ): Call<DeviceAddResponse>

    @POST("bed-device/link")
    fun bedDeviceLink(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
        @Body request: BedDeviceLinkRequest
    ): Call<GenericResponse>

    @GET("bed-device/patient-reminders")
    fun bedDeviceGetPatientReminders(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
    ): Call<BedDevicePatientReminders>

    @GET("bed-device/patient-routine/{filterType}")
    fun bedDeviceGetPatientRoutine(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
        @Path("filterType") filterType: String
    ): Call<BedDevicePatientRoutine>

    @POST("bed-device/medical-transcript")
    fun bedDeviceGetMedicalTranscript(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
        @Body request: BedDeviceMedicalTranscriptRequest
    ): Call<BedDeviceMedicalTranscriptResponse>

    @GET("bed-device/patient-info")
    fun bedDevicePatientInfo(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
    ): Call<BedDevicePatientInfoResponse>

    @Multipart
    @POST("transcript/bed-audio-upload")
    fun transcriptBedAudioUpload(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String,
        @Header("Device-Register-ID") deviceRegisterID: String,
        @Part file: MultipartBody.Part,
        @Part("data") data: RequestBody
    ): Call<TranscriptBedAudioUploadResponse>
}
