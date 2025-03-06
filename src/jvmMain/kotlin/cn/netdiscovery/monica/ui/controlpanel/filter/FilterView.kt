package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.runtime.Composable
import cn.netdiscovery.monica.state.ApplicationState
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.filter.FilterView
 * @author: Tony Shen
 * @date: 2025/3/6 15:34
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun filter(state: ApplicationState) {

    val viewModel: FilterViewModel = koinInject()

}