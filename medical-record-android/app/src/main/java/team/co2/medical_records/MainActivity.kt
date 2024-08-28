package team.co2.medical_records
import SettingScreen
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import team.co2.medical_records.ui.theme.Medical_RecordsTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.* // 用于布局相关的组件
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI


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

                val items= listOf(
                    NavigationItem(
                        title = "首頁",
                        selectedIcon = painterResource(R.drawable.baseline_home_24),
                        unselectedIcon = painterResource(R.drawable.baseline_home_24),
                        hasNews = false,

                        ),
                    NavigationItem(
                        title = "待辦事項",
                        selectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
                        unselectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
                        hasNews = false,
                        badgeCount = 3   ,
                    ),
                    NavigationItem(
                        title = "交談紀錄",
                        selectedIcon = painterResource(R.drawable.baseline_radio_24),
                        unselectedIcon = painterResource(R.drawable.baseline_radio_24),
                        hasNews = true,
                    ),
                    NavigationItem(
                        title = "行程表",
                        selectedIcon = painterResource(R.drawable.baseline_today_24),
                        unselectedIcon = painterResource(R.drawable.baseline_today_24),
                        hasNews = true,
                    ),
                    NavigationItem(
                        title = "其他",
                        selectedIcon = painterResource(R.drawable.baseline_dashboard_24),
                        unselectedIcon = painterResource(R.drawable.baseline_dashboard_24),
                        hasNews = false,
                    ),
                    NavigationItem(
                        title = "偏好設置",
                        selectedIcon = painterResource(R.drawable.baseline_settings_24),
                        unselectedIcon = painterResource(R.drawable.baseline_settings_24),
                        hasNews = false,
                    ),
                )
                var SelectItemIndex by rememberSaveable { mutableStateOf(0) }

               Row( modifier = Modifier
                   .fillMaxWidth(),
                   horizontalArrangement = Arrangement.Start // 确保子项靠左对齐
                ){
                    NavigationSideBar(
                        item = items,
                        selectedItemIndex = SelectItemIndex,//選哪個畫面
                        onNavigate = { SelectItemIndex = it }
                    )
                    //聊天紀錄
                    val messages = listOf(
                        Message(1,"LED", Role.DOCTOR, "汪汪先生好，昨晚睡得如何", "2024-07-28 10:00", true),
                        Message(2,"汪O安", Role.PATIENT, "我要找電燈泡，不是LED", "2024-07-28 10:00", false),
                        Message(3,"一隻魚", Role.NURSE, "汪先生，主治醫師在問妳昨晚睡得好嗎", "2024-07-28 10:30", true)
                    )

                    //代辦事項
                    val reminders = listOf(
                        Reminder("晚上12點後禁止飲食，包含飲水"),
                        Reminder("貴重物品簽收單"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同"),
                        Reminder("告知家人或朋友手術時間和地點，以便陪同")
                    )
                    val tasks = listOf(
                        Task("07:00", "吃降血壓藥"),
                        Task("08:00", "手術及麻醉同意書"),
                        Task("08:05", "手術部位註記"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人"),
                        Task("08:15", "等待進入手術室，並通知主要聯絡人")

                    )

//                   PatientCard()
//                   val views = listOf(PatientCard(),ChatScreen(messages))
//                   if (SelectItemIndex in views.indices) {
//                       views[SelectItemIndex]
//
//                   }
                    when(SelectItemIndex){
                        0 -> PatientCard()
                        1 -> MainContent(reminders,tasks)
                        2 -> ChatScreen(messages)
                        5 -> SettingScreen()
                    }

                }



            }
        }
    }
}

