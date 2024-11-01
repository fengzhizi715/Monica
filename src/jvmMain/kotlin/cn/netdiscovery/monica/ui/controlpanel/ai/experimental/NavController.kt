package cn.netdiscovery.monica.ui.controlpanel.ai.experimental

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.NavController
 * @author: Tony Shen
 * @date: 2024/9/23 20:04
 * @version: V1.0 <描述当前版本功能>
 */
class NavController(
    private val startDestination: String,
    private var backStackScreens: MutableSet<String> = mutableSetOf()
) {
    // Variable to store the state of the current screen
    var currentScreen: MutableState<String> = mutableStateOf(startDestination)

    // Function to handle the navigation between the screen
    fun navigate(route: String) {
        if (route != currentScreen.value) {
            if (backStackScreens.contains(currentScreen.value) && currentScreen.value != startDestination) {
                backStackScreens.remove(currentScreen.value)
            }

            if (route == startDestination) {
                backStackScreens = mutableSetOf()
            } else {
                backStackScreens.add(currentScreen.value)
            }

            currentScreen.value = route
        }
    }

    // Function to handle the back
    fun navigateBack() {
        if (backStackScreens.isNotEmpty()) {
            currentScreen.value = backStackScreens.last()
            backStackScreens.remove(currentScreen.value)
        }
    }
}

/**
 * Composable to remember the state of the navcontroller
 */
@Composable
fun rememberNavController(
    startDestination: String,
    backStackScreens: MutableSet<String> = mutableSetOf()
): MutableState<NavController> = rememberSaveable {
    mutableStateOf(NavController(startDestination, backStackScreens))
}