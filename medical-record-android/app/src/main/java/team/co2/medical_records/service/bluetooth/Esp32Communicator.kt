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
        if (isRunning) {
            return this
        }
        this.usbSerialPort = usbSerialDriver.ports[0]
        this.deviceConnection = usbManager.openDevice(usbSerialDriver.device)

        return this
    }

    fun startReading(onDataReceived: (String) -> Unit) {
        if (deviceConnection == null || usbSerialPort == null) {
            throw IllegalStateException("Device connection or USB serial port is not set, please run setDeviceConnection() first")
        }
        if (isRunning) {
            return
        }
        isRunning = true

        CoroutineScope(Dispatchers.IO).launch {
            try{
                usbSerialPort!!.open(deviceConnection)
                usbSerialPort!!.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
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
            } catch (e: Exception) {
                //
            }
        }
    }

    fun stopReading() {
        this.usbSerialPort?.close()
        this.deviceConnection?.close()
        isRunning = false
    }
}
