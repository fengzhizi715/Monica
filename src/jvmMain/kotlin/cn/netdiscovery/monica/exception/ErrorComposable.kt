package cn.netdiscovery.monica.exception

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.ui.widget.topToast

/**
 * 错误处理Compose组件
 * @FileName:
 *          cn.netdiscovery.monica.exception.ErrorComposable
 * @author: Tony Shen
 * @date: 2025/9/28 10:40
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ErrorHandler(
    errorState: ErrorState
) {
    // 初始化错误管理器
    val errorManager = remember { 
        ErrorManager().apply {
            setErrorState(errorState)
            // 注册默认错误处理器
            registerHandler(cn.netdiscovery.monica.exception.handlers.NetworkErrorHandler())
            registerHandler(cn.netdiscovery.monica.exception.handlers.ImageProcessingErrorHandler())
            registerHandler(cn.netdiscovery.monica.exception.handlers.ValidationErrorHandler())
            registerHandler(cn.netdiscovery.monica.exception.handlers.FileIOErrorHandler())
            registerHandler(cn.netdiscovery.monica.exception.handlers.AIServiceErrorHandler())
        }
    }
    
    // 设置全局错误管理器
    LaunchedEffect(errorManager) {
        GlobalErrorManager.setInstance(errorManager, errorState)
    }
    
    // 监听 Toast 消息
    val toastMessage by errorState.toastMessage.collectAsState()
    // 显示 Toast
    if (toastMessage != null) {
        centerToast(
            modifier = Modifier, 
            message = toastMessage!!,
            onDismissCallback = {
                errorState.clearToast()
            }
        )
    }

    // 监听 Top Toast 消息
    val topToastMessage by errorState.topToastMessage.collectAsState()
    // 显示 Top Toast
    if (topToastMessage != null) {
        topToast(
            modifier = Modifier,
            message = topToastMessage!!,
            onDismissCallback = {
                errorState.clearToast()
            }
        )
    }

    // 监听对话框状态
    val dialogState by errorState.dialogState.collectAsState()
    dialogState?.let { state ->
        AlertDialog(
            onDismissRequest = { 
                errorState.clearDialog()
                state.onDismiss()
            },
            title = { Text(state.title) },
            text = { Text(state.message) },
            confirmButton = {
                TextButton(onClick = { 
                    errorState.clearDialog()
                    state.onDismiss()
                }) {
                    Text("确定")
                }
            }
        )
    }
}
