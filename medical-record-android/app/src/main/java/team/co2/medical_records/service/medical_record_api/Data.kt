package team.co2.medical_records.service.medical_record_api

import org.intellij.lang.annotations.Language


data class RegisterRequest(
    val username: String,
    val password: String,
)

data class LoginRequest(
    val username: String,
    val password: String,
    val session: String = "DEVICE"
)

data class DeviceAddRequest(
    val device_type_id: String,
    val bluetooth_mac: String,
    val ipv6: String,
    val ipv4: String
)

data class BedDeviceLinkRequest(
    val bed_id: String,
)

data class BedDeviceMedicalTranscriptRequest(
    val start_date: String? = null,
    val end_date: String? = null,
    val page: Int = 1,
    val order_by: String = "DESC",
    val item_per_page: Int = 1000
)

data class GenericResponse(
    val code: String,
    val message: String,
)

data class LoginResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val uid: String,
        val type: String,
        val token: String,
        val device_register_id: String?
    )
}

data class CheckSessionResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val type: String,
    )
}

data class DeviceAddResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val device_register_id: String
    )
}

data class BedDevicePatientReminders(
    val code: String,
    val message: String,
    val data: List<Data>
) {
    data class Data(
        val title: String,
        val finished: Boolean,
    )
}

data class BedDevicePatientRoutine(
    val code: String,
    val message: String,
    val data: List<Data>
) {
    data class Data(
        val time: String,
        val title: String,
        val description: String? = null,
        val finished: Boolean
    )
}

data class BedDeviceMedicalTranscriptResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val total_pages: Int,
        val items: List<Item>
    ) {
        data class Item(
            val id: Int,
            val datetime: String,
            val name: String?,
            val content: String,
        )
    }
}

data class BedDevicePatientInfoResponse(
    val code: String,
    val message: String,
    val data: Data
) {
    data class Data(
        val has_patient: Boolean,
        val bed: String?,
        val admission_days: Int?,
        val department: String?,
        val doctor: NameImage?,
        val resident: NameImage?,
        val nurse: NameImage?,
        val patient: Patient?,
        val tags: List<Tags>
    ) {
        data class NameImage(
            val name: String,
            val image_uid: String?
        )

        data class Patient(
            val name: String,
            val image_uid: String?,
            val language: String,
            val age: String,
            val gender: String,
            val blood: String,
            val feature_id: String
        )

        data class Tags(
            val title: String,
            val icon: String?,
            val description: String?
        )
    }
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