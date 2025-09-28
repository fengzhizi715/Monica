package cn.netdiscovery.monica.exception.handlers

import cn.netdiscovery.monica.exception.*

/**
 * AI服务错误处理器
 * @FileName:
 *          cn.netdiscovery.monica.exception.handlers.AIServiceErrorHandler
 * @author: Tony Shen
 * @date: 2025/9/28 10:30
 * @version: V1.0 <描述当前版本功能>
 */
class AIServiceErrorHandler : ErrorHandler {
    
    override fun handleError(error: AppError): ErrorHandlingStrategy {
        return when (error.severity) {
            ErrorSeverity.LOW -> ErrorHandlingStrategy.SHOW_TOAST
            ErrorSeverity.MEDIUM -> ErrorHandlingStrategy.SHOW_DIALOG
            ErrorSeverity.HIGH -> if (error.retryable) ErrorHandlingStrategy.RETRY else ErrorHandlingStrategy.SHOW_DIALOG
            ErrorSeverity.CRITICAL -> ErrorHandlingStrategy.FALLBACK
        }
    }
    
    override fun canHandle(errorType: ErrorType): Boolean = 
        errorType == ErrorType.AI_SERVICE_ERROR
}
