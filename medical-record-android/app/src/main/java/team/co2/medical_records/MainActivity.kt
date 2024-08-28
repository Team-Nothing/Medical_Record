package team.co2.medical_records
import SettingScreen
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import team.co2.medical_records.ui.theme.Medical_RecordsTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.* // 用于布局相关的组件
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI
import team.co2.medical_records.ui.screen.BedDeviceScreen
import team.co2.medical_records.ui.screen.HelloScreen


class MainActivity : ComponentActivity() {


    private lateinit var esp32SerialCommunicator: ESP32Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        val usbManager = getSystemService(USB_SERVICE) as UsbManager
//        esp32SerialCommunicator = ESP32Communicator(usbManager)
//        val deviceList = esp32SerialCommunicator.getAllUsbDevices()
//        Log.d("USB_DEVICE_LIST", "Detected devices: ${deviceList.size}")
//        deviceList.forEach { driver ->
//            Log.d("USB_DEVICE", driver.device.deviceName)
//        }
//        esp32SerialCommunicator.setDeviceConnection(deviceList[0]).startReading { data ->
//            Log.d("USB_DEVICE", data)
//        }
//
//        val medicalRecordAPI = MedicalRecordAPI()
//        medicalRecordAPI.register("username", "password", {
//            Log.d("XDDREGISTER", "OK")
//            // make toast
//            Toast.makeText(this, "註冊成功", Toast.LENGTH_SHORT).show()
//        }, { code ->
//            Toast.makeText(this, "註冊失敗: $code", Toast.LENGTH_SHORT).show()
//            Log.e("XDDREGISTER", "Error: $code")
//        })

        setContent {
            Medical_RecordsTheme {
//                RegisterScreen()
//                LoginScreen()

                MainNavHost()
            }
        }
    }

    @Composable
    fun MainNavHost() {
        val mainNavController = rememberNavController()
        NavHost(mainNavController, startDestination = "hello") {
            composable("hello") {
                HelloScreen(mainNavController)
            }
            composable("register") {
                RegisterScreen()
            }
            composable("login") {
                LoginScreen()
            }
            composable("bed-device") {
                BedDeviceScreen(mainNavController)
            }
        }
    }
}

