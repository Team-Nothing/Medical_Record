package team.co2.medical_records.service.bluetooth

data class BluetoothResponse(
    val MAC: String,
    val nearby_devices: List<Data>
) {
    data class Data(
        val rssi: Int,
        val MAC: String
    )
}