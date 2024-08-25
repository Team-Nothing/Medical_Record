package team.co2.medical_records

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun PatientCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "汪 O 安 ",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(2f)
            )
//            Spacer(modifier = Modifier.width(8.dp))
//            Text(text = "♂", fontSize = 18.sp
            Column(modifier = Modifier.weight(0.5f)) {
                Text(text = "♂", fontSize = 18.sp)
                Text(
                    text = "先生",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            Column (
                modifier = Modifier
                    .weight(2f)
                    .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                    .padding(8.dp) // 内容的边距

            ){
                Text(text = "年齡：21y 6m")
                Text(text = "血型：O")
                Text(text = "住院天數：21")
                Text(text = "主要語言：中文")
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(text = "7A187", fontWeight = FontWeight.Bold)
                }

                Text(text = "精神科", fontWeight = FontWeight.Bold)
            }

        }


//        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // 添加内边距
                .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp))
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), // 项目之间的间隔
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐

        ) {
            Chip("禁食", painterResource(R.drawable.baseline_no_food_24), Color.Red)
            Chip("尚未吃藥", painterResource(R.drawable.baseline_medication_liquid_24), Color.Yellow)
        }

//        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // 添加内边距
            horizontalArrangement = Arrangement.spacedBy(16.dp), // 项目之间的间隔
            verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
        ) {
            DoctorCard("主治醫師", "LED", Modifier.weight(1f))
            DoctorCard("住院醫師", "XXX", Modifier.weight(1f))
            DoctorCard("護理師", "XXX", Modifier.weight(1f))
        }

//        Spacer(modifier = Modifier.height(8.dp))
        val serialNumber = "XDDDDDDDDDDDDDDDDDDDDDD"
        Row(
            modifier = Modifier.fillMaxWidth(), // 让 Row 占满宽度
            horizontalArrangement = Arrangement.End
        ){
            Text(text = "Serial: $serialNumber")
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
fun DoctorCard(role: String, name: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column() {
            Text(text = role, color = Color.Black, fontWeight = FontWeight.Bold)
            Icon(painter = painterResource(R.drawable.baseline_person_24), contentDescription = role, tint = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = name, fontWeight = FontWeight.Bold)
    }
}
