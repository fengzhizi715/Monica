package cn.netdiscovery.monica.exception

/**
 * 错误处理器接口
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorHandler
 * @author: Tony Shen
 * @date: 2025/9/26 17:15
 * @version: V1.0 <描述当前版本功能>
 */
interface ErrorHandler {

    fun handleError(error: AppError): ErrorHandlingStrategy

    fun canHandle(errorType: ErrorType): Boolean
}
