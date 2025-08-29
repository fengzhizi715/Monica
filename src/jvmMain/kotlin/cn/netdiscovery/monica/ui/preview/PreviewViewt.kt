package cn.netdiscovery.monica.ui.preview

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import cn.netdiscovery.monica.state.BlurStatus
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.state.MosaicStatus
import cn.netdiscovery.monica.state.ZoomPreviewStatus
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.PreviewView
 * @author: Tony Shen
 * @date: 2024/4/26 11:09
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun preview(
    state: ApplicationState,
    modifier: Modifier
) {
    val viewModel: PreviewViewModel = koinInject()

    Card(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        onClick = {
            chooseImage(state) { file ->
                val image = getBufferedImage(file, state)
                state.rawImage = image
                state.currentImage = state.rawImage
                state.rawImageFile = file
            }
        },
        enabled = state.rawImage == null
    ) {
        if (state.rawImage == null) {
            chooseImage()
        } else {
            previewImage(state,viewModel)
        }
    }
}

@Composable
private fun previewImage(state: ApplicationState, viewModel: PreviewViewModel) {
    val i18nState = rememberI18nState()
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
                                if (state.currentStatus == MosaicStatus) {
                                    viewModel.mosaic(width, height, it, state)
                                } else if (state.currentStatus == BlurStatus) {
                                    viewModel.blur(width,height, it, state)
                                }
                            })
                    }
                    .drawWithContent {
                    drawContent()
                    })
        }

        Row (
            modifier = Modifier.fillMaxSize().weight(1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
                            toolTipButton(text = i18nState.getString("restore_original"),
                painter = painterResource("images/preview/initial_picture.png"),
                iconModifier = Modifier.size(30.dp),
                onClick = {
                    viewModel.recoverImage(state)
                })

                            toolTipButton(text = i18nState.getString("previous_step"),
                painter = painterResource("images/preview/reduction.png"),
                onClick = {
                    viewModel.getLastImage(state)
                })

                            toolTipButton(text = i18nState.getString("enlarge_preview"),
                painter = painterResource("images/preview/zoom.png"),
                onClick = {
                    state.togglePreviewWindowAndUpdateStatus(ZoomPreviewStatus)
                })

                            toolTipButton(text = i18nState.getString("save"),
                painter = painterResource("images/preview/save.png"),
                onClick = {
                    viewModel.saveImage(state)
                })

                            toolTipButton(text = i18nState.getString("delete"),
                painter = painterResource("images/preview/delete.png"),
                onClick = {
                    viewModel.clearImage(state)
                })
        }
    }
}

@Composable
private fun chooseImage() {
    val i18nState = rememberI18nState()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = i18nState.getString("click_to_select_image_or_drag"),
            textAlign = TextAlign.Center
        )
    }
}
