package team.co2.medical_records.service.device

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceInformation(private val context: Context) {

    fun getDeviceId(deviceId: (deviceId: String?) -> Unit) {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            val device = AdvertisingIdClient.getAdvertisingIdInfo(context)
            deviceId(device.id)
        }
    }
}