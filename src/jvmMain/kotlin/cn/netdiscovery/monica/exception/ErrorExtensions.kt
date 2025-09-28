package cn.netdiscovery.monica.exception

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 错误处理扩展函数
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorExtensions
 * @author: Tony Shen
 * @date: 2025/9/28 10:35
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 全局错误处理函数 - 用于在非 Composable 上下文中调用
 */
fun showError(
    type: ErrorType,
    severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    message: String = "操作失败，请重试",
    userMessage: String = "操作失败，请重试"
) {
    val error = AppError(
        type = type,
        severity = severity,
        message = message,
        userMessage = userMessage
    )
    val errorState = GlobalErrorManager.getErrorState()
    if (errorState != null) {
        errorState.showError(error)
    } else {
        // 如果 GlobalErrorManager 未初始化，直接打印日志
        println("错误: ${error.userMessage}")
    }
}

/**
 * 安全执行IO操作
 */
suspend fun <T> safeExecuteIO(
    errorType: ErrorType = ErrorType.FILE_IO_ERROR,
    severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    userMessage: String = "文件操作失败，请重试",
    retryable: Boolean = true,
    context: Map<String, Any> = emptyMap(),
    block: suspend () -> T
): Result<T> {
    return withContext(Dispatchers.IO) {
        safeExecute(errorType, severity, userMessage, retryable, context, block)
    }
}
/**
 * 安全执行异步操作
 */
suspend fun <T> safeExecute(
    errorType: ErrorType,
    severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    userMessage: String = "操作失败，请重试",
    retryable: Boolean = false,
    context: Map<String, Any> = emptyMap(),
    block: suspend () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        val error = AppError(
            type = errorType,
            severity = severity,
            message = e.message ?: "未知错误",
            userMessage = userMessage,
            cause = e,
            retryable = retryable,
            context = context
        )
        GlobalErrorManager.getErrorState()?.showError(error) ?: run {
            // 如果全局错误管理器未初始化，仅记录日志
            println("错误: ${error.message}")
        }
        Result.Error(error)
    }
}

/**
 * 安全执行同步操作
 */
fun <T> safeExecuteSync(
    errorType: ErrorType,
    severity: ErrorSeverity = ErrorSeverity.MEDIUM,
    userMessage: String = "操作失败，请重试",
    retryable: Boolean = false,
    context: Map<String, Any> = emptyMap(),
    block: () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: Exception) {
        val error = AppError(
            type = errorType,
            severity = severity,
            message = e.message ?: "未知错误",
            userMessage = userMessage,
            cause = e,
            retryable = retryable,
            context = context
        )
        GlobalErrorManager.getErrorState()?.showError(error) ?: run {
            // 如果全局错误管理器未初始化，仅记录日志
            println("错误: ${error.message}")
        }
        Result.Error(error)
    }
}