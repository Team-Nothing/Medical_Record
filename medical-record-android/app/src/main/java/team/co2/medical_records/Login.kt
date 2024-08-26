import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import team.co2.medical_records.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2E9FA)),  // 設定背景顏色為淺紫色
        contentAlignment = Alignment.Center  // 將內容居中對齊
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,  // 水平居中
            verticalArrangement = Arrangement.spacedBy(20.dp)  // 垂直方向間隔20dp
        ) {
            // 標題文字 "醫療病例自動記錄系統"
            Text(text = "醫療病例自動記錄系統", fontSize = 24.sp, color = Color.Black)

            // 帳號輸入框
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

            // 登錄按鈕
            TextButton(onClick = { /* 處理登錄邏輯 */ }) {
                Text("LOGIN")
            }

            // 註冊按鈕
            TextButton(onClick = { /* 處理註冊邏輯 */ }) {
                Text("還沒有帳號？ > 請註冊 <")
            }
        }
    }
}