package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ExperimentView
 * @author: Tony Shen
 * @date: 2024/9/23 19:37
 * @version: V1.0 <描述当前版本功能>
 */
/**
 * Screens
 */
enum class Screen(
    val label: String,
    val icon: ImageVector
) {
    BinaryImageAnalysis(
        label = "二值图像分析",
        icon = Icons.Filled.Home
    ),
    ContourAnalysis(
        label = "轮廓分析",
        icon = Icons.Filled.Notifications
    ),
    ImageConvolution(
        label = "图像卷积",
        icon = Icons.Filled.Settings
    ),
    MorphologicalOperations(
        label = "形态学操作",
        icon = Icons.Filled.Settings
    ),
    TemplateMatching(
        label = "模版匹配",
        icon = Icons.Filled.Settings
    )
}

@Composable
fun CustomNavigationHost(
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.BinaryImageAnalysis.name) {
//            HomeScreen(navController)
        }

        composable(Screen.ContourAnalysis.name) {
//            NotificationScreen(navController)
        }

        composable(Screen.ImageConvolution.name) {
//            SettingScreen(navController)
        }

    }.build()
}

@Composable
fun experiment(state: ApplicationState) {

    val screens = Screen.values().toList()
    val navController by rememberNavController(Screen.BinaryImageAnalysis.name)
    val currentScreen by remember {
        navController.currentScreen
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        NavigationRail(
            modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().width(80.dp)
        ) {
            screens.forEach {
                NavigationRailItem(
                    selected = currentScreen == it.name,
                    icon = {
                        Icon(
                            imageVector = it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = {
                        Text(it.label)
                    },
                    modifier = Modifier.width(100.dp),
                    alwaysShowLabel = true,
                    onClick = {
                        navController.navigate(it.name)
                    }
                )
            }
        }

        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CustomNavigationHost(navController = navController)
        }
    }
}