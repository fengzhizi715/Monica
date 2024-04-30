package cn.netdiscovery.monica.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState

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
            state.onClickImgChoose()
        },
        enabled = state.rawImage == null
    ) {
        if (state.rawImage == null) {
            chooseImg()
        } else {
            previewImage(state)
        }
    }
}

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
                modifier = Modifier.drawWithContent {
                    drawContent()
                    if (state.isShowGuideLine) {
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(x = 0f, y = size.height * state.topPercent),
                            end = Offset(x = size.width, y = size.height * state.topPercent),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Cyan,
                            start = Offset(x = 0f, y = size.height * state.topPercent),
                            end = Offset(x = size.width, y = size.height * state.topPercent)
                        )
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(x = 0f, y = size.height * (1f - state.bottomPercent)),
                            end = Offset(x = size.width, y = size.height * (1f - state.bottomPercent)),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Cyan,
                            start = Offset(x = 0f, y = size.height * (1f - state.bottomPercent)),
                            end = Offset(x = size.width, y = size.height * (1f - state.bottomPercent))
                        )
                    }
                }.clickable {
                    state.togglePreviewWindow(true)
                },
            )
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
                    painter = painterResource("reduction.png"),
                    contentDescription = "还原",
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                modifier = Modifier.padding(5.dp),
                onClick = {
                    state.rawImage = null
                    state.currentImage = null
                    state.rawImageFile = null }
            ) {
                Icon(
                    painter = painterResource("delete.png"),
                    contentDescription = "删除",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }

}

@Composable
private fun chooseImg() {
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