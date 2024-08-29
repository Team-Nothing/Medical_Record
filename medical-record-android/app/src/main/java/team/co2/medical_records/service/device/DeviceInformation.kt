package team.co2.medical_records.service.device

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings.Secure
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

class DeviceInformation(private val context: Context) {

    fun getDeviceId(deviceId: (deviceId: String?) -> Unit) {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            val device = AdvertisingIdClient.getAdvertisingIdInfo(context)
            deviceId(device.id)
        }
    }

    fun getBluetoothMac(): String {
        return Secure.getString(context.contentResolver, "bluetooth_address")
    }

    fun getIpv4(): String {
        return Secure.getString(context.contentResolver, "wifi_ipv4")
    }

    fun getIpv6(): String {
        return Secure.getString(context.contentResolver, "wifi_ipv6")
    }

    fun getIPAddress(ip: (ipv4: String, ipv6: String) -> Unit) {
        var ipv4 = ""
        var ipv6 = ""
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addresses: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (address in addresses) {
                    if (!address.isLoopbackAddress) {
                        Log.d("IP Address", address.hostAddress ?: "")
                        if (address.hostAddress!!.split(".").size == 4 && address.hostAddress!!.startsWith("192.168") && ipv4.isEmpty()) {
                            ipv4 = address.hostAddress!!
                        } else if (address.hostAddress!!.split("%")[1] == "wlan0" && ipv6.isEmpty()) {
                            ipv6 = address.hostAddress!!.split("%")[0]
                        }
                    }
//                    if (ipv4.isNotEmpty() && ipv6.isNotEmpty()) {
//                        ip(ipv4, ipv6)
//                        return
//                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("IP Address", "Unable to get IP address", ex)
            ip(ipv4, ipv6)
        }
    }

    fun getBluetoothMacAddress(): String {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val bluetoothMacAddress: String? = bluetoothAdapter?.address

        return bluetoothMacAddress ?: ""
    }


//    @Composable
//    fun CheckBluetoothPermission(result: (isGranted: Boolean) -> Unit) {
//        val permissionLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission(),
//            onResult = { isGranted ->
//                result(isGranted)
//            }
//        )
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            if (ContextCompat.checkSelfPermission(
//                context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
//            ) {
//                result(true)
//            } else {
//                LaunchedEffect(Unit) {
//                    permissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
//                }
//            }
//        } else {
//            result(true)
//        }
//    }
}