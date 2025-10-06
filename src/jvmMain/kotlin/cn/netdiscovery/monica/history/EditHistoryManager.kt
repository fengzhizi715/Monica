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
class EditHistoryManager<T>(
    private val maxHistorySize: Int = 20,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val logger: Logger = logger<EditHistoryManager<T>>()

    private val undoStack = ArrayDeque<Pair<T, HistoryEntry>>() // 历史状态栈
    private val redoStack = ArrayDeque<Pair<T, HistoryEntry>>() // 可重做的状态
    private val operationLog = mutableListOf<HistoryEntry>()     // 日志记录

    val canUndo: Boolean get() = undoStack.size > 1              // 至少有一个可撤销
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    private var debounceJob: Job? = null
    private val debounceDelayMillis = 300L

    /**
     * 清空所有历史记录
     */
    fun clear() {
        undoStack.clear()
        redoStack.clear()
        operationLog.clear()
    }

    /**
     * 记录一次新的编辑状态。
     * 新状态会成为当前状态，并清空 redo 栈。
     */
    fun push(state: T, entry: HistoryEntry) {
        // 避免重复状态 (例如连续相同参数的调色)
        val last = undoStack.lastOrNull()?.first
        if (last != null && last == state) {
            return
        }

        // 超出容量则移除最早的
        if (undoStack.size >= maxHistorySize) {
            undoStack.removeFirst()
        }

        undoStack.addLast(state to entry)
        redoStack.clear()
    }

    /**
     * 只记录操作日志（不会影响撤销栈）
     */
    fun logOnly(entry: HistoryEntry) {
        operationLog.add(entry)
        if (operationLog.size > maxHistorySize) {
            operationLog.removeAt(0)
        }
    }

    fun getOperationLog(): List<HistoryEntry> = operationLog.toList()

    /**
     * 防抖 push，避免频繁记录。
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
                throw e
            } catch (e: Exception) {
                logger.error("pushDebouncedAsync failed: ${e.message}", e)
                onError?.invoke(e)
            }
        }
    }

    /**
     * 撤销一次操作，返回撤销后的状态（上一个状态）。
     */
    fun undo(): Pair<T, HistoryEntry>? {
        if (canUndo) {
            val last = undoStack.removeLast()
            redoStack.addLast(last)
            return undoStack.lastOrNull()
        }
        return null
    }

    /**
     * 重做（恢复上一次撤销的状态）
     */
    fun redo(): Pair<T, HistoryEntry>? {
        if (canRedo) {
            val next = redoStack.removeLast()
            undoStack.addLast(next)
            return next
        }
        return null
    }

    /**
     * 查看上一个状态（不修改当前指针）
     */
    fun previousState(): Pair<T, HistoryEntry>? {
        return if (canUndo) {
            val iterator = undoStack.iterator()
            var prev: Pair<T, HistoryEntry>? = null
            while (iterator.hasNext()) {
                val current = iterator.next()
                if (!iterator.hasNext()) break // 到最后一个时退出
                prev = current
            }
            prev
        } else null
    }

    fun peekUndoEntry(): HistoryEntry? = undoStack.lastOrNull()?.second
    fun peekRedoEntry(): HistoryEntry? = redoStack.lastOrNull()?.second
}