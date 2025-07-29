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
import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.modules.opencv.CVParams
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import com.safframework.rxcache.utils.GsonUtils
import loadingDisplay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ExperimentView
 * @author: Tony Shen
 * @date: 2024/9/23 19:37
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

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
    ImageEnhance(
        label = "图像增强",
        resourcePath = "images/ai/image_enhance.png"
    ),
    ImageDenoising(
        label = "图像降噪",
        resourcePath = "images/ai/image_convolution.png"
    ),
    MorphologicalOperations(
        label = "形态学操作",
        resourcePath = "images/ai/morphological_operations.png"
    ),
    MatchTemplate(
        label = "模版匹配",
        resourcePath = "images/ai/match_template.png"
    )
}

@Composable
fun customNavigationHost(
    state: ApplicationState,
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Home.name) {
            experimentHome()
        }

        composable(Screen.BinaryImage.name) {
            binaryImage(state, Screen.BinaryImage.label)
        }

        composable(Screen.EdgeDetection.name) {
            edgeDetection(state, Screen.EdgeDetection.label)
        }

        composable(Screen.ContourAnalysis.name) {
            contourAnalysis(state, Screen.ContourAnalysis.label)
        }

        composable(Screen.ImageEnhance.name) {
            imageEnhance(state, Screen.ImageEnhance.label)
        }

        composable(Screen.ImageDenoising.name) {
            imageDenoising(state, Screen.ImageDenoising.label)
        }

        composable(Screen.MorphologicalOperations.name) {
            morphologicalOperations(state, Screen.MorphologicalOperations.label)
        }

        composable(Screen.MatchTemplate.name) {
            matchTemplate(state, Screen.MatchTemplate.label)
        }
    }.build()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun experiment(state: ApplicationState) {

    val screens = Screen.entries
    val navController by rememberNavController(Screen.Home.name)
    val currentScreen by remember { navController.currentScreen }

    PageLifecycle(
        onInit = {
            logger.info("OpenCVDebugView 启动时初始化")
        },
        onDisposeEffect = {
            logger.info("OpenCVDebugView 关闭时释放资源")
            CVState.clearAllStatus()
        }
    )

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
                            state.getLastImage()?.let {
                                state.currentImage = it
                            }
                        })

                    toolTipButton(text = "保存",
                        painter = painterResource("images/doodle/save.png"),
                        iconModifier = Modifier.size(36.dp),
                        onClick = {

                            val records = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV).getOperationLog()
                            logger.info("records = ${GsonUtils.toJson(records)}")

                            state.togglePreviewWindow(false)
                        })
                }
            }
        }

        if (loadingDisplay) {
            showLoading()
        }

        if (showVerifyToast) {
            centerToast(message = verifyToastMessage) {
                showVerifyToast = false
            }
        }
    }
}

/**
 * experiment 模块下，通用的显示验证相关的 toast
 */
fun experimentViewVerifyToast(message: String) {
    verifyToastMessage = message
    showVerifyToast = true
}

@Composable
fun experimentViewClick(
    state: ApplicationState,
    onClick: Action
): Action {
    return rememberThrottledClick(filter = {
        if (state.currentImage == null) {
            experimentViewVerifyToast("请先选择图像")
            false
        } else {
            true
        }
    }, onClick = onClick)
}