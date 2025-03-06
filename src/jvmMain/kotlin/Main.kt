import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.http.HttpConnectionClient
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.manager.OpenCVManager
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.saveFilterParamsAndRemark
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.CVState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.experiment
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.faceSwap
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionViewModel
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.colorCorrection
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorPick
import cn.netdiscovery.monica.ui.controlpanel.cropimage.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.cropimage.cropImage
import cn.netdiscovery.monica.ui.controlpanel.doodle.drawImage
import cn.netdiscovery.monica.ui.controlpanel.generategif.generateGif
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.shapeDrawing
import cn.netdiscovery.monica.ui.main.generalSettings
import cn.netdiscovery.monica.ui.main.mainView
import cn.netdiscovery.monica.ui.main.openURLDialog
import cn.netdiscovery.monica.ui.main.showVersionInfo
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.showimage.showImage
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.ui.widget.showLoading
import cn.netdiscovery.monica.ui.widget.topToast
import cn.netdiscovery.monica.utils.*
import com.safframework.kotlin.coroutines.runInBackground
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val filterNames = mutableListOf("选择滤镜")

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)
var picUrl by mutableStateOf("")

var showVersion by mutableStateOf(false)
private var showTopToast by mutableStateOf(false)
private var toastMessage by mutableStateOf("")
private var showVerifyToast by mutableStateOf(false)
private var verifyToastMessage by mutableStateOf("")
var showGeneralSettings by mutableStateOf(false)

lateinit var client: HttpConnectionClient

lateinit var mAppKoin: Koin

private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

fun main() = application {

    // 使用 LaunchedEffect 在应用启动时执行一次初始化操作
    LaunchedEffect(Unit) {
        initData()
    }

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    lateinit var previewViewModel: PreviewViewModel
    lateinit var cropViewModel: CropViewModel
    lateinit var faceSwapModel: FaceSwapViewModel
    lateinit var colorCorrectionViewModel: ColorCorrectionViewModel

    Tray(
        state = trayState,
        icon = painterResource("images/launcher.ico"),
        menu = {
            Item(
                text = "软件版本信息",
                onClick = {
                    showVersion = true
                },
            )
            Item(
                text = "打开本地图片",
                onClick = {
                    chooseImage(applicationState) { file ->
                        applicationState.rawImage = BufferedImages.load(file)
                        applicationState.currentImage = applicationState.rawImage
                        applicationState.rawImageFile = file
                    }
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
            previewViewModel         = koinInject()
            cropViewModel            = koinInject()
            faceSwapModel            = koinInject()
            colorCorrectionViewModel = koinInject()

            applicationState.window = window

            mainView(applicationState)

            if (loadingDisplay) {
                showLoading()
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

            if (showTopToast) {
                topToast(message = toastMessage) {
                    showTopToast = false
                }
            }

            if (showVerifyToast) {
                centerToast(message = verifyToastMessage) {
                    showVerifyToast = false
                }
            }

            if (showVersion) {
                showVersionInfo {
                    showVersion = false
                }
            }

            if (showGeneralSettings) {
                generalSettings(applicationState) {
                    showGeneralSettings = false
                }
            }
        }
    }

    if (applicationState.isShowPreviewWindow) {

        if (applicationState.currentImage == null &&
            (applicationState.currentStatus != GenerateGifStatus && applicationState.currentStatus != FaceSwapStatus && applicationState.currentStatus != OpenCVDebugStatus)){
            showTopToast("请先选择图像")

            return@application
        }

        Window(
            title = getWindowsTitle(applicationState),
            onCloseRequest = {
                when(applicationState.currentStatus) {
                    DoodleStatus -> {
                        showTopToast("想要保存涂鸦效果，需要点击保存按钮")
                    }
                    ShapeDrawingStatus -> {
                        showTopToast("想要保存形状绘制的结果，需要点击保存按钮")
                    }
                    CropSizeStatus -> {
                        cropViewModel.clearCropImageView()
                    }
                    ColorCorrectionStatus -> {
                        colorCorrectionViewModel.clearAllStatus()
                    }
                    FaceSwapStatus -> {
                        faceSwapModel.clearTargetImage()
                    }
                    OpenCVDebugStatus -> {
                        CVState.clearAllStatus()
                    }
                }

                applicationState.closePreviewWindow()
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = if(isWindows) WindowPlacement.Maximized else WindowPlacement.Fullscreen
            }
        ) {
            when(applicationState.currentStatus) {
                ZoomPreviewStatus -> {
                    logger.info("enter ShowImgView")
                    showImage(applicationState)
                }
                ColorPickStatus -> {
                    logger.info("enter ColorPickView")
                    colorPick(applicationState)
                }
                GenerateGifStatus -> {
                    logger.info("enter GenerateGifView")
                    generateGif(applicationState)
                }
                DoodleStatus -> {
                    logger.info("enter DoodleView")
                    drawImage(applicationState)
                }
                ShapeDrawingStatus -> {
                    logger.info("enter ShapeDrawingView")
                    shapeDrawing(applicationState)
                }
                CropSizeStatus -> {
                    logger.info("enter CropImageView")
                    cropImage(applicationState)
                }
                ColorCorrectionStatus -> {
                    logger.info("enter ColorCorrectionView")
                    colorCorrection(applicationState)
                }
                FilterStatus -> {
                    logger.info("enter FilterView")

                }
                FaceSwapStatus -> {
                    logger.info("enter FaceSwapView")
                    faceSwap(applicationState)
                }
                OpenCVDebugStatus -> {
                    logger.info("enter OpenCVDebugView")
                    experiment(applicationState)
                }
                else -> {}
            }
        }
    }
}

fun showTopToast(message:String) {
    toastMessage = message
    showTopToast = true
}

fun showVerifyToast(message: String) {
    verifyToastMessage = message
    showVerifyToast = true
}

/**
 * 初始化数据，只初始一次，包括：
 * 1. 加载滤镜的配置
 * 2. 初始化 HttpConnectionClient
 * 3. 加载 opencv 的图像处理库
 * 4. 加载深度学习相关的模型
 */
private fun initData() {

    logger.info("os = $os, arch = $arch, osVersion = $osVersion, javaVersion = $javaVersion, javaVendor = $javaVendor, monicaVersion = $appVersion, kotlinVersion = $kotlinVersion")

    filterNames.addAll(getFilterNames())
    saveFilterParamsAndRemark()

    client = HttpConnectionClient(timeout, retryNum)

    logger.info("MonicaImageProcess Version = $imageProcessVersion, OpenCV Version = $openCVVersion, ONNXRuntime Version = $onnxRuntimeVersion")

    if (isProVersion) {
        runInBackground { // 初始化人脸检测的模块
            OpenCVManager.initFaceDetectModule()
        }

        runInBackground { // 初始化生成素描画的模块
            OpenCVManager.initSketchDrawingModule()
        }

        runInBackground { // 初始化换脸的模块
            OpenCVManager.initFaceSwapModule()
        }
    }
}

private fun getWindowsTitle(state: ApplicationState):String = when(state.currentStatus) {
    DoodleStatus          -> "涂鸦图像"
    ShapeDrawingStatus    -> "形状绘制"
    ColorPickStatus       -> "图像取色"
    GenerateGifStatus     -> "生成 gif"
    CropSizeStatus        -> "图像裁剪"
    ColorCorrectionStatus -> "图像调色"
    FilterStatus          -> "使用滤镜"
    OpenCVDebugStatus     -> "简单 CV 算法的快速验证"
    FaceSwapStatus        -> "人脸替换"
    else                  -> "放大预览"
}