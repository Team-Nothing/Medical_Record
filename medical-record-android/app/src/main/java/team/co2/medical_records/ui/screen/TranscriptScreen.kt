package team.co2.medical_records.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import team.co2.medical_records.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class Message(
    val id: Int,
    val time: String,
    val leftRight: LeftRight,
    val name: String,
    val content: String
)

enum class LeftRight {
    LEFT, RIGHT
}


@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun TranscriptScreen(messages: List<Message>) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000) // Check every second
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastInteractionTime > 15000) { // 15 seconds inactivity
                coroutineScope.launch {
                    listState.scrollToItem(0) // Scroll to top
                }
                lastInteractionTime = currentTime // Reset the timer after scrolling to the top
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Release) {
                            lastInteractionTime = System.currentTimeMillis()
                        }
                    }
                }
            },
        contentPadding = PaddingValues(8.dp),
        reverseLayout = true,
        state = listState,
    ) {
        var lastMessageTime: String? = null
        messages.forEachIndexed { index, message ->
            item {
                MessageItem(message)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (shouldShowDateSeparator(lastMessageTime, message.time) || index == messages.lastIndex) {
                item {
                    DateSeparator(message.time)
                }
            }
            lastMessageTime = message.time
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    Row(
        horizontalArrangement = if (message.leftRight == LeftRight.RIGHT) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        val messageTime = LocalDateTime.parse(message.time, formatter).format(timeFormatter)

        if (message.leftRight == LeftRight.LEFT) {
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
                    text = message.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Text(text = message.content, fontSize = 16.sp)
        }
        if (message.leftRight == LeftRight.RIGHT) {
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
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
        Text(
            text = time,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.Gray
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

fun shouldShowDateSeparator(lastTime: String?, currentTime: String): Boolean {
    if (lastTime == null) return true

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val lastDateTime = LocalDateTime.parse(lastTime, formatter)
    val currentDateTime = LocalDateTime.parse(currentTime, formatter)

    val minutesBetween = ChronoUnit.MINUTES.between(lastDateTime, currentDateTime)

    return minutesBetween >= 5
}
