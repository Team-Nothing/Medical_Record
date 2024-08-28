package team.co2.medical_records.ui.screen

import SettingScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import team.co2.medical_records.ChatScreen
import team.co2.medical_records.Routine
import team.co2.medical_records.Message
import team.co2.medical_records.ui.layout.NavigationItem
import team.co2.medical_records.ui.layout.NavigationSideBar
import team.co2.medical_records.PatientCard
import team.co2.medical_records.R
import team.co2.medical_records.Reminder
import team.co2.medical_records.Role
import team.co2.medical_records.Task
import team.co2.medical_records.ui.layout.NotImplementedScreen

@Composable
fun BedDeviceScreen(
    mainNavController: NavHostController
) {
    var reminders by remember { mutableStateOf(emptyList<Reminder>()) }
    var tasks by remember { mutableStateOf(emptyList<Task>()) }

    val navigationItems = listOf(
        NavigationItem(
            title = "首頁",
            selectedIcon = painterResource(R.drawable.baseline_home_24),
            unselectedIcon = painterResource(R.drawable.baseline_home_24),
            hasNews = false,
            badgeCount = null,
            layout = { PatientCard() }
        ),
        NavigationItem(
            title = "待辦事項",
            selectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
            unselectedIcon = painterResource(R.drawable.baseline_assignment_turned_in_24),
            hasNews = false,
            badgeCount = 3,
            layout = { Routine(reminders, tasks) }
        ),
        NavigationItem(
            title = "交談紀錄",
            selectedIcon = painterResource(R.drawable.baseline_radio_24),
            unselectedIcon = painterResource(R.drawable.baseline_radio_24),
            hasNews = true,
            badgeCount = null,
            layout = { ChatScreen(emptyList()) }
        ),
        NavigationItem(
            title = "行程表",
            selectedIcon = painterResource(R.drawable.baseline_today_24),
            unselectedIcon = painterResource(R.drawable.baseline_today_24),
            hasNews = true,
            badgeCount = null,
            layout = { NotImplementedScreen() }
        ),
        NavigationItem(
            title = "其他",
            selectedIcon = painterResource(R.drawable.baseline_dashboard_24),
            unselectedIcon = painterResource(R.drawable.baseline_dashboard_24),
            hasNews = false,
            badgeCount = null,
            layout = { NotImplementedScreen() }
        ),
        NavigationItem(
            title = "偏好設置",
            selectedIcon = painterResource(R.drawable.baseline_settings_24),
            unselectedIcon = painterResource(R.drawable.baseline_settings_24),
            hasNews = false,
            badgeCount = null,
            layout = { SettingScreen() }
        ),
    )
    var selectItemIndex by rememberSaveable { mutableIntStateOf(0) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 0.dp),
    ){
        NavigationSideBar(
            items = navigationItems,
            selectedItemIndex = selectItemIndex,
            onNavigate = { selectItemIndex = it }
        )
        //聊天紀錄
        val messages = listOf(
            Message(1,"LED", Role.DOCTOR, "汪汪先生好，昨晚睡得如何", "2024-07-28 10:00", true),
            Message(2,"汪O安", Role.PATIENT, "我要找電燈泡，不是LED", "2024-07-28 10:00", false),
            Message(3,"一隻魚", Role.NURSE, "汪先生，主治醫師在問妳昨晚睡得好嗎", "2024-07-28 10:30", true)
        )

        reminders = listOf(
            Reminder("晚上12點後禁止飲食，包含飲水"),
            Reminder("貴重物品簽收單"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同"),
            Reminder("告知家人或朋友手術時間和地點，以便陪同")
        )

        tasks = listOf(
            Task("07:00", "吃降血壓藥"),
            Task("08:00", "手術及麻醉同意書"),
            Task("08:05", "手術部位註記"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人"),
            Task("08:15", "等待進入手術室，並通知主要聯絡人")

        )

//        val views: List<@Composable () -> Unit> = listOf(
//            { PatientCard() },
//            { MainContent(reminders, tasks) },
//            { ChatScreen(messages) },
//            { NotImplementedScreen() },
//            { NotImplementedScreen() },
//            { SettingScreen() }
//        )
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 8.dp)
        ) {
            if (selectItemIndex in navigationItems.indices) {
                navigationItems[selectItemIndex].layout()
            } else {
                Text("Invalid selection")
            }
        }

//        when(selectItemIndex){
//            0 -> PatientCard()
//            1 -> MainContent(reminders,tasks)
//            2 -> ChatScreen(messages)
//            5 -> SettingScreen()
//        }
    }
}