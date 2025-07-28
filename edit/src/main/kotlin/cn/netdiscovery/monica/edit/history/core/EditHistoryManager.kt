package cn.netdiscovery.monica.edit.history.core

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.edit.history.core.EditHistoryManager
 * @author: Tony Shen
 * @date: 2025/7/28 13:40
 * @version: V1.0 管理每个编辑会话的历史记录栈，包括撤销和重做功能。
 */
class EditHistoryManager<T>(private val maxHistorySize: Int = 20) {

    private val undoStack = ArrayDeque<T>()
    private val redoStack = ArrayDeque<T>()

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

    fun undo(currentState: T): T? {
        if (canUndo) {
            val previous = undoStack.removeLast()
            redoStack.addLast(currentState)
            return previous
        }
        return null
    }

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