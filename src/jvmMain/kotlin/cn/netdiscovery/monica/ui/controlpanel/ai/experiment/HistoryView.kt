package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.history.HistoryEntry
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.HistoryViewModel
import cn.netdiscovery.monica.ui.widget.title
import cn.netdiscovery.monica.utils.formatTimestamp
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Date

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.HistoryView
 * @author: Tony Shen
 * @date: 2025/7/30 09:32
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@Composable
fun history(state: ApplicationState, title: String) {
    val viewModel: HistoryViewModel = koinInject()

    val historyEntries = remember { mutableStateListOf<HistoryEntry>() }

    LaunchedEffect(Unit) {
        historyEntries.clear()
        historyEntries.addAll(viewModel.getOperationLog())
        logger.info("historyEntries size = ${historyEntries.size}")
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp, top = 10.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        CVHistoryList(historyEntries)
    }
}

@Composable
fun CVHistoryList(history: List<HistoryEntry>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history) { entry ->
                HistoryItem(entry)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun HistoryItem(entry: HistoryEntry) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = "操作: ${entry.operation}",
        )
        Text(
            text = "时间: ${formatTimestamp.format(Date(entry.timestamp))}",
        )
        Text(
            text = "参数: ${entry.parameters.entries.joinToString { "${it.key}=${it.value}" }}",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (entry.description.isNotEmpty()) {
            Text(
                text = "描述: ${entry.description}"
            )
        }
    }
}