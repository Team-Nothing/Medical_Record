package team.co2.medical_records.ui.screen

import android.content.Context
import android.content.Context.USB_SERVICE
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.hoho.android.usbserial.driver.UsbSerialDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import team.co2.medical_records.R
import team.co2.medical_records.service.bluetooth.BluetoothResponse
import team.co2.medical_records.service.bluetooth.ESP32Communicator
import team.co2.medical_records.service.medical_record_api.DeviceError
import team.co2.medical_records.service.medical_record_api.GenericResponse
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI
import java.io.Console


enum class LinkType(val type: String) {
    DOCTOR("doctor"),
    NURSE("nurse"),
    BED_DEVICE("bed_device"),
    MANAGER("manager");

    companion object {
        fun fromType(type: String): LinkType? {
            return entries.find { it.type == type }
        }
    }
}

data class TypeConfig(
    val title: String,
    var inputFields: List<InputField> = emptyList(),
    val useBluetoothScanner: Boolean = false,
    val action: (inputFields: List<InputField>) -> Unit = {}
)

data class InputField(
    val title: String,
    var data: MutableState<String> = mutableStateOf(""),
)

data class BluetoothDevice(
    val deviceId: Int,
    val vendorId: Int,
    val mac: String,
)

@Composable
fun LinkScreen(navController: NavHostController, medicalRecordAPI: MedicalRecordAPI, context: Context, linkType: LinkType?) {
    var bluetoothDevice: BluetoothDevice? by remember { mutableStateOf(null) }
    var usbDevices: List<UsbSerialDriver> by remember { mutableStateOf(emptyList()) }
    var showError by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    var esp32SerialCommunicator: ESP32Communicator? = null

    val typeConfig by remember {
        mutableStateOf( when(linkType) {
            LinkType.DOCTOR -> TypeConfig(
                "將此帳號連接至 -> 醫生"
            )
            LinkType.NURSE -> TypeConfig("將此帳號連接至 -> 護士")
            LinkType.BED_DEVICE -> TypeConfig(
                "將此帳號連接至 -> 床邊裝置",
                listOf(
                    InputField("病床 ID"),
                ), true
            ) { inputFields ->
                medicalRecordAPI.bedDeviceAdd(bluetoothDevice, inputFields[0].data.value, {
                    navController.navigate("bed-device") {
                        popUpTo(0) { inclusive = true }
                    }
                }, { code ->
                    showError = when(code) {
                        DeviceError.MISSING_BLUETOOTH_MAC -> "請先設定藍芽掃描器"
                        DeviceError.MISSING_BED_ID -> "請輸入病床 ID"
                        DeviceError.SESSION_NOT_FOUND,
                        DeviceError.INVALID_SESSION,
                        DeviceError.SESSION_EXPIRED -> {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                            "請重新登入"
                        }
                        else -> "未知錯誤 $code"
                    }
                })
            }

            LinkType.MANAGER -> TypeConfig("將此帳號連接至 -> 管理員")
            else -> TypeConfig("未知")
        })
    }

    if (typeConfig.useBluetoothScanner) {
        val usbManager = getSystemService(context, UsbManager::class.java)
        if (usbManager == null) {
            Log.e("USB_MANAGER", "Failed to get USB Manager")
            return
        }
        esp32SerialCommunicator = ESP32Communicator(usbManager)

        LaunchedEffect(Unit) {
            while (isActive) {
                val deviceList = esp32SerialCommunicator.getAllUsbDevices()
                usbDevices = deviceList
                delay(3000)
            }
        }
    }

    if (showError.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {
                showError = ""
            },
            title = {
                Text(text = "連接錯誤")
            },
            text = {
                Text(text = showError)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showError = ""
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), // Fill the entire available space
        contentAlignment = Alignment.Center // Center content inside the Box
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = typeConfig.title,
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp)
            ) {
                if (typeConfig.useBluetoothScanner) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
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
                        DeviceList(LocalContext.current, usbDevices, esp32SerialCommunicator!!) { device ->
                            bluetoothDevice = device
                        }
                    }
                }
                typeConfig.inputFields.forEachIndexed { index, item ->
                    OutlinedTextField(
                        value = item.data.value,
                        onValueChange = { item.data.value = it },
                        label = { Text("請輸入${item.title}") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = if (index == typeConfig.inputFields.size - 1) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        typeConfig.action(typeConfig.inputFields)
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_right_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp), // Adjust size as needed
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "連接",
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceList(context: Context, usbDevices: List<UsbSerialDriver>, esp32SerialCommunicator: ESP32Communicator, onBluetoothMacChange: (BluetoothDevice) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem: UsbSerialDriver? by remember { mutableStateOf(null) }
    var hasFound by remember { mutableStateOf(false) }

    var buttonWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current


    if (usbDevices.isEmpty()) {
        selectedItem = null
        hasFound = false
    } else if (!hasFound) {
        val index = esp32SerialCommunicator.findDevice(context, usbDevices)
        Log.d("USB_DEVICE", "Index: $index")
        if (index != -1) {
            selectedItem = usbDevices[index]
            hasFound = true
            onBluetoothMacChange(
                BluetoothDevice(
                    selectedItem!!.device.deviceId,
                    selectedItem!!.device.vendorId,
                    selectedItem!!.device.deviceName
                ),
            )
        } else if (selectedItem != null) {
            esp32SerialCommunicator.setDeviceConnection(selectedItem!!).startReading { data ->
                try {
                    val bluetoothData = Gson().fromJson(data, BluetoothResponse::class.java)
                    if (bluetoothData.MAC.isNotEmpty()) {
                        hasFound = true
                        esp32SerialCommunicator.saveData(selectedItem!!, context)
                        onBluetoothMacChange(
                            BluetoothDevice(
                                selectedItem!!.device.deviceId,
                                selectedItem!!.device.vendorId,
                                bluetoothData.MAC
                            ),
                        )
                        esp32SerialCommunicator.stopReading()
                    }
                } catch (e: Exception) {
                    Log.e("USB_DEVICE", e.message ?: "Unknown error")
                }
            }
        }
    }

    Column {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // Capture the width of the button
                    buttonWidth = with(density) { coordinates.size.width.toDp() }
                }
        ) {
            Text(selectedItem?.device?.deviceName ?: "未選擇")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(buttonWidth)
        ) {
            usbDevices.forEach { device ->
                DropdownMenuItem(
                    text = { Text("${device.device.deviceName} - ${device.device.manufacturerName ?: "unknown"}") },
                    onClick = {
                        selectedItem = device
                        expanded = false
                    }
                )
            }
        }
    }
}
