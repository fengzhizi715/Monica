package cn.netdiscovery.monica.ui.controlpanel.cartoon

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.chooseImage
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

    val viewModel: CartoonViewModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
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
                        state.rawImage = BufferedImages.load(file)
                        state.currentImage = state.rawImage
                        state.rawImageFile = file
                    }
                },
                enabled = state.currentImage == null
            ) {
                if (state.currentImage == null) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = "请点击选择图像",
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
            subTitle(text = "请选择下列动漫风格", modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold)

            desktopLazyRow(modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(100.dp)) {
                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convertCartoon(state,1) {
                            showToast("算法服务异常")
                        }
                    }
                ) {
                    Text(
                        text = "宫崎骏风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convertCartoon(state,2) {
                            showToast("算法服务异常")
                        }
                    }
                ) {
                    Text(
                        text = "日系人像风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convertCartoon(state,3) {
                            showToast("算法服务异常")
                        }
                    }
                ) {
                    Text(
                        text = "黑白线稿", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convertCartoon(state,4) {
                            showToast("算法服务异常")
                        }
                    }
                ) {
                    Text(
                        text = "新海诚风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }

                Card(
                    elevation = 16.dp,
                    modifier = Modifier.fillMaxSize().padding(start = 5.dp).clickable{

                        viewModel.convertCartoon(state,5) {
                            showToast("算法服务异常")
                        }
                    }
                ) {
                    Text(
                        text = "可爱风格", fontSize = 22.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.width(200.dp).wrapContentSize(Alignment.Center)
                    )
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {

            toolTipButton(text = "删除",
                painter = painterResource("images/preview/delete.png"),
                iconModifier = Modifier.size(36.dp),
                onClick = {
                    state.clearImage()
                })

            toolTipButton(text = "上一步",
                painter = painterResource("images/doodle/previous_step.png"),
                onClick = {
                    state.getLastImage()?.let {
                        state.currentImage = it
                    }
                })

            toolTipButton(text = "保存",
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