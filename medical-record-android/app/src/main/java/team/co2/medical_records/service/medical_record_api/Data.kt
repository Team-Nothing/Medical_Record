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

// 登入 -> 使用者傳入
data class LoginRequest(
    val username: String,
    val password: String,
    val session: String,
)

// 登入 -> 伺服器回傳
data class LoginResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val uid: String,
        val token: String
    )
}

//chehk-session -> 伺服器回傳
data class CheckSessionResponse(
    val code: String,
    val message: String,
)

//renew-session -> 使用者傳入
data class RenewSessionRequest(
    val seesion: String,
)

data class RenewSessionResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val token: String
    )
}
