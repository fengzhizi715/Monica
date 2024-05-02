package cn.netdiscovery.monica.state

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.window.TrayState
import client
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.ui.selectedIndex
import cn.netdiscovery.monica.utils.*
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
    var currentImage: BufferedImage? by mutableStateOf( rawImage )
    var lastImage: BufferedImage? by mutableStateOf( rawImage )
    var rawImageFile: File? = null

    var saturation by mutableStateOf(0f )
    var luminance by mutableStateOf(0f )
    var hue by mutableStateOf(0f )

    var isBasic by mutableStateOf(false)
    var isHLS by mutableStateOf(false)
    var isFilter by mutableStateOf(false)

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
                            currentImage = rawImage
                            rawImageFile = file
                        }
                    }
                }
            }
        )
    }

    fun onClickPreviewImage() {
        scope.launch {
            loadingDisplayWithSuspend {
                if (isHLS) {
                    currentImage = hsl(rawImage!!, saturation, hue, luminance)
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

                    lastImage = currentImage
                    currentImage = doFilter(filterName,array,this@ApplicationState)
                }
            }
        }
    }

    fun loadUlrImage(picUrl:String) {
        scope.launch {
            loadingDisplayWithSuspend {
                val inputStream = client.getImage(url = picUrl)

                if (inputStream!=null) {
                    currentImage = ImageIO.read(inputStream)
                }
            }
        }
    }

    fun clearImage() {
        this.rawImage = null
        this.currentImage = null
        this.rawImageFile = null
    }

    fun togglePreviewWindow(isShow: Boolean = true) {
        isShowPreviewWindow = isShow
    }
}