package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.divider
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
    val i18nState = rememberI18nState()
    val viewModel: HistoryViewModel = koinInject()

    val historyEntries = remember { mutableStateListOf<HistoryEntry>() }

    LaunchedEffect(Unit) {
        historyEntries.clear()
        historyEntries.addAll(viewModel.getOperationLog())
    }

    Column (modifier = Modifier.fillMaxSize().padding(start = 20.dp, end =  20.dp)) {
        title(modifier = Modifier.align(Alignment.CenterHorizontally), text = title, color = Color.Black)

        CVHistoryList(historyEntries, i18nState)
    }
}

@Composable
fun CVHistoryList(history: List<HistoryEntry>, i18nState: cn.netdiscovery.monica.ui.i18n.I18nState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(history) { entry ->
                HistoryItem(entry, i18nState)
                divider()
            }
        }
    }
}

@Composable
fun HistoryItem(entry: HistoryEntry, i18nState: cn.netdiscovery.monica.ui.i18n.I18nState) {
    Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
        Text(
            text = "${i18nState.getString("operation")}: ${entry.operation}",
        )
        Text(
            text = "${i18nState.getString("time")}: ${formatTimestamp.format(Date(entry.timestamp))}",
        )
        Text(
            text = "${i18nState.getString("parameters")}: ${entry.parameters.entries.joinToString { "${it.key}=${it.value}" }}",
            maxLines = 6,
            overflow = TextOverflow.Ellipsis
        )
        if (entry.description.isNotEmpty()) {
            Text(
                text = "${i18nState.getString("description")}: ${entry.description}"
            )
        }
    }
}