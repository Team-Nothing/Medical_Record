package team.co2.medical_records.service.bluetooth

import android.content.Context
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import com.google.gson.Gson
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ESP32Communicator(
    private val usbManager: UsbManager,
) {
    private var _isRunning = false
    private var deviceConnection: UsbDeviceConnection? = null
    private var usbSerialPort: UsbSerialPort? = null

    companion object {
        val ACTIONS = listOf("bluetooth")
    }

    val isRunning: Boolean
        get() = _isRunning

    fun getAllUsbDevices(): List<UsbSerialDriver> {
        val usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        return usbSerialDrivers
    }

    fun setDeviceConnection(usbSerialDriver: UsbSerialDriver): ESP32Communicator {
        if (_isRunning) {
            return this
        }
        this.usbSerialPort = usbSerialDriver.ports[0]
        this.deviceConnection = usbManager.openDevice(usbSerialDriver.device)

        return this
    }

    fun startReading(onDataReceived: (BluetoothResponse) -> Unit) {
        if (deviceConnection == null || usbSerialPort == null) {
            throw IllegalStateException("Device connection or USB serial port is not set, please run setDeviceConnection() first")
        }
        if (_isRunning) {
            return
        }
        _isRunning = true

        CoroutineScope(Dispatchers.IO).launch {
            try{
                usbSerialPort!!.open(deviceConnection)
                usbSerialPort!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                val buffer = ByteArray(1024)
                var totalData = ""
                var nextAction: ActionResponse? = null
                while (_isRunning) {
                    val numBytesRead = usbSerialPort!!.read(buffer, 1000)
                    if (numBytesRead > 0) {
                        val receivedData = String(buffer, 0, numBytesRead)
                        try {
                            when (nextAction?.data) {
                                "bluetooth" -> {
                                    totalData += receivedData
                                    Log.d("ESP32Communicator", receivedData)
                                    Log.d("ESP32Communicator", "data: ${totalData.length}/${nextAction.length}")
                                    if (totalData.length >= nextAction.length) {
                                        val bluetoothData =
                                            Gson().fromJson(receivedData, BluetoothResponse::class.java)
                                        withContext(Dispatchers.Main) {
                                            onDataReceived(bluetoothData)
                                        }
                                        totalData = ""
                                        nextAction = null
                                    }
                                }
                                else -> {
                                    val actionData = Gson().fromJson(receivedData, ActionResponse::class.java)
                                    if (actionData.data in ACTIONS) {
                                        nextAction = actionData
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            nextAction = null
                            totalData = ""
                        }

                    }
                }
            } catch (e: Exception) {
                Log.e("ESP32Communicator", "startReading: ${e.message}")
                Log.e("ESP32Communicator", "startReading: ${e.stackTraceToString()}")
            }
        }
    }

    fun stopReading() {
        this.usbSerialPort?.close()
        this.deviceConnection?.close()
        _isRunning = false
    }

    fun saveData(device: UsbSerialDriver, context: Context) {
        context.getSharedPreferences(
            "bluetooth-scanner", Context.MODE_PRIVATE
        ).edit().putInt("device-id", device.device.deviceId).putInt("vendor-id", device.device.vendorId).apply()
    }

    fun findDevice(context: Context, usbDevices: List<UsbSerialDriver>): Int {
        val sharedPreferences = context.getSharedPreferences(
            "bluetooth-scanner", Context.MODE_PRIVATE
        )
        val deviceId = sharedPreferences.getInt("device-id", -1)
        val vendorId = sharedPreferences.getInt("vendor-id", -1)

        usbDevices.forEach{
            if (it.device.deviceId == deviceId && it.device.vendorId == vendorId) {
                return usbDevices.indexOf(it)
            }
        }
        return -1
    }
}
