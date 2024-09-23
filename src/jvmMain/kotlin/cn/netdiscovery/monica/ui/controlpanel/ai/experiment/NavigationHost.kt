package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.runtime.Composable

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.NavigationHost
 * @author: Tony Shen
 * @date: 2024/9/23 20:06
 * @version: V1.0 <描述当前版本功能>
 */
class NavigationHost(
    val navController: NavController,
    val contents: @Composable NavigationGraphBuilder.() -> Unit
) {

    @Composable
    fun build() {
        NavigationGraphBuilder().renderContents()
    }

    inner class NavigationGraphBuilder(
        val navController: NavController = this@NavigationHost.navController
    ) {
        @Composable
        fun renderContents() {
            this@NavigationHost.contents(this)
        }
    }
}


/**
 * Composable to build the Navigation Host
 */
@Composable
fun NavigationHost.NavigationGraphBuilder.composable(
    route: String,
    content: @Composable () -> Unit
) {
    if (navController.currentScreen.value == route) {
        content()
    }
}