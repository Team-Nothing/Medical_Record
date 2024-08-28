package team.co2.medical_records.service.medical_record_api


data class GenericResponse(
    val code: String,
    val message: String,
)

data class RegisterRequest(
    val username: String,
    val password: String,
)

data class LoginRequest(
    val username: String,
    val password: String,
    val session: String = "DEVICE"
)

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


//
//// 登入 -> 使用者傳入
//data class LoginRequest(
//    val username: String,
//    val password: String,
//    val session: String,
//)
//
//// 登入 -> 伺服器回傳
//data class LoginResponse(
//    val code: String,
//    val message: String,
//    val data: Data
//) {
//    data class Data(
//        val uid: String,
//        val token: String
//    )
//}
//
////chehk-session -> 使用者傳入
//suspend fun checkSession(): CheckSessionResponse {
//    // server 回傳 CheckSessionResponse
//    return check1Session().execute().body()!!
//}
//
////chehk-session -> 伺服器回傳
//data class CheckSessionResponse(
//    val code: String,
//    val message: String,
//)
//
////renew-session -> 使用者傳入
//data class RenewSessionRequest(
//    val seesion: String,
//)
//
//data class RenewSessionResponse(
//    val code: String,
//    val message: String,
//    val data: Data
//) {
//    data class Data(
//        val token: String
//    )
//}
//
//data class resetPasswordRequest(
//    val password: String,
//    val new_password: String,
//)
//
//data class resetPasswordResponse(
//    val code: String,
//    val message: String,
//)
//
//data class authenticateRequest(
//    val password: String
//)
//
//data class changePasswordResponse(
//    val code: String,
//    val message: String,
//    val data: Data
//) {
//    data class Data(
//        val token: String
//    )
//}