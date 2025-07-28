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

    private val undoStack = ArrayDeque<T>()
    private val redoStack = ArrayDeque<T>()

    private var debounceJob: Job? = null
    private val debounceDelayMillis = 300L

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }

    fun pushState(state: T) {
        if (undoStack.size >= maxHistorySize) {
            undoStack.removeFirst()
        }
        undoStack.addLast(state)
        redoStack.clear()
    }

    /**
     * 防抖版本的 pushState，避免在频繁拖动过程中记录太多中间状态。
     */
    fun pushStateDebounced(state: T) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(debounceDelayMillis)
            withContext(Dispatchers.Main) {
                pushState(state)
            }
        }
    }

    /**
     * 撤销
     */
    fun undo(currentState: T): T? {
        if (canUndo) {
            val previous = undoStack.removeLast()
            redoStack.addLast(currentState)
            return previous
        }
        return null
    }

    /**
     * 重做
     */
    fun redo(currentState: T): T? {
        if (canRedo) {
            val next = redoStack.removeLast()
            undoStack.addLast(currentState)
            return next
        }
        return null
    }

    fun peekUndo(): T? = undoStack.lastOrNull()
    fun peekRedo(): T? = redoStack.lastOrNull()
}