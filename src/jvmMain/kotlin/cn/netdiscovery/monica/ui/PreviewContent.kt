package cn.netdiscovery.monica.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.currentTime
import cn.netdiscovery.monica.utils.getUniqueFile
import cn.netdiscovery.monica.utils.saveImage
import cn.netdiscovery.monica.utils.showFileSelector
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JFileChooser


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.PreviewContent
 * @author: Tony Shen
 * @date: 2024/4/26 11:09
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PreviewContent(
    state: ApplicationState,
    modifier: Modifier
) {
    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        onClick = {
            state.onClickImageChoose()
        },
        enabled = state.rawImage == null
    ) {
        if (state.rawImage == null) {
            chooseImage()
        } else {
            previewImage(state)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun previewImage(state: ApplicationState) {
    if (state.currentImage == null) return

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().weight(9f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = state.currentImage!!.toPainter(),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .pointerInput(Unit) {
                        val width = this.size.width
                        val height = this.size.height

                        detectTapGestures(
                            onPress = {
                                if (state.isMosaic) {
                                    state.mosaic(width,height, it)
                                }
                            })
                    }
                    .combinedClickable(onLongClick = {
                        // perform long click operations
                    }, onDoubleClick = {
                        // perform double click operations
                    }, onClick = {
                        if (state.isBasic) {
                            state.togglePreviewWindow(false)
                        } else {
                            state.togglePreviewWindow(true)
                        }
                    })
                    .drawWithContent {
                    drawContent()

                    })
        }

        Row (
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = { state.currentImage = state.rawImage }
            ) {
                Icon(
                    painter = painterResource("initial_picture.png"),
                    contentDescription = "最初",
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    if (state.lastImage!=null)
                        state.currentImage = state.lastImage
                }
            ) {
                Icon(
                    painter = painterResource("reduction.png"),
                    contentDescription = "上一步",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    state.clearImage()
                }
            ) {
                Icon(
                    painter = painterResource("delete.png"),
                    contentDescription = "删除",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    state.onClickPreviewImage()
                }
            ) {
                Icon(
                    painter = painterResource("preview.png"),
                    contentDescription = "预览",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    showFileSelector(
                        isMultiSelection = false,
                        selectionMode = JFileChooser.DIRECTORIES_ONLY,
                        selectionFileFilter = null
                    ) {
                        state.scope.launch {
                            val outputPath = it[0].absolutePath
                            val saveFile = File(outputPath).getUniqueFile(state.rawImageFile?:File("${currentTime()}.jpg"))
                            state.currentImage!!.saveImage(saveFile, 0.8f)
                            state.showTray(msg = "保存成功（${outputPath}）")
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource("save.png"),
                    contentDescription = "保存",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }

}

@Composable
private fun chooseImage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "请点击选择图像或拖拽图像至此",
            textAlign = TextAlign.Center
        )
    }
}