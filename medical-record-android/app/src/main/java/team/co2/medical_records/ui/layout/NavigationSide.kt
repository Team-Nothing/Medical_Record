package team.co2.medical_records.ui.layout

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import team.co2.medical_records.R

data class NavigationItem(
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val hasNews: Boolean,
    val badgeCount: Int? = null,
    val layout: @Composable () -> Unit
)

@Composable
fun NavigationSideBar(
    items: List<NavigationItem>,
    selectedItemIndex: Int,
    onNavigate: (Int) -> Unit
){
    val scrollState = rememberScrollState()
    NavigationRail (
        header = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                elevation = FloatingActionButtonDefaults.elevation()
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_add_alert_24),
                    contentDescription = "Add"
                )
            }
        },
        content = {
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .wrapContentSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                items.forEachIndexed { index, item ->
                    var itemOffsetY by remember { mutableIntStateOf(0) }
                    var itemHeight by remember { mutableIntStateOf(0) }

                    // Measure item position and size
                    Box(
                        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
                            itemOffsetY = layoutCoordinates.positionInParent().y.toInt()
                            itemHeight = layoutCoordinates.size.height
                        }
                    ) {
                        NavigationRailItem(
                            selected = selectedItemIndex == index,
                            onClick = { onNavigate(index) },
                            icon = {
                                NavigationIcon(
                                    item = item,
                                    selected = selectedItemIndex == index,
                                    showBadge = isItemVisible(
                                        scrollState = scrollState,
                                        itemOffsetY = itemOffsetY,
                                        itemHeight = itemHeight
                                    )
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
    )
//    {
//        Column(
//            modifier = Modifier.verticalScroll(scrollState).wrapContentSize()
//
//        ){
//            rails.forEachIndexed{ index, item ->
//                NavigationRailItem(
//                    selected = selectedItemIndex == index,
//                    onClick = { onNavigate(index) },
//                    icon={
//                        NavigationIcon(
//                            item = item,
//                            selected = selectedItemIndex == index
//                        )
//                    },
//                    label = {
//                        Text(text = item.title)
//                    }
//                )
//            }
//        }
//
//    }

}

@Composable
fun NavigationIcon(
    item: NavigationItem,
    selected: Boolean,
    showBadge: Boolean
) {
    BadgedBox(
        badge = {
            if (showBadge) {
                if (item.badgeCount != null) {
                    Badge {
                        Text(text = item.badgeCount.toString())
                    }
                } else if (item.hasNews) {
                    Badge()
                }
            }
        }
    ) {
        Icon(
            painter = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
        )
    }
}

fun isItemVisible(
    scrollState: ScrollState,
    itemOffsetY: Int,
    itemHeight: Int
): Boolean {
    val viewportStart = scrollState.value
    val viewportEnd = scrollState.value + scrollState.maxValue

    Log.d("XDD", "viewportStart: $viewportStart, viewportEnd: $viewportEnd, itemOffsetY: $itemOffsetY, itemHeight: $itemHeight")

    return itemOffsetY - 24 > viewportStart
}