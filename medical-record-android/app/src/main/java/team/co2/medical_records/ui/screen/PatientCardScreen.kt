package team.co2.medical_records.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import team.co2.medical_records.R
import team.co2.medical_records.service.medical_record_api.BedDevicePatientInfoResponse
import team.co2.medical_records.ui.layout.NotImplementedScreen


@Composable
fun PatientCard(patientInfo: BedDevicePatientInfoResponse.Data?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp))
    ) {
        if (patientInfo == null || !patientInfo.has_patient) {
            NotImplementedScreen()
        } else{
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .width(300.dp)
                        .padding(16.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = patientInfo.patient!!.name,
                        fontSize = 60.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Column (
                        verticalArrangement = Arrangement.Center
                    ){
                        Text(text = if (patientInfo.patient.gender == "男性") "♂" else "♀", fontSize = 18.sp)
                        Text(
                            text = if (patientInfo.patient.gender == "男性") "先生" else "女士",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Column (
                    modifier = Modifier
                        .weight(2f)
                        .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp, 8.dp) // 内容的边距

                ){
                    Text(text = "年齡：${patientInfo.patient!!.age.split("-")[0]}歲 ${patientInfo.patient!!.age.split("-")[1]}月", fontSize = 14.sp)
                    Text(text = "血型：${patientInfo.patient.blood}", fontSize = 14.sp)
                    Text(text = "住院天數：${patientInfo.admission_days}", fontSize = 14.sp)
                    Text(text = "主要語言：${patientInfo.patient.language}", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    Column(
                        modifier = Modifier
                            .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                            .padding(24.dp, 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(text = patientInfo.bed ?: "未知", fontWeight = FontWeight.Bold)
                    }

                    Text(text = patientInfo.department ?: "未知", fontWeight = FontWeight.Bold)
                }

            }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // 项目之间的间隔
                    verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                ) {
                    Chip("禁食", painterResource(R.drawable.baseline_no_food_24), Color.Red)
                    Chip("尚未吃藥", painterResource(R.drawable.baseline_medication_liquid_24), Color.Yellow)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // 添加内边距
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 项目之间的间隔
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            DoctorCard("主治醫師", patientInfo?.doctor?.name ?: "未知", Modifier.weight(1f), null)
            DoctorCard("住院醫師", patientInfo?.resident?.name ?: "未知", Modifier.weight(1f), R.drawable.baseline_face_6_24)
            DoctorCard("護理師", patientInfo?.nurse?.name ?: "未知", Modifier.weight(1f), R.drawable.baseline_face_4_24)
        }
    }
}

@Composable
fun Chip(text: String, icon: Painter, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)

    ) {
        Icon(painter = icon, contentDescription = text, tint = Color.White)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = Color.White)
    }
}

@Composable
fun DoctorCard(role: String, name: String, modifier: Modifier = Modifier, resource: Int?) {
    Column(
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp)

    ) {
        Text(text = role, color = Color.Black, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (resource == null) {
                Image(
                    painter = painterResource(R.drawable.led),  // Replace with your actual image resource name
                    contentDescription = "My Image",
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                )
            } else {
                Icon(painter = painterResource(resource), contentDescription = role,
                    tint = Color.Gray, modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxHeight())
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
        }
    }
}
