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
    NONE("OK");

    companion object {
        fun fromCode(code: String): AuthError? {
            return entries.find { it.code == code }
        }
    }
}

