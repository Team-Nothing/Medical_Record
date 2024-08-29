package team.co2.medical_records
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import team.co2.medical_records.ui.theme.Medical_RecordsTheme
import androidx.compose.runtime.Composable

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI
import team.co2.medical_records.ui.screen.BedDeviceScreen
import team.co2.medical_records.ui.screen.HelloScreen
import team.co2.medical_records.ui.screen.LinkScreen
import team.co2.medical_records.ui.screen.LinkType
import team.co2.medical_records.ui.screen.LoginScreen
import team.co2.medical_records.ui.screen.RegisterScreen
import team.co2.medical_records.ui.screen.SelectTypeScreen


class MainActivity : ComponentActivity() {


    private lateinit var esp32SerialCommunicator: ESP32Communicator
    private lateinit var medicalRecordAPI: MedicalRecordAPI

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
        medicalRecordAPI = MedicalRecordAPI(this)
        setContent {
            Medical_RecordsTheme {
                MainNavHost(medicalRecordAPI)
            }
        }
    }

    @Composable
    fun MainNavHost(medicalRecordAPI: MedicalRecordAPI) {
        val mainNavController = rememberNavController()
        NavHost(mainNavController, startDestination = "hello") {
            composable("hello") {
                HelloScreen(mainNavController, medicalRecordAPI, LocalContext.current)
            }
            composable("register") {
                RegisterScreen(mainNavController, medicalRecordAPI, LocalContext.current)
            }
            composable("login") {
                LoginScreen(mainNavController, medicalRecordAPI, LocalContext.current)
            }
            composable("select-account-type") {
                SelectTypeScreen(mainNavController, medicalRecordAPI, LocalContext.current)
            }
            composable("link/{data}",
                arguments = listOf(navArgument("data") { type = NavType.StringType })
            ) {
                val type = it.arguments?.getString("data")
                Log.d("LINK_TYPE", type ?: "null")
                LinkScreen(mainNavController, medicalRecordAPI, LocalContext.current, LinkType.fromType(type ?: ""))
            }
            composable("bed-device") {
                BedDeviceScreen(mainNavController)
            }
        }
    }
}

