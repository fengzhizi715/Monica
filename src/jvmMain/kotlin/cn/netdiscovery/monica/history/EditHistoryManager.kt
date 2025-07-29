package cn.netdiscovery.monica.history

import kotlinx.coroutines.*

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

    private val undoStack = ArrayDeque<Pair<T, HistoryEntry>>()
    private val redoStack = ArrayDeque<Pair<T, HistoryEntry>>()

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
     * 防抖版本的 push，避免在频繁操作过程中记录太多中间状态。
     */
    fun pushDebounced(state: T, entry: HistoryEntry) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(debounceDelayMillis)
            withContext(Dispatchers.Main) {
                push(state, entry)
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