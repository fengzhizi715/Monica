package cn.netdiscovery.monica.exception

/**
 * 应用错误类
 * @FileName:
 *          cn.netdiscovery.monica.exception.AppError
 * @author: Tony Shen
 * @date: 2025/9/26 17:20
 * @version: V1.0 <描述当前版本功能>
 */
data class AppError(
    val type: ErrorType,
    val severity: ErrorSeverity,
    val message: String,                              // 技术性错误信息，用于日志
    val userMessage: String,                          // 用户友好的错误信息
    val cause: Throwable? = null,                     // 原始异常
    val retryable: Boolean = false,                   // 是否可重试
    val context: Map<String, Any> = emptyMap(),       // 错误上下文信息
    val timestamp: Long = System.currentTimeMillis(), // 错误发生时间
    val errorCode: String? = null                     // 错误代码，用于国际化
) {
    companion object {
        fun fromException(
            exception: Throwable,
            type: ErrorType,
            severity: ErrorSeverity = ErrorSeverity.MEDIUM,
            userMessage: String = "操作失败，请重试"
        ): AppError {
            return AppError(
                type = type,
                severity = severity,
                message = exception.message ?: "未知错误",
                userMessage = userMessage,
                cause = exception,
                retryable = isRetryableException(exception)
            )
        }

        private fun isRetryableException(exception: Throwable): Boolean {
            return when (exception) {
                is java.net.SocketTimeoutException,
                is java.net.ConnectException,
                is java.io.IOException -> true
                else -> false
            }
        }
    }
}