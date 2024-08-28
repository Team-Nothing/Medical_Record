package team.co2.medical_records.service.medical_record_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @GET("/")
    fun status(): Call<GenericResponse>

    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<GenericResponse>

    @POST("auth/login")
    fun login(
        @Body request: LoginRequest,
        @Header("X-Device-ID") deviceID: String
    ): Call<LoginResponse>

    @POST("auth/check-session")
    fun checkSession(
        @Header("Authorization") authToken: String,
        @Header("X-Device-ID") deviceID: String
    ): Call<GenericResponse>
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

