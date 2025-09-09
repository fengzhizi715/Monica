package cn.netdiscovery.monica.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.utils.Action
import picUrl

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.main.Dialogs
 * @author: Tony Shen
 * @date: 2025/4/17 11:57
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 加载网络图片的对话框
 */
@Composable
fun openURLDialog(onConfirm: Action, onDismiss: Action) {
    val i18nState = rememberI18nState()
    
    AlertDialog(
        modifier = Modifier.width(600.dp).height(200.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(text = i18nState.getString("load_network_image_dialog"))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = picUrl,
                    onValueChange = { picUrl = it }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm.invoke()
                }
            ) {
                Text(i18nState.getString("confirm"))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss.invoke()
                }
            ) {
                Text(i18nState.getString("cancel"))
            }
        }
    )
}

@Composable
fun showVersionInfo(onClick: Action) {
    val i18nState = rememberI18nState()
    
    AlertDialog(onDismissRequest = {},
        title = {
            Text(i18nState.getString("monica_software_info"))
        },
        text = {
            Column {
                val versionInfo = if (isProVersion) i18nState.getString("pro_version") else i18nState.getString("test_version")
                Text(i18nState.getString("monica_version_info", appVersion, versionInfo, buildTime))
                Text("OS: $os, $osVersion, $arch")
                Text("JDK: $javaVersion, $javaVendor")
                Text("Kotlin: $kotlinVersion, Compose Desktop: $composeVersion")
                Text(i18nState.getString("opencv_version_info", openCVVersion, imageProcessVersion))
                Text(i18nState.getString("copyright_info"))
                Text("Wechat: fengzhizi715")
                Text(i18nState.getString("github_url"))
            }
        },
        confirmButton = {
            Button(onClick = {
                onClick.invoke()
            }) {
                Text(i18nState.getString("close"))
            }
        })
}