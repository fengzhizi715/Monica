package cn.netdiscovery.monica.exception.handlers

import cn.netdiscovery.monica.exception.*

/**
 * 验证错误处理器
 * @FileName:
 *          cn.netdiscovery.monica.exception.handlers.ValidationErrorHandler
 * @author: Tony Shen
 * @date: 2025/9/28 10:30
 * @version: V1.0 <描述当前版本功能>
 */
class ValidationErrorHandler : ErrorHandler {
    
    override fun handleError(error: AppError): ErrorHandlingStrategy {
        return when (error.severity) {
            ErrorSeverity.LOW -> ErrorHandlingStrategy.SHOW_TOAST
            ErrorSeverity.MEDIUM -> ErrorHandlingStrategy.SHOW_TOAST
            ErrorSeverity.HIGH -> ErrorHandlingStrategy.SHOW_DIALOG
            ErrorSeverity.CRITICAL -> ErrorHandlingStrategy.SHOW_DIALOG
        }
    }
    
    override fun canHandle(errorType: ErrorType): Boolean = 
        errorType == ErrorType.VALIDATION_ERROR
}
