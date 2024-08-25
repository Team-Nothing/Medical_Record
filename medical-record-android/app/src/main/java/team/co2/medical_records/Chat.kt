package team.co2.medical_records

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Message(
    val id: Int,
    val name: String,
    val role: Role,
    val content: String,
    val time: String,// 例如："2024-07-28 10:00
    val isVoiceMessage: Boolean
)

enum class Role {
    DOCTOR, NURSE, PATIENT
}


@Composable
fun ChatScreen(messages: List<Message>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp)),
        contentPadding = PaddingValues(8.dp)
    ) {
        var lastMessageTime: String? = null
        messages.forEachIndexed { index, message ->
            if (lastMessageTime == null || shouldShowDateSeparator(lastMessageTime, message.time)) {
                item {
                    DateSeparator(message.time)
                }
            }
            item {
                MessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
            lastMessageTime = message.time
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Row(
        horizontalArrangement = if (message.role == Role.PATIENT) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val messageTime = LocalDateTime.parse(message.time, formatter).format(timeFormatter)

        if (message.role == Role.PATIENT) {
            Text(text = messageTime, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .wrapContentSize()
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){

                    Icon(
                        painter = painterResource(R.drawable.baseline_person_24),
                        contentDescription = null
                    )
                    Text(
                        text = "${message.role} - ${message.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
//                    Spacer(modifier = Modifier.weight(1f))

                    if (message.isVoiceMessage) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_volume_up_24),
                            contentDescription = null
                        )
                    }

                }
            Text(text = message.content, fontSize = 16.sp)
            }
        if (message.role != Role.PATIENT) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = messageTime, fontSize = 16.sp)
        }
    }
}


@Composable
fun DateSeparator(time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = time,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.Gray
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.weight(1f)
        )
    }
}

fun shouldShowDateSeparator(lastTime: String?, currentTime: String): Boolean {
    if (lastTime == null) return true

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val lastDateTime = LocalDateTime.parse(lastTime, formatter)
    val currentDateTime = LocalDateTime.parse(currentTime, formatter)

    val minutesBetween = ChronoUnit.MINUTES.between(lastDateTime, currentDateTime)



    return minutesBetween >= 15
}
