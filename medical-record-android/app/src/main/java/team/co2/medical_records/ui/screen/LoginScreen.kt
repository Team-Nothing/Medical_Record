package team.co2.medical_records.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import team.co2.medical_records.R
import team.co2.medical_records.service.medical_record_api.AuthError
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI


@Composable
fun LoginScreen(navController: NavHostController, medicalRecordAPI: MedicalRecordAPI, context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var responseString by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
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
                    text = "醫療紀錄整合輔助系統",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "登入:",
                        color = Color.Black,
                    )
                }
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("請輸入帳號") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_person_outline_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                // 密碼輸入框
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("請輸入密碼") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() } // Dismiss the keyboard
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate("register") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text="還沒有帳號？ > 註冊 <",
                            color = MaterialTheme.colorScheme.primary,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            medicalRecordAPI.authLogin(username, password, {
                                navController.navigate("select-account-type") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }, { code ->
                                responseString = when (code) {
                                    AuthError.USERNAME_IS_EMPTY -> "帳號不能為空"
                                    AuthError.PASSWORD_IS_EMPTY -> "密碼不能為空"
                                    AuthError.PASSWORD_MISMATCH, AuthError.USER_NOT_FOUND -> "帳號或密碼錯誤"
                                    AuthError.MISSING_DEVICE_ID -> "無法抓取裝置ID"
                                    else -> "登入失敗: $code"
                                }
                            })
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                        ) {
                            // Replace with your icon resource
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_right_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp), // Adjust size as needed
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                            Text(
                                text = "登入",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                            )
                        }
                    }

                }

            }
        }
    }
    if (responseString.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "登入失敗")
            },
            text = {
                Text(text = responseString)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        responseString = ""
                    }
                ) {
                    Text(text = "OK")
                }
            }
        )
    }
}