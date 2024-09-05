package team.co2.medical_records.service.medical_record_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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
}

//interface LoginService {
//    @POST("auth/login")
//    @Headers("X-Device-ID" to session)
//    fun login(@Body request: LoginRequest): Call<LoginResponse>
//}
//
//interface CheckSessionService {
//    @Headers("Authorization: Bearer $token")
//    @POST("auth/check-session")
//    fun getData(): Call<CheckSessionResponse>
//}
//
//interface RenewSessionService {
//    @Headers("Authorization: Bearer $token")
//    @POST("auth/renew-session")
//    fun renewSession(@Body request: RenewSessionRequest): Call<RenewSessionResponse>
//}
//
//interface resetPasswordService {
//    @POST("auth/reset-password")
//    fun resetPassword(@Body request: resetPasswordRequest): Call<resetPasswordResponse>
//}
//
//interface authenticateService {
//    @POST("auth/change-password")
//    fun changePassword(@Body request: authenticateRequest): Call<changePasswordResponse>
//}

