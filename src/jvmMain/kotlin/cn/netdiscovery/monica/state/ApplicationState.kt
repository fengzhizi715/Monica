package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.TrayState
import cn.netdiscovery.monica.imageprocess.filter.*
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.ui.selectedIndex
import cn.netdiscovery.monica.utils.hsl
import cn.netdiscovery.monica.utils.loadingDisplay
import cn.netdiscovery.monica.utils.loadingDisplayWithSuspend
import cn.netdiscovery.monica.utils.showFileSelector
import filterNames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JFileChooser

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.state.ApplicationState
 * @author: Tony Shen
 * @date: 2024/4/26 10:42
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun rememberApplicationState(
    scope: CoroutineScope,
    trayState: TrayState
) = remember {
    ApplicationState(scope, trayState)
}

class ApplicationState(val scope:CoroutineScope,
                       val trayState: TrayState) {

    lateinit var window: ComposeWindow

    var rawImage: BufferedImage? by mutableStateOf(null)
    var showImage: BufferedImage? by mutableStateOf( rawImage )
    var rawImgFile: File? = null

    var saturation by mutableStateOf(0.8f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    var topPercent by mutableStateOf(0.3f)
    var bottomPercent by mutableStateOf(0.3f)

    var isHLS by mutableStateOf(true)
    var isFilter by mutableStateOf(true)

    var isShowGuideLine by mutableStateOf(false)

    var outputPath by mutableStateOf("")
    var isUsingSourcePath by mutableStateOf(false)

    var isShowPreviewWindow by mutableStateOf(false)

    fun onClickImgChoose() {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.FILES_ONLY,
            onFileSelected = {
                scope.launch(Dispatchers.IO) {
                    loadingDisplay {
                        val file = it.getOrNull(0)
                        if (file != null) {
                            rawImage = ImageIO.read(file)
                            showImage = rawImage
                            rawImgFile = file
                        }
                    }
                }
            }
        )
    }

    fun onClickBuildImg() {
        scope.launch {
            loadingDisplayWithSuspend {
                if (isHLS) {
                    showImage = hsl(rawImage!!, saturation, hue, luminance)
                }

                if(isFilter) {
                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName)

                    val array = mutableListOf<Any>()
                    params?.forEach {
                        array.add(it.third)
                    }

                    if (selectedIndex.value>0) {
                        println("filterName: $filterName, params: $array")
                    }

                    when(filterName) {
                        "BilateralFilter" -> {
                            showImage = BilateralFilter(array[0] as Double,array[1] as Double).transform(showImage!!)
                        }
                        "BoxBlurFilter" -> {
                            showImage = BoxBlurFilter(array[0] as Int,array[1] as Int,array[2] as Int).transform(showImage!!)
                        }
                        "ConBriFilter" -> {
                            showImage = ConBriFilter(array[0] as Float,array[1] as Float).transform(showImage!!)
                        }
                        "GammaFilter" -> {
                            showImage = GammaFilter(array[0] as Double).transform(showImage!!)
                        }
                        "GaussianFilter" -> {
                            showImage = GaussianFilter(array[0] as Float).transform(showImage!!.toComposeImageBitmap().toAwtImage())
                        }
                        "SpotlightFilter" -> {
                            showImage = SpotlightFilter(array[0] as Int).transform(showImage!!)
                        }
                    }
                }
            }
        }
    }

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }
}