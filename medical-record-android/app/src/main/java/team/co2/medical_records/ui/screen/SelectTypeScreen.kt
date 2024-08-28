package team.co2.medical_records.ui.screen

import android.content.Context
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
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import team.co2.medical_records.R
import team.co2.medical_records.service.medical_record_api.MedicalRecordAPI


data class TypeButton(
    val title: String,
    val icon: Int,
    val action: () -> Unit = {}
)

@Composable
fun SelectTypeScreen(navController: NavHostController, medicalRecordAPI: MedicalRecordAPI, context: Context) {
    val typeButtons = listOf(
        TypeButton("醫生", R.drawable.baseline_face_5_24),
        TypeButton("護士", R.drawable.baseline_face_4_24),
        TypeButton("床邊裝置", R.drawable.baseline_bed_24),
        TypeButton("管理員", R.drawable.baseline_manage_accounts_24)
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
                            onClick = { /* Handle button click */ },
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