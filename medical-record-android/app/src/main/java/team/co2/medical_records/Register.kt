package team.co2.medical_records

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle


@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "醫療病例自動記錄系統",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "註冊:",
                        color = Color.Black,
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("請輸入帳號") },
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
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("請輸入密碼") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                // 密碼輸入框
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("請確認輸入密碼") },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = { /* 處理登入邏輯 */ },
                            modifier = Modifier.wrapContentWidth()
                    ) {

                        Text(
                            text="已經有帳號？ > 登陸 <",
                            color = Color.Black,
                            style = TextStyle(
                            fontSize = 12.sp, // Adjust font size as needed
                        ))

                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = { /* 處理註冊邏輯 */ },
                        colors = ButtonDefaults.buttonColors(Color(0xFFB589F5)),
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Replace with your icon resource
                            Icon(
                                painter = painterResource(R.drawable.baseline_arrow_right_24),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp), // Adjust size as needed
                                tint = Color.White
                            )
                            Text(
                                text = "REGISTER",
                                color = Color.White,
                                modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                            )
                        }
                    }

                }

            }
        }
    }

}