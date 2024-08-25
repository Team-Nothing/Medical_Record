package team.co2.medical_records.service.medical_record_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/register")
    fun register(@Body request: RegisterRequest): Call<GenericResponse>
}