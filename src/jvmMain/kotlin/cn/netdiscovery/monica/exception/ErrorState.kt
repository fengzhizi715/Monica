package cn.netdiscovery.monica.exception

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 错误状态管理
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorState
 * @author: Tony Shen
 * @date: 2025/9/28 10:17
 * @version: V1.0 <描述当前版本功能>
 */
class ErrorState {
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _topToastMessage = MutableStateFlow<String?>(null)
    val topToastMessage: StateFlow<String?> = _topToastMessage.asStateFlow()

    private val _dialogState = MutableStateFlow<DialogState?>(null)
    val dialogState: StateFlow<DialogState?> = _dialogState.asStateFlow()

    data class DialogState(
        val title: String,
        val message: String,
        val onDismiss: () -> Unit
    )

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun showTopToast(message: String) {
        _topToastMessage.value = message
    }

    fun showDialog(title: String, message: String, onDismiss: () -> Unit = {}) {
        _dialogState.value = DialogState(title, message, onDismiss)
    }

    fun clearToast() {
        _toastMessage.value = null
        _topToastMessage.value = null
    }

    fun clearDialog() {
        _dialogState.value = null
    }
    
    /**
     * 直接显示错误 - 用于在非 Composable 上下文中调用
     */
    fun showError(error: AppError) {
        when (error.severity) {
            ErrorSeverity.LOW -> {
                showTopToast(error.userMessage)
            }
            ErrorSeverity.MEDIUM -> {
                showToast(error.userMessage)
            }
            ErrorSeverity.HIGH -> {
                showDialog("错误", error.userMessage)
            }
            ErrorSeverity.CRITICAL -> {
                showDialog("严重错误", error.userMessage)
            }
        }
    }
}