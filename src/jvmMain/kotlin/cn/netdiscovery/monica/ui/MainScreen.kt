package cn.netdiscovery.monica.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.dropFileTarget
import cn.netdiscovery.monica.utils.legalSuffixList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.imageio.ImageIO

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.MainScreen
 * @author: Tony Shen
 * @date: 2024/4/26 10:54
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun MainScreen(
    state: ApplicationState
) {
    state.window.contentPane.dropTarget = dropFileTarget {
        state.scope.launch(Dispatchers.IO) {
            val filePath = it.getOrNull(0)
            if (filePath != null) {
                val file = File(filePath)
                if (file.isFile && file.extension in legalSuffixList) {
                    state.rawImg = ImageIO.read(file)
                    state.showImg = state.rawImg
                    state.rawImgFile = file
                }
            }
        }
    }

    MaterialTheme {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            PreviewContent(state, Modifier.weight(1.4f))
            ControlContent(state, Modifier.weight(0.6f))
        }


        if (state.dialogMsg.isNotBlank()) {
            CommonDialog(state)
        }
    }
}

@Composable
fun CommonDialog(state: ApplicationState) {
    Dialog(
        onCloseRequest = { state.onDialogCloseRequest?.invoke() },
        title = state.dialogTitle,
        resizable = false
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = state.dialogMsg,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                maxLines = 3,
                modifier = Modifier.padding(
                    top = 12.dp, bottom = 25.dp,
                    start = 20.dp, end = 20.dp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Divider()
            Row {
                if (state.dialogCancelBtnText.isNotBlank()) {
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        onClick = {
                            state.onDialogCloseRequest?.invoke()
                        }
                    ) {
                        Text(
                            text = state.dialogCancelBtnText,
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Color(red = 53, green = 128, blue = 186)
                        )
                    }
                }
                if (state.dialogCancelBtnText.isNotBlank() && state.dialogSureBtnText.isNotBlank()) {
                    Divider(
                        modifier = Modifier
                            .width(1.dp)
                            .height(45.dp)
                    )
                }
                if (state.dialogSureBtnText.isNotBlank()) {
                    TextButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp),
                        onClick = {
                            state.onDialogSure?.invoke()
                        }
                    ) {
                        Text(
                            text = state.dialogSureBtnText,
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = Color(red = 53, green = 128, blue = 186)
                        )
                    }
                }
            }
        }
    }
}