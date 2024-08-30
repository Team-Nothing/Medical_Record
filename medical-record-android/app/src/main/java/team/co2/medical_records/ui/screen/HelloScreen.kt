package team.co2.medical_records.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import team.co2.medical_records.R
import team.co2.medical_records.service.medical_record_api.AccountType
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI

@Composable
fun HelloScreen(navController: NavHostController, medicalRecordAPI: MedicalRecordAPI, context: Context) {
    var showError by remember { mutableStateOf(false) }
    var isCheckingStatus by remember { mutableStateOf(false) }
    var isFirst by remember { mutableStateOf(true) }

    fun getStatus() {
        medicalRecordAPI.status({
            showError = false
            medicalRecordAPI.authCheckSession({ accountType ->
                Log.d("HelloScreen", "accountType: $accountType")
                when(accountType) {
                    AccountType.BED_DEVICE -> {
                        navController.navigate("bed-device") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    else -> navController.navigate("select-account-type")
                }
            }, { _ ->
                navController.navigate("register") {
                    popUpTo(0) { inclusive = true }
                }
            })
        }, { _ ->
            showError = true
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 16.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_medical_information_24),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(128.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "醫療紀錄整合輔助系統",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp
            )
        }
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = {
                showError = false
                isCheckingStatus = true
            },
            title = {
                Text(text = "網路錯誤")
            },
            text = {
                Text(text = "無法連接至伺服器")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showError = false
                        isCheckingStatus = true
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }

    if (isCheckingStatus) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "等待 5 秒後嘗試重新連線 ...", Toast.LENGTH_SHORT).show()
            delay(5000)
            isCheckingStatus = false
            getStatus()
        }
    } else if (isFirst) {
        isFirst = false
        LaunchedEffect(Unit) {
            delay(1500)
            getStatus()
        }
    }
}

