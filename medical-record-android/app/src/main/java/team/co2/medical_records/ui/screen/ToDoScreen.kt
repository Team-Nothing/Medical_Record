package team.co2.medical_records.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Reminder(val message: String, val finished: Boolean)
data class Task(val time: String, val title: String, val description: String?, val finished: Boolean)

@Composable
fun Routine(reminders: List<Reminder>, tasks: List<Task>) {
    Row(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF3E5F5), shape = RoundedCornerShape(16.dp))
        .padding(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "提醒",
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
                )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp)),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(reminders) { reminder ->
                    ReminderList(reminder)
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "代辦事項",
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
                )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFFFFF), shape = RoundedCornerShape(16.dp)),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(tasks) { task ->
                    TaskList(task)
                }
            }
        }
    }
}


@Composable
fun ReminderList(reminder: Reminder) {
    Text(reminder.message, modifier = Modifier
        .padding(vertical = 4.dp)
        .background(Color(0xFFF8CCDB), shape = RoundedCornerShape(8.dp))
        .padding(8.dp)
        .fillMaxSize()
    )
}

@Composable
fun TaskList(task: Task) {
    Column(modifier = Modifier
        .padding(vertical = 4.dp)
        .background(Color(0xFF9CF8E6), shape = RoundedCornerShape(16.dp))
        .padding(8.dp)
        .fillMaxSize()
    ) {
        Text("${task.time} - ${task.title}")
    }
}
