package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.toolTipButton
import org.koin.compose.koinInject
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapView
 * @author: Tony Shen
 * @date: 2024/8/25 13:02
 * @version: V1.0 <描述当前版本功能>
 */
typealias OnImageChange = (image: BufferedImage) -> Unit

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun faceSwap(state: ApplicationState) {

    val viewModel: FaceSwapModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row (
                modifier = Modifier.fillMaxSize().padding(top= 20.dp, bottom = 20.dp, end = 90.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.padding(10.dp).weight(1.0f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {
                        viewModel.chooseImage(state) { file ->
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
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "source",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Image(
                                painter = state.currentImage!!.toPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier)
                        }
                    }
                }

                Card(
                    modifier = Modifier.padding(10.dp).weight(1.0f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {
                        viewModel.chooseImage(state) { file ->
                            viewModel.targetImage = BufferedImages.load(file)
                        }
                    },
                    enabled = viewModel.targetImage == null
                ) {
                    if (viewModel.targetImage == null) {
                        Text(
                            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                            text = "请点击选择图像",
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "target",
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colors.primary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Image(
                                painter = viewModel.targetImage!!.toPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier)
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.align(Alignment.CenterEnd)
            .padding(start = 10.dp, end = 10.dp)
            .background(color = Color.LightGray, shape = RoundedCornerShape(15))) {
            Column(
                Modifier.padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                toolTipButton(text = "删除 source 的图",
                    painter = painterResource("images/preview/delete.png"),
                    iconModifier = Modifier.size(30.dp),
                    onClick = {
                        state.clearImage()
                    })

                toolTipButton(text = "检测 source 图中的人脸",
                    painter = painterResource("images/ai/face_landmark.png"),
                    iconModifier = Modifier.size(30.dp),
                    onClick = {
                        viewModel.faceLandMark(state, state.currentImage) {
                            state.currentImage = it
                        }
                    })

                toolTipButton(text = "删除 target 的图",
                    painter = painterResource("images/preview/delete.png"),
                    iconModifier = Modifier.size(30.dp),
                    onClick = {
                        viewModel.clearTargetImage()
                    })

                toolTipButton(text = "检测 target 图中的人脸",
                    painter = painterResource("images/ai/face_landmark.png"),
                    iconModifier = Modifier.size(30.dp),
                    onClick = {
                        viewModel.faceLandMark(state, viewModel.targetImage) {
                            viewModel.targetImage = it
                        }
                    })
            }
        }
    }
}