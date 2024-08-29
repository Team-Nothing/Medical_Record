package team.co2.medical_records.service.medical_record_api

enum class GenericError(val code: String) {
    NETWORK("GENERIC/NETWORK"),
    API_FAILED_FETCH_RESULT("GENERIC/API-FAILED-FETCH-RESULT");

    companion object {
        fun fromCode(code: String): GenericError? {
            return entries.find { it.code == code }
        }
    }
}

enum class AuthError(val code: String) {
    NETWORK("GENERIC/NETWORK"),
    API_FAILED_FETCH_RESULT("GENERIC/API-FAILED-FETCH-RESULT"),
    INTERNAL_SERVER_ERROR("INTERNAL-SERVER-ERROR"),
    MISSING_FIELDS("GENERIC/MISSING-FIELDS"),
    SESSION_NOT_FOUND("AUTH/SESSION-NOT-FOUND"),
    USERNAME_IS_EMPTY("AUTH/USERNAME-IS-EMPTY"),
    PASSWORD_IS_EMPTY("AUTH/PASSWORD-IS-EMPTY"),
    CONFIRM_PASSWORD_IS_EMPTY("AUTH/CONFIRM_PASSWORD_IS_EMPTY"),
    PASSWORD_MISMATCH("AUTH/PASSWORD-MISMATCH"),
    USER_EXISTS("AUTH/USER-EXISTS"),
    INVALID_SESSION("AUTH/INVALID-SESSION"),
    MISSING_DEVICE_ID("AUTH/MISSING-DEVICE-ID"),
    USER_NOT_FOUND("AUTH/USER-NOT-FOUND"),
    SESSION_EXPIRED("AUTH/SESSION-EXPIRED"),
    NONE("OK");

    companion object {
        fun fromCode(code: String): AuthError? {
            return entries.find { it.code == code }
        }
    }
}

enum class DeviceError(val code: String) {
    NETWORK("GENERIC/NETWORK"),
    API_FAILED_FETCH_RESULT("GENERIC/API-FAILED-FETCH-RESULT"),
    MISSING_FIELDS("GENERIC/MISSING-FIELDS"),
    INTERNAL_SERVER_ERROR("INTERNAL-SERVER-ERROR"),
    SESSION_NOT_FOUND("AUTH/SESSION-NOT-FOUND"),
    INVALID_SESSION("AUTH/INVALID-SESSION"),
    MISSING_DEVICE_ID("AUTH/MISSING-DEVICE-ID"),
    SESSION_EXPIRED("AUTH/SESSION-EXPIRED"),
    MISSING_BED_ID("DEVICE/MISSING-BED-ID"),
    MISSING_BLUETOOTH_MAC("DEVICE/MISSING-BLUETOOTH-MAC"), ;

    companion object {
        fun fromCode(code: String): DeviceError? {
            return entries.find { it.code == code }
        }
    }
}

enum class AccountType(val type: String) {
    DOCTOR("DOCTOR"),
    NURSE("NURSE"),
    BED_DEVICE("BED-DEVICE"),
    NONE("NONE");

    companion object {
        fun fromType(type: String): AccountType {
            return entries.find { it.type == type } ?: NONE
        }
    }
}

