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
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.*
import cn.netdiscovery.monica.utils.Action
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import loadingDisplay
import cn.netdiscovery.monica.ui.i18n.rememberI18nState
import cn.netdiscovery.monica.i18n.LocalizationManager
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
    private val labelKey: String,
    val resourcePath: String
) {
    Home(
        labelKey = "experiment_home",
        resourcePath = "images/ai/home.png"
    ),
    BinaryImage(
        labelKey = "experiment_binary_image",
        resourcePath = "images/ai/binary_image.png"
    ),
    EdgeDetection(
        labelKey = "experiment_edge_detection",
        resourcePath = "images/ai/edge_detection.png"
    ),
    ContourAnalysis(
        labelKey = "experiment_contour_analysis",
        resourcePath = "images/ai/contour_analysis.png"
    ),
    ImageEnhance(
        labelKey = "experiment_image_enhance",
        resourcePath = "images/ai/image_enhance.png"
    ),
    ImageDenoising(
        labelKey = "experiment_image_denoising",
        resourcePath = "images/ai/image_convolution.png"
    ),
    MorphologicalOperations(
        labelKey = "experiment_morphological_operations",
        resourcePath = "images/ai/morphological_operations.png"
    ),
    MatchTemplate(
        labelKey = "experiment_match_template",
        resourcePath = "images/ai/match_template.png"
    ),
    History(
        labelKey = "experiment_history",
        resourcePath = "images/ai/history.png"
    );
    
    fun getLabel(): String {
        return LocalizationManager.getString(labelKey)
    }
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
            binaryImage(state, Screen.BinaryImage.getLabel())
        }

        composable(Screen.EdgeDetection.name) {
            edgeDetection(state, Screen.EdgeDetection.getLabel())
        }

        composable(Screen.ContourAnalysis.name) {
            contourAnalysis(state, Screen.ContourAnalysis.getLabel())
        }

        composable(Screen.ImageEnhance.name) {
            imageEnhance(state, Screen.ImageEnhance.getLabel())
        }

        composable(Screen.ImageDenoising.name) {
            imageDenoising(state, Screen.ImageDenoising.getLabel())
        }

        composable(Screen.MorphologicalOperations.name) {
            morphologicalOperations(state, Screen.MorphologicalOperations.getLabel())
        }

        composable(Screen.MatchTemplate.name) {
            matchTemplate(state, Screen.MatchTemplate.getLabel())
        }

        composable(Screen.History.name) {
            history(state, Screen.History.getLabel())
        }
    }.build()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun experiment(state: ApplicationState) {
    val i18nState = rememberI18nState()

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
                                contentDescription = it.getLabel()
                            )
                        },
                        label = {
                            Text(it.getLabel())
                        },
                        modifier = Modifier.width(100.dp).height(80.dp),
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
                                text = i18nState.getString("click_to_select_image"),
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
                    toolTipButton(text = i18nState.getString("delete"),
                        painter = painterResource("images/preview/delete.png"),
                        iconModifier = Modifier.size(36.dp),
                        onClick = {
                            state.clearImage()
                        })

                    toolTipButton(text = i18nState.getString("undo"),
                        painter = painterResource("images/doodle/previous_step.png"),
                        iconModifier = Modifier.size(36.dp),
                        onClick = {
                            state.getLastImage()?.let {
                                state.currentImage = it
                            }
                        })

                    toolTipButton(text = i18nState.getString("save"),
                        painter = painterResource("images/doodle/save.png"),
                        iconModifier = Modifier.size(36.dp),
                        onClick = {
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
    val i18nState = rememberI18nState()
    
    return rememberThrottledClick(filter = {
        if (state.currentImage == null) {
            experimentViewVerifyToast(i18nState.getString("please_select_image_first"))
            false
        } else {
            true
        }
    }, onClick = onClick)
}