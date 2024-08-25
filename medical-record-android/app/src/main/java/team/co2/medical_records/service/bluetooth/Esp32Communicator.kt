package team.co2.medical_records.service.bluetooth

import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
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
    private var isRunning = false
    private var deviceConnection: UsbDeviceConnection? = null
    private var usbSerialPort: UsbSerialPort? = null

    fun getAllUsbDevices(): List<UsbSerialDriver> {
        val usbSerialDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager)

        return usbSerialDrivers
    }

    fun setDeviceConnection(usbSerialDriver: UsbSerialDriver): ESP32Communicator {
        this.deviceConnection = usbManager.openDevice(usbSerialDriver.device)
        this.usbSerialPort = usbSerialDriver.ports?.get(0)
        return this
    }

    fun startReading(onDataReceived: (String) -> Unit) {
        if (deviceConnection == null || usbSerialPort == null) {
            throw IllegalStateException("Device connection or USB serial port is not set, please run setDeviceConnection() first")
        }

        isRunning = true
        CoroutineScope(Dispatchers.IO).launch {
            val buffer = ByteArray(1024)
            while (isRunning) {
                val numBytesRead = usbSerialPort!!.read(buffer, 1000)
                if (numBytesRead > 0) {
                    val receivedData = String(buffer, 0, numBytesRead)
                    withContext(Dispatchers.Main) {
                        onDataReceived(receivedData)
                    }
                }
            }
        }
    }

    fun stopReading() {
        isRunning = false
    }
}