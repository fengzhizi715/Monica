package cn.netdiscovery.monica.exception

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.exception.Errors
 * @author: Tony Shen
 * @date: 2025/9/26 17:12
 * @version: V1.0 <描述当前版本功能>
 */
// 错误类型枚举
enum class ErrorType {
    NETWORK_ERROR,      // 网络错误
    IMAGE_PROCESSING,   // 图像处理错误
    VALIDATION_ERROR,   // 验证错误
    FILE_IO_ERROR,      // 文件IO错误
    CONFIG_ERROR,       // 配置错误
    AI_SERVICE_ERROR,   // AI服务错误
    UI_ERROR,           // UI错误
    UNKNOWN_ERROR       // 未知错误
}

// 错误严重程度
enum class ErrorSeverity {
    LOW,        // 低严重程度，不影响主要功能
    MEDIUM,     // 中等严重程度，影响部分功能
    HIGH,       // 高严重程度，影响主要功能
    CRITICAL    // 严重错误，可能导致应用崩溃
}

// 错误处理策略
enum class ErrorHandlingStrategy {
    SHOW_TOAST,         // 显示Toast提示
    SHOW_DIALOG,        // 显示错误对话框
    LOG_ONLY,           // 仅记录日志
    RETRY,              // 自动重试
    FALLBACK,           // 降级处理
    IGNORE              // 忽略错误
}

// 统一错误结果
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val error: AppError) : Result<T>()
}