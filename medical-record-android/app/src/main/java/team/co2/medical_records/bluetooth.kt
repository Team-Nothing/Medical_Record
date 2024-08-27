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


@Composable
fun SettingScreen() {
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
                TextButton(
                    onClick = { /* 處理設定邏輯 */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)), // 设置圆角半径为 16dp
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = "連接藍芽偵測器",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                        )
                        Text(
                            text = "即時掃描身邊的藍芽裝置",
                            color = Color.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp) // Adjust spacing as needed
                        )
                        DropdownMenuSample()
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