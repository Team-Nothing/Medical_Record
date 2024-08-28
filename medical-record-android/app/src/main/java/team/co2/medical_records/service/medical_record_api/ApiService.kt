package team.co2.medical_records.service.medical_record_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<GenericResponse>
}

interface LoginService {
    @POST("auth/login")
    @Headers("X-Device-ID" to session)
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}

interface CheckSessionService {
    @Headers("Authorization: Bearer $token")
    @POST("auth/check-session")
    fun getData(): Call<CheckSessionResponse>
}

interface RenewSessionService {
    @Headers("Authorization: Bearer $token")
    @POST("auth/renew-session")
    fun renewSession(@Body request: RenewSessionRequest): Call<RenewSessionResponse>
}

interface resetPasswordService {
    @POST("auth/reset-password")
    fun resetPassword(@Body request: resetPasswordRequest): Call<resetPasswordResponse>
}

interface authenticateService {
    @POST("auth/change-password")
    fun changePassword(@Body request: authenticateRequest): Call<changePasswordResponse>
}

