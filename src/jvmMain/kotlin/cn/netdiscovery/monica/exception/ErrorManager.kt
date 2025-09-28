package cn.netdiscovery.monica.exception

import cn.netdiscovery.monica.utils.logger

/**
 * 错误管理器
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorManager
 * @author: Tony Shen
 * @date: 2025/9/26 17:32
 * @version: V1.0 <描述当前版本功能>
 */

// 全局错误管理器实例
object GlobalErrorManager {
    private var _instance: ErrorManager? = null

    fun setInstance(errorManager: ErrorManager) {
        _instance = errorManager
    }

    fun getInstance(): ErrorManager? = _instance
}

class ErrorManager {
    private val handlers = mutableListOf<ErrorHandler>()
    private val logger = logger<ErrorManager>()
    private var errorState: ErrorState? = null

    fun setErrorState(errorState: ErrorState) {
        this.errorState = errorState
    }

    fun registerHandler(handler: ErrorHandler) {
        handlers.add(handler)
    }

    fun handleError(error: AppError) {
        // 记录错误日志
        logError(error)

        // 查找合适的处理器
        val handler = handlers.find { it.canHandle(error.type) }

        // 根据处理策略更新状态
        when (handler?.handleError(error)) {
            ErrorHandlingStrategy.SHOW_TOAST -> {
                errorState?.showToast(error.userMessage)
            }
            ErrorHandlingStrategy.SHOW_DIALOG -> {
                errorState?.showDialog("错误", error.userMessage)
            }
            ErrorHandlingStrategy.RETRY -> {
                // 重试逻辑
            }
            ErrorHandlingStrategy.LOG_ONLY -> {
                // 仅记录日志
            }
            else -> logger.warn("未处理的错误: ${error.message}")
        }
    }

    private fun logError(error: AppError) {
        when (error.severity) {
            ErrorSeverity.LOW -> logger.debug("${error.type}: ${error.message}", error.cause)
            ErrorSeverity.MEDIUM -> logger.warn("${error.type}: ${error.message}", error.cause)
            ErrorSeverity.HIGH -> logger.error("${error.type}: ${error.message}", error.cause)
            ErrorSeverity.CRITICAL -> logger.error("严重错误 [${error.type}]: ${error.message}", error.cause)
        }
    }
}
