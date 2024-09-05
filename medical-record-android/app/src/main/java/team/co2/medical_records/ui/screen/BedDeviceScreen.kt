package team.co2.medical_records.ui.screen

import SettingScreen
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import team.co2.medical_records.ui.layout.NavigationItem
import team.co2.medical_records.ui.layout.NavigationSideBar
import team.co2.medical_records.R
import team.co2.medical_records.service.audio.AudioRecordingService
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.BedDevicePatientInfoResponse
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI
import team.co2.medical_records.ui.layout.NotImplementedScreen

@Composable
fun BedDeviceScreen(
    mainNavController: NavHostController,
    medicalRecordAPI: MedicalRecordAPI,
    context: Context
) {
    var reminders by remember { mutableStateOf(emptyList<Reminder>()) }
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    var patientInfo: BedDevicePatientInfoResponse.Data? by remember { mutableStateOf(null) }
    var messages by remember { mutableStateOf(emptyList<Message>()) }
    var isRecording by remember { mutableStateOf(false) }
    var esp32SerialCommunicator: ESP32Communicator? = null
    val usbManager = getSystemService(context, UsbManager::class.java)
    if (usbManager != null) {
        esp32SerialCommunicator = ESP32Communicator(usbManager)
    }

    val recordForegroundService = Intent(context, AudioRecordingService::class.java)
    isRecording = LocalContext.current.getSharedPreferences("bed-device", Context.MODE_PRIVATE).getBoolean("isRecording", false)

    if (isRecording) {
        ContextCompat.startForegroundService(context, recordForegroundService)
    } else {
        context.stopService(recordForegroundService)
    }

    LaunchedEffect(Unit) {
        while (true) {
            medicalRecordAPI.bedDeviceGetReminder({
                reminders = it
            }, { e ->
                Toast.makeText(mainNavController.context, e?.code ?: "Unknown", Toast.LENGTH_SHORT).show()
            })

            medicalRecordAPI.bedDeviceGetRoutine({
                tasks = it
            }, { e ->
                Toast.makeText(mainNavController.context, e?.code ?: "Unknown", Toast.LENGTH_SHORT).show()
            })

            medicalRecordAPI.bedDeviceGetMedicalTranscript({ totals, msgs ->
                messages = msgs
            }, {
                Toast.makeText(mainNavController.context, it?.code ?: "Unknown", Toast.LENGTH_SHORT).show()
            })

            medicalRecordAPI.bedDevicePatientInfo({ info ->
                patientInfo = info
            }, {
                Toast.makeText(mainNavController.context, it?.code ?: "Unknown", Toast.LENGTH_SHORT).show()
            })

            delay(10000)
        }
    }

    val navigationItems = listOf(
        NavigationItem(
            title = "首頁",
            selectedIcon = painterResource(R.drawable.baseline_home_24),
            unselectedIcon = painterResource(R.drawable.baseline_home_24),
            hasNews = false,
            badgeCount = null,
            layout = { PatientCard(patientInfo) }
        ),
        NavigationItem(
            title = "待辦事項",
            selectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
            unselectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
            hasNews = false,
            badgeCount = 3,
            layout = { Routine(reminders, tasks) }
        ),
        NavigationItem(
            title = "交談紀錄",
            selectedIcon = painterResource(R.drawable.baseline_radio_24),
            unselectedIcon = painterResource(R.drawable.baseline_radio_24),
            hasNews = true,
            badgeCount = null,
            layout = { TranscriptScreen(messages) }
        ),
        NavigationItem(
            title = "行程表",
            selectedIcon = painterResource(R.drawable.baseline_today_24),
            unselectedIcon = painterResource(R.drawable.baseline_today_24),
            hasNews = true,
            badgeCount = null,
            layout = { NotImplementedScreen() }
        ),
        NavigationItem(
            title = "其他",
            selectedIcon = painterResource(R.drawable.baseline_dashboard_24),
            unselectedIcon = painterResource(R.drawable.baseline_dashboard_24),
            hasNews = false,
            badgeCount = null,
            layout = { NotImplementedScreen() }
        ),
        NavigationItem(
            title = "偏好設置",
            selectedIcon = painterResource(R.drawable.baseline_settings_24),
            unselectedIcon = painterResource(R.drawable.baseline_settings_24),
            hasNews = false,
            badgeCount = null,
            layout = { SettingScreen(esp32SerialCommunicator) {
                isRecording = it
            } }
        ),
    )
    var selectItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp),
    ){
        NavigationSideBar(
            items = navigationItems,
            selectedItemIndex = selectItemIndex,
            onNavigate = { selectItemIndex = it }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 8.dp)
        ) {
            if (selectItemIndex in navigationItems.indices) {
                navigationItems[selectItemIndex].layout()
            } else {
                Text("Invalid selection")
            }
        }
    }
}