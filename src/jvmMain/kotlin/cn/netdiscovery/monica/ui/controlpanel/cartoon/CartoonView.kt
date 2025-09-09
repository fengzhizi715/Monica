package cn.netdiscovery.monica.ui.controlpanel.cartoon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import loadingDisplay
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.cartoon.CartoonView
 * @author: Tony Shen
 * @date: 2025/4/16 17:32
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

private var showToast by mutableStateOf(false)
private var toastMessage by mutableStateOf("")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun cartoon(state: ApplicationState) {
    val i18nState = rememberI18nState()
    val viewModel: CartoonViewModel = koinInject()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {

        Row (
            modifier = Modifier.fillMaxSize().padding(bottom = 160.dp, end = 90.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxSize().padding(10.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                    chooseImage(state) { file ->
                        state.rawImage = getBufferedImage(file, state)
                        state.currentImage = state.rawImage
                        state.rawImageFile = file
                    }
                },
                enabled = state.currentImage == null
            ) {
                if (state.currentImage == null) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = i18nState.getString("click_to_select_image"),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                } else {
                    Image(
                        painter = state.currentImage!!.toPainter(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 160.dp).align(Alignment.BottomStart)) {
            subTitle(text = i18nState.getString("select_anime_style"), modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)

            desktopLazyRow(modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(100.dp)) {
                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convert2Cartoon(state,1) {
                            showToast(i18nState.getString("algorithm_service_error"))
                        }
                    }
                ) {
                    Text(
                        text = i18nState.getString("miyazaki_style"), fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convert2Cartoon(state,2) {
                            showToast(i18nState.getString("algorithm_service_error"))
                        }
                    }
                ) {
                    Text(
                        text = i18nState.getString("japanese_portrait_style"), fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convert2Cartoon(state,3) {
                            showToast(i18nState.getString("algorithm_service_error"))
                        }
                    }
                ) {
                    Text(
                        text = i18nState.getString("black_white_line_art"), fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convert2Cartoon(state,4) {
                            showToast(i18nState.getString("algorithm_service_error"))
                        }
                    }
                ) {
                    Text(
                        text = i18nState.getString("shinkai_style"), fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convert2Cartoon(state,5) {
                            showToast(i18nState.getString("algorithm_service_error"))
                        }
                    }
                ) {
                    Text(
                        text = i18nState.getString("cute_style"), fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {

            toolTipButton(text = i18nState.getString("delete"),
                painter = painterResource("images/preview/delete.png"),
                iconModifier = Modifier.size(36.dp),
                onClick = {
                    state.clearImage()
                })

            toolTipButton(text = i18nState.getString("previous_step"),
                painter = painterResource("images/doodle/previous_step.png"),
                onClick = {
                    state.getLastImage()?.let {
                        state.currentImage = it
                    }
                })

            toolTipButton(text = i18nState.getString("save"),
                painter = painterResource("images/doodle/save.png"),
                onClick = {
                    state.closePreviewWindow()
                })
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showToast) {
            centerToast(message = toastMessage) {
                showToast = false
            }
        }
    }
}

private fun showToast(message: String) {
    toastMessage = message
    showToast = true
}