package cn.netdiscovery.monica.exception

/**
 * 错误处理扩展函数
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorExtensions
 * @author: Tony Shen
 * @date: 2025/9/28 10:35
 * @version: V1.0 <描述当前版本功能>
 */
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
        GlobalErrorManager.getInstance()?.handleError(error) ?: run {
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
        GlobalErrorManager.getInstance()?.handleError(error) ?: run {
            // 如果全局错误管理器未初始化，仅记录日志
            println("错误: ${error.message}")
        }
        Result.Error(error)
    }
}