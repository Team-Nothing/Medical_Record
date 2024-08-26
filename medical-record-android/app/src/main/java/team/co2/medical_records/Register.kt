package team.co2.medical_records

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.res.painterResource


@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2E9FA)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "醫療病例自動記錄系統",
                    fontSize = 24.sp,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("請輸入帳號") },
                    leadingIcon = { Icon(
                        painter = painterResource(R.drawable.baseline_person_outline_24),
                        contentDescription = null
                    ) }
                )
                // 密碼輸入框
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("請輸入密碼") },
                    visualTransformation = PasswordVisualTransformation(),  // 隱藏密碼輸入
                    leadingIcon = {Icon(
                        painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                        contentDescription = null
                    ) }
                )
                // 密碼輸入框
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("請確認輸入密碼") },
                    visualTransformation = PasswordVisualTransformation(),  // 隱藏密碼輸入
                    leadingIcon = {Icon(
                        painter = painterResource(R.drawable.baseline_remove_red_eye_24),
                        contentDescription = null
                    ) }
                )
                TextButton(
                    onClick = { /* 處理註冊邏輯 */ },
                    colors = ButtonDefaults.buttonColors(Color(0xFFB589F5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REGISTER", color = Color.White)
                }
                TextButton(onClick = { /* 處理登入邏輯 */ }) {
                    Text("已經有帳號？ > 登陸吧 <", color = Color.Gray)
                }
            }
        }
    }
}