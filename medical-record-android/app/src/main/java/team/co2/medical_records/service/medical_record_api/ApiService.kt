package team.co2.medical_records.service.medical_record_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface ApiService {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<GenericResponse>

    @POST("auth/login")
    fun login(@Header("X-Device-ID") deviceId: String, @Body request: LoginRequest): Call<LoginResponse>

    @POST("auth/check-session")
    fun checkSession(@Header("Authorization") token: String): Call<CheckSessionResponse>

    @POST("auth/renew-session")
    fun renewSession(@Header("Authorization") token: String, @Body request: RenewSessionRequest): Call<RenewSessionResponse>

}