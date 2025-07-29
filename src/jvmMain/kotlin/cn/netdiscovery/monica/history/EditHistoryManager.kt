package cn.netdiscovery.monica.history

import cn.netdiscovery.monica.utils.logger
import kotlinx.coroutines.*
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.EditHistoryManager
 * @author: Tony Shen
 * @date: 2025/7/28 13:40
 * @version: V1.0 管理每个编辑会话的历史记录栈，包括撤销和重做功能。
 */
class EditHistoryManager<T>(private val maxHistorySize: Int = 20,
                            private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    private val logger: Logger = logger<EditHistoryManager<T>>()

    private val undoStack = ArrayDeque<Pair<T, HistoryEntry>>()
    private val redoStack = ArrayDeque<Pair<T, HistoryEntry>>()
    private val operationLog = mutableListOf<HistoryEntry>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    private var debounceJob: Job? = null
    private val debounceDelayMillis = 300L

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

    fun push(state: T, entry: HistoryEntry) {
        if (undoStack.size >= maxHistorySize) {
            undoStack.removeFirst()
        }
        undoStack.addLast(state to entry)
        redoStack.clear()
    }

    /**
     * 只记录真正的操作, 便于后续支持视图或者全局的历史视图
     */
    fun logOnly(entry: HistoryEntry) {
        operationLog.add(entry)
        if (operationLog.size > maxHistorySize) {
            operationLog.removeAt(0)
        }
    }

    fun getOperationLog(): List<HistoryEntry> = operationLog.toList()

    /**
     * 防抖版本的 push，避免在频繁操作过程中记录太多中间状态。
     */
    fun pushDebouncedAsync(
        entry: HistoryEntry,
        block: suspend () -> T,
        onError: ((Throwable) -> Unit)? = null
    ) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(debounceDelayMillis)
            try {
                val state = block()
                withContext(Dispatchers.Main) {
                    push(state, entry)
                }
            } catch (e: CancellationException) {
                throw e // 正常传递取消
            } catch (e: Exception) {
                logger.error("pushDebouncedAsync failed: ${e.message}", e)
                onError?.invoke(e)
            }
        }
    }

    /**
     * 撤销
     */
    fun undo(currentState: T, currentEntry: HistoryEntry): Pair<T, HistoryEntry>? {
        if (canUndo) {
            val last = undoStack.removeLast()
            redoStack.addLast(currentState to currentEntry)
            return last
        }
        return null
    }


    /**
     * 重做
     */
    fun redo(currentState: T, currentEntry: HistoryEntry): Pair<T, HistoryEntry>? {
        if (canRedo) {
            val next = redoStack.removeLast()
            undoStack.addLast(currentState to currentEntry)
            return next
        }
        return null
    }

    fun peekUndoEntry(): HistoryEntry? = undoStack.lastOrNull()?.second
    fun peekRedoEntry(): HistoryEntry? = redoStack.lastOrNull()?.second
}