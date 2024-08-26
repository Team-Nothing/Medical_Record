package team.co2.medical_records.service.medical_record_api

// 註冊 -> 使用者傳入
data class RegisterRequest(
    val username: String,
    val password: String,
)

// 註冊 -> 伺服器回傳
data class GenericResponse(
    val code: String,
    val message: String,
)
