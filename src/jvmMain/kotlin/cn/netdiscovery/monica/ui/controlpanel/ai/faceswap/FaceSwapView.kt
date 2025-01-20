package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.ThreeBallLoading
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.showLoading
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.chooseImage
import loadingDisplay
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapView
 * @author: Tony Shen
 * @date: 2024/8/25 13:02
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun faceSwap(state: ApplicationState) {

    val viewModel: FaceSwapViewModel = koinInject()

    val showSwapFaceSettings = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(false) }

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
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Box {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "source",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Image(
                                    painter = state.currentImage!!.toPainter(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                )
                            }

                            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                                toolTipButton(text = "上一步",
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    painter = painterResource("images/doodle/previous_step.png"),
                                    onClick = {
                                        viewModel.getLastSourceImage(state)
                                    })

                                toolTipButton(text = "检测 source 图中的人脸",
                                    painter = painterResource("images/ai/face_landmark.png"),
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    onClick = {
                                        viewModel.faceLandMark(state, state.currentImage) {
                                            state.addQueue(state.currentImage!!)
                                            state.currentImage = it
                                        }
                                    })

                                toolTipButton(text = "删除 source 的图",
                                    painter = painterResource("images/preview/delete.png"),
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    onClick = {
                                        state.clearImage()
                                    })
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier.padding(10.dp).weight(1.0f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {
                        chooseImage(state) { file ->
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
                        Box {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "target",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colors.primary,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Image(
                                    painter = viewModel.targetImage!!.toPainter(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier)
                            }

                            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                                toolTipButton(text = "上一步",
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    painter = painterResource("images/doodle/previous_step.png"),
                                    onClick = {

                                        if (viewModel.lastTargetImage!=null) {
                                            viewModel.targetImage = viewModel.lastTargetImage
                                        }
                                    })

                                toolTipButton(text = "检测 target 图中的人脸",
                                    painter = painterResource("images/ai/face_landmark.png"),
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    onClick = {
                                        viewModel.faceLandMark(state, viewModel.targetImage) {
                                            viewModel.lastTargetImage = viewModel.targetImage
                                            viewModel.targetImage = it
                                        }
                                    })

                                toolTipButton(text = "删除 target 的图",
                                    painter = painterResource("images/preview/delete.png"),
                                    buttonModifier = Modifier,
                                    iconModifier = Modifier.size(20.dp),
                                    onClick = {
                                        viewModel.clearTargetImage()
                                    })
                            }
                        }
                    }
                }
            }
        }

        rightSideMenuBar(modifier = Modifier.align(Alignment.CenterEnd)) {
            toolTipButton(text = "设置",
                painter = painterResource("images/cropimage/settings.png"),
                iconModifier = Modifier.size(36.dp),
                onClick = {
                    showSwapFaceSettings.value = true
                })

            toolTipButton(text = "人脸替换",
                painter = painterResource("images/ai/face_swap2.png"),
                iconModifier = Modifier.size(36.dp),
                onClick = {

                    if (state.currentImage!=null && viewModel.targetImage!=null) {
                        viewModel.faceSwap(state, state.currentImage, viewModel.targetImage, selectedOption.value) {
                            viewModel.lastTargetImage = viewModel.targetImage
                            viewModel.targetImage = it
                        }
                    }
                })

            toolTipButton(text = "保存结果",
                painter = painterResource("images/doodle/save.png"),
                iconModifier = Modifier.size(36.dp),
                onClick = {
                    if (viewModel.targetImage!=null) {
                        state.addQueue(state.currentImage!!)
                        state.currentImage = viewModel.targetImage
                        viewModel.clearTargetImage()
                    }
                    state.togglePreviewWindow(false)
                })
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showSwapFaceSettings.value) {
            AlertDialog(onDismissRequest = {},
                title = {
                    Text("替换 target 中人脸的数量")
                },
                text = {
                    Column {
                        Row {
                            RadioButton(
                                selected = !selectedOption.value,
                                onClick = { selectedOption.value = false }
                            )
                            Text("替换1个人脸", modifier = Modifier.align(Alignment.CenterVertically))
                        }

                        Row {
                            RadioButton(
                                selected = selectedOption.value,
                                onClick = { selectedOption.value = true }
                            )
                            Text("替换全部的人脸", modifier = Modifier.align(Alignment.CenterVertically))
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showSwapFaceSettings.value = false
                    }) {
                        Text("关闭")
                    }
                })
        }
    }
}