package cn.netdiscovery.monica.ui.controlpanel.ai.experiment

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapViewModel
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.ExperimentView
 * @author: Tony Shen
 * @date: 2024/9/23 19:37
 * @version: V1.0 <描述当前版本功能>
 */

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
    BinaryImageAnalysis(
        label = "二值图像分析",
        resourcePath = "images/ai/binary_image_analysis.png"
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
    navController: NavController
) {
    NavigationHost(navController) {
        composable(Screen.Home.name) {
        }

        composable(Screen.BinaryImageAnalysis.name) {
            binaryImageAnalysis()
        }

        composable(Screen.ContourAnalysis.name) {
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

            Row (modifier = Modifier.fillMaxSize().weight(9.5f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {

                Column (modifier = Modifier.fillMaxSize().weight(1.0f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    customNavigationHost(navController = navController)
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
        }
    }
}