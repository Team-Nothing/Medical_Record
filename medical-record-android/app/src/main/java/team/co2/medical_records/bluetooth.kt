import android.content.Context
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.FlowRowScopeInstance.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.res.painterResource
import team.co2.medical_records.R
import androidx.compose.material3.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import com.hoho.android.usbserial.driver.UsbSerialDriver
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import team.co2.medical_records.service.bluetooth.BluetoothResponse
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.ui.screen.BluetoothDevice
import team.co2.medical_records.ui.screen.DeviceList


@Composable
fun SettingScreen(esp32SerialCommunicator: ESP32Communicator?, context: Context) {
    var bluetoothDevice: BluetoothDevice? by remember { mutableStateOf(null) }
    var usbDevices: List<UsbSerialDriver> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        while (isActive && esp32SerialCommunicator != null) {
            val deviceList = esp32SerialCommunicator.getAllUsbDevices()
            usbDevices = deviceList
            delay(3000)
        }
    }
//    Box(
//        modifier = Modifier.fillMaxSize(), // Fill the entire available space
//        contentAlignment = Alignment.Center // Center content inside the Box
//    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.wrapContentWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "偏好設定",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column(
                modifier = Modifier
//                    .weight(1f)
                    .fillMaxHeight()
                    .fillMaxWidth()
                    .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                if (esp32SerialCommunicator != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth().background(Color.White, shape = RoundedCornerShape(16.dp)).padding(8.dp)
                    ) {
                        Text(
                            text = "連接藍芽偵測器",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                        )
                        Text(
                            text = if (bluetoothDevice != null) {
                                "找尋成功: ${bluetoothDevice!!.mac}"
                            } else {
                                "請透過 USB 連接本裝置以進行設定"
                            },
                            color = Color.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                        )
                        DeviceList(LocalContext.current, usbDevices, esp32SerialCommunicator) { device ->
                            bluetoothDevice = device
                        }
                    }
                }
            }


        }

    }
//}

@Composable
fun DropdownMenuSample() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Option 1", "Option 2", "Option 3")
    var selectedOption by remember { mutableStateOf(options[0]) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedOption,
            onValueChange = { },
            readOnly = true,
            label = { Text("裝置選擇") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
                        contentDescription = "Dropdown Icon"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}