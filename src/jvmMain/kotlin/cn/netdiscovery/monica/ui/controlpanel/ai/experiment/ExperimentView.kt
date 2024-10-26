package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.height
import cn.netdiscovery.monica.config.loadingWidth
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.ui.widget.ThreeBallLoading
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.composeClick
import loadingDisplay
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ExperimentView
 * @author: Tony Shen
 * @date: 2024/9/23 19:37
 * @version: V1.0 <描述当前版本功能>
 */

private var showVerifyToast by mutableStateOf(false)
private var verifyToastMessage by mutableStateOf("")

/**
 * Screens
 */
enum class Screen(
    val label: String,
    val resourcePath: String
) {
    Home(
        label = "首页",
        resourcePath = "images/ai/home.png"
    ),
    BinaryImage(
        label = "二值化",
        resourcePath = "images/ai/binary_image.png"
    ),
    EdgeDetection(
        label = "边缘检测",
        resourcePath = "images/ai/edge_detection.png"
    ),
    ContourAnalysis(
        label = "轮廓分析",
        resourcePath = "images/ai/contour_analysis.png"
    ),
    ImageConvolution(
        label = "图像卷积",
        resourcePath = "images/ai/image_convolution.png"
    ),
    MorphologicalOperations(
        label = "形态学操作",
        resourcePath = "images/ai/morphological_operations.png"
    ),
    TemplateMatching(
        label = "模版匹配",
        resourcePath = "images/ai/template_matching.png"
    )
}

@Composable
fun customNavigationHost(
    state: ApplicationState,
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Home.name) {
        }

        composable(Screen.BinaryImage.name) {
            binaryImage(state)
        }

        composable(Screen.EdgeDetection.name) {
            edgeDetection(state)
        }

        composable(Screen.ContourAnalysis.name) {
            contourAnalysis(state)
        }

        composable(Screen.ImageConvolution.name) {
        }

        composable(Screen.MorphologicalOperations.name) {
        }

        composable(Screen.TemplateMatching.name) {
        }
    }.build()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun experiment(state: ApplicationState) {

    val viewModel: ExperimentViewModel = koinInject()

    val screens = Screen.entries
    val navController by rememberNavController(Screen.Home.name)
    val currentScreen by remember {
        navController.currentScreen
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row (
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight().width(100.dp).weight(0.5f)
            ) {
                screens.forEach {
                    NavigationRailItem(
                        selected = currentScreen == it.name,
                        icon = {
                            Icon(
                                painter = painterResource(it.resourcePath),
                                modifier = Modifier.width(25.dp).height(25.dp),
                                contentDescription = it.label
                            )
                        },
                        label = {
                            Text(it.label)
                        },
                        modifier = Modifier.width(100.dp),
                        alwaysShowLabel = true,
                        onClick = {
                            navController.navigate(it.name)
                        }
                    )
                }
            }

            Box(
                Modifier.fillMaxSize().weight(9.5f),
                contentAlignment = Alignment.Center
            ) {
                Row (modifier = Modifier.fillMaxSize().padding(end = 90.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center) {
                    Column (modifier = Modifier.fillMaxSize().weight(1.0f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        customNavigationHost(state, navController)
                    }

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
                            Image(
                                painter = state.currentImage!!.toPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
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

                    toolTipButton(text = "撤回",
                        painter = painterResource("images/doodle/previous_step.png"),
                        iconModifier = Modifier.size(36.dp),
                        onClick = {
                            viewModel.getLastImage(state)
                        })
                }
            }
        }

        if (loadingDisplay) {
            ThreeBallLoading(Modifier.width(loadingWidth).height(height))
        }

        if (showVerifyToast) {
            centerToast(Modifier, verifyToastMessage, onDismissCallback = {
                showVerifyToast = false
            })
        }
    }
}

fun experimentViewVerifyToast(message: String) {
    verifyToastMessage = message
    showVerifyToast = true
}

@Composable
inline fun experimentViewClick(
    state: ApplicationState,
    crossinline onClick: Action
): Action {
    return composeClick(filter = {
        if (state.currentImage == null) {
            experimentViewVerifyToast("请先选择图像")
            false
        } else {
            true
        }
    }, onClick = onClick)
}