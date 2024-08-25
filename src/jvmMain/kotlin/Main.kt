import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.opencv.DLManager
import cn.netdiscovery.monica.opencv.LoadManager
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParamsAndRemark
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapModel
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.faceSwap
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorPick
import cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropImage
import cn.netdiscovery.monica.ui.controlpanel.doodle.drawImage
import cn.netdiscovery.monica.ui.main.mainView
import cn.netdiscovery.monica.ui.main.openURLDialog
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.showimage.showImage
import cn.netdiscovery.monica.ui.widget.ThreeBallLoading
import cn.netdiscovery.monica.ui.widget.TopToast
import cn.netdiscovery.monica.utils.*
import com.safframework.kotlin.coroutines.runInBackground
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean

val filterNames = mutableListOf("选择滤镜")

val flag = AtomicBoolean(false)

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)
var picUrl by mutableStateOf("")

var showToast by mutableStateOf(false)
var toastMessage by mutableStateOf("")

lateinit var client: HttpConnectionClient

lateinit var mAppKoin: Koin

private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

fun main() = application {

    initData()

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    lateinit var previewViewModel: PreviewViewModel
    lateinit var cropViewModel: CropViewModel
    lateinit var faceSwapModel: FaceSwapModel

    Tray(
        state = trayState,
        icon = painterResource("images/launcher.ico"),
        menu = {
            Item(
                text = "打开本地图片",
                onClick = {
                    previewViewModel.chooseImage(applicationState)
                },
            )
            Item(
                text = "加载网络图片",
                onClick = {
                    openURLDialog = true
                },
            )
            Item(
                text = "保存图像",
                onClick = {
                    previewViewModel.saveImage(applicationState)
                },
            )
        }
    )

    Window(onCloseRequest = ::exitApplication,
        title = "Monica 图片编辑器 $appVersion",
        state = rememberWindowState(width = width, height = height).apply {
            position = WindowPosition(Alignment.BottomCenter)
        }) {

        KoinApplication(application = {
            mAppKoin = koin
            modules(viewModelModule)
        }) {
            previewViewModel = koinInject()
            cropViewModel    = koinInject()
            faceSwapModel    = koinInject()

            applicationState.window = window

            mainView(applicationState)

            if (loadingDisplay) {
                ThreeBallLoading(Modifier.width(loadingWidth).height(height))
            }

            if (openURLDialog) {
                openURLDialog(
                    onConfirm = {
                        openURLDialog = false

                        previewViewModel.loadUrl(picUrl, applicationState)

                        picUrl = ""
                    },
                    onDismiss = {
                        openURLDialog = false
                    })
            }

            if (showToast) {
                TopToast(Modifier,toastMessage, onDismissCallback = {
                    showToast = false
                })
            }
        }
    }

    if (applicationState.isShowPreviewWindow && applicationState.currentImage != null) {
        Window(
            title = getWindowsTitle(applicationState),
            onCloseRequest = {
                when(applicationState.currentStatus) {
                    DoodleStatus -> {
                        toastMessage = "想要保存涂鸦效果，需要点击保存按钮"
                        showToast = true
                    }

                    CropSizeStatus -> {
                        cropViewModel.clearCropImageView()
                    }

                    FaceSwapStatus -> {
                        faceSwapModel.clearTargetImage()
                    }

                    else -> {}
                }

                applicationState.resetCurrentStatus()
                applicationState.togglePreviewWindow(false)
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = WindowPlacement.Fullscreen
            }
        ) {
            when(applicationState.currentStatus) {
                ColorPickStatus -> {
                    logger.info("enter ColorPickView")
                    colorPick(applicationState)
                }
                DoodleStatus    -> {
                    logger.info("enter DoodleView")
                    drawImage(applicationState)
                }
                CropSizeStatus  -> {
                    logger.info("enter CropImageView")
                    cropImage(applicationState)
                }
                FaceSwapStatus -> {
                    logger.info("enter FaceSwapView")
                    faceSwap(applicationState)
                }
                else            -> {
                    logger.info("enter ShowImgView")
                    showImage(applicationState)
                }
            }
        }
    }
}

/**
 * 初始化数据，只初始一次，包括：
 * 1. 加载滤镜的配置
 * 2. 初始化 HttpConnectionClient
 * 3. 加载 opencv 的图像处理库
 * 4. 加载深度学习相关的模型
 */
private fun initData() {

    if (!flag.get()) { // 防止被多次初始化
        logger.info("os = $os, arch = $arch, osVersion = $osVersion, javaVersion = $javaVersion, javaVendor = $javaVendor, monicaVersion = $appVersion")

        filterNames.addAll(getFilterNames())
        saveFilterParamsAndRemark()

        client = HttpConnectionClient(timeout, retryNum)

        LoadManager.copy()
        logger.info("MonicaImageProcess Version = ${ImageProcess.getVersion()}, OpenCV Version = ${ImageProcess.getOpenCVVersion()}")

        runInBackground { // 初始化人脸检测的模块
            DLManager.initFaceDetectModule()
        }

        runInBackground { // 初始化生成素描画的模块
            DLManager.initSketchDrawingModule()
        }

        runInBackground { // 初始化换脸的模块
            DLManager.initFaceSwapModule()
        }

        flag.set(true)
    }
}

private fun getWindowsTitle(state: ApplicationState):String {

    return when(state.currentStatus) {
        ColorPickStatus -> "图像取色"
        DoodleStatus    -> "涂鸦图像"
        CropSizeStatus  -> "图像裁剪"
        FaceSwapStatus  -> "人脸替换"
        else            -> "放大预览"
    }
}