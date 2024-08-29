package team.co2.medical_records.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import team.co2.medical_records.R
import team.co2.medical_records.service.medical_record_api.AuthError
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI


data class TypeButton(
    val title: String,
    val icon: Int,
    val linkType: LinkType
)

@Composable
fun SelectTypeScreen(navController: NavHostController, medicalRecordAPI: MedicalRecordAPI, context: Context) {
    val typeButtons = listOf(
        TypeButton("醫生", R.drawable.baseline_face_5_24, LinkType.DOCTOR),
        TypeButton("護士", R.drawable.baseline_face_4_24, LinkType.NURSE),
        TypeButton("床邊裝置", R.drawable.baseline_bed_24, LinkType.BED_DEVICE),
        TypeButton("管理員", R.drawable.baseline_manage_accounts_24, LinkType.MANAGER)
    )

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
                    text = "請選擇使用者角色",
                    fontSize = 24.sp,
                    color = Color.Black
                )
                TextButton(
                    onClick = {
                        medicalRecordAPI.authLogout({
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }, { code ->
                            when(code) {
                                AuthError.SESSION_NOT_FOUND,
                                AuthError.INVALID_SESSION,
                                AuthError.MISSING_DEVICE_ID,
                                AuthError.SESSION_EXPIRED -> {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                                else -> {
                                    Toast.makeText(context, "登出失敗: $code", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                    },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text="或是切換帳號？ >登出<",
                        color = MaterialTheme.colorScheme.primary,
                        style = TextStyle(fontSize = 12.sp)
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp)
            ) {
                typeButtons.forEach{ typeButton ->
                    item {
                        Button(
                            onClick = {
                                navController.navigate("link/${typeButton.linkType.type}")
                            },
                            modifier = Modifier
                                .aspectRatio(1f) // Makes the button square
                                .padding(8.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = painterResource(typeButton.icon),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(96.dp) // Adjust size as needed
                                )
                                Text(text = typeButton.title)
                            }
                        }
                    }
                }
            }
        }
    }
}