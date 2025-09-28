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

    fun showDialog(title: String, message: String, onDismiss: () -> Unit = {}) {
        _dialogState.value = DialogState(title, message, onDismiss)
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun clearDialog() {
        _dialogState.value = null
    }
}