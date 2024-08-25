package team.co2.medical_records

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource

data class NavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val hasNews: Boolean,
    val badgeCount: Int? = null,

    )

@Composable
fun NavigationSideBar(
    item: List<NavigationItem>,
    selectedItemIndex: Int,
    onNavigate: (Int) -> Unit


){
    val scrollState = rememberScrollState()
    NavigationRail (
        header = {

            FloatingActionButton(
                onClick = { /*TODO*/ },
                elevation = FloatingActionButtonDefaults.elevation()
            ){
                Icon(
                    painter = painterResource(R.drawable.baseline_add_alert_24) ,
                    contentDescription = "Add"
                )

            }
        }
    ){
        Column(
            modifier = Modifier.verticalScroll(scrollState).wrapContentSize()

        ){
            item.forEachIndexed{ index,item ->
                NavigationRailItem(
                    selected = selectedItemIndex == index,
                    onClick = { onNavigate(index) },
                    icon={
                        NavigationIcon(
                            item = item,
                            Selected = selectedItemIndex == index
                        )
                    },
                    label = {
                        Text(text = item.title)
                    }
                )
            }
        }

    }

}

@Composable
fun NavigationIcon(
    item: NavigationItem,
    Selected: Boolean,
){
    BadgedBox(
        badge ={
            if(item.badgeCount != null){
                Badge{
                    Text(text = item.badgeCount.toString())
                }
            }else if (item.hasNews){
                Badge()
            }
        }
    ) {
        Icon(
            painter = if(Selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
        )

    }
}