import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.http.healthCheck
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.initFilterMap
import cn.netdiscovery.monica.rxcache.initFilterParamsConfig
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.CVState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.experiment
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.faceSwap
import cn.netdiscovery.monica.ui.controlpanel.cartoon.cartoon
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionViewModel
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.colorCorrection
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorPick
import cn.netdiscovery.monica.ui.controlpanel.cropimage.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.cropimage.cropImage
import cn.netdiscovery.monica.ui.controlpanel.doodle.drawImage
import cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
import cn.netdiscovery.monica.ui.controlpanel.filter.filter
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
import cn.netdiscovery.monica.utils.chooseImage
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import javax.imageio.ImageIO

val filterNames = mutableListOf<String>()
val filterMaps = mutableMapOf<String, String>()

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)
var picUrl by mutableStateOf("")

var showVersion by mutableStateOf(false)
private var showTopToast by mutableStateOf(false)
private var topToastMessage by mutableStateOf("")
private var showCenterToast by mutableStateOf(false)
private var centerToastMessage by mutableStateOf("")
var showGeneralSettings by mutableStateOf(false)

lateinit var mAppKoin: Koin

private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

fun main() = application {

    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )

    // 使用 LaunchedEffect 在应用启动时执行一次初始化操作
    LaunchedEffect(Unit) {
        initData(applicationState)
    }

    lateinit var previewViewModel: PreviewViewModel
    lateinit var cropViewModel: CropViewModel
    lateinit var faceSwapModel: FaceSwapViewModel
    lateinit var colorCorrectionViewModel: ColorCorrectionViewModel
    lateinit var filterViewModel: FilterViewModel

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

    Thread.setDefaultUncaughtExceptionHandler{ _, throwable ->

        logger.error("全局异常捕获", throwable)
    }

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
            filterViewModel          = koinInject()

            applicationState.window  = window

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
                topToast(message = topToastMessage) {
                    showTopToast = false
                }
            }

            if (showCenterToast) {
                centerToast(message = centerToastMessage) {
                    showCenterToast = false
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
            (applicationState.currentStatus != GenerateGifStatus
                    && applicationState.currentStatus != FilterStatus
                    && applicationState.currentStatus != FaceSwapStatus
                    && applicationState.currentStatus != OpenCVDebugStatus
                    && applicationState.currentStatus != CartoonStatus)) {
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
                    FilterStatus -> {
                        filterViewModel.clear()
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
                    filter(applicationState)
                }
                FaceSwapStatus -> {
                    logger.info("enter FaceSwapView")
                    faceSwap(applicationState)
                }
                OpenCVDebugStatus -> {
                    logger.info("enter OpenCVDebugView")
                    experiment(applicationState)
                }
                CartoonStatus -> {
                    logger.info("enter CartoonView")
                    cartoon(applicationState)
                }
                else -> {}
            }
        }
    }
}

fun showTopToast(message:String) {
    topToastMessage = message
    showTopToast = true
}

fun showCenterToast(message: String) {
    centerToastMessage = message
    showCenterToast = true
}

/**
 * 初始化数据，只初始一次，包括：
 * 1. 加载滤镜的配置
 * 2. 加载 opencv 的图像处理库
 * 3. 校验算法服务
 */
private fun initData(state:ApplicationState) {

    logger.info("os = $os, arch = $arch, osVersion = $osVersion, javaVersion = $javaVersion, javaVendor = $javaVendor, monicaVersion = $appVersion, kotlinVersion = $kotlinVersion")

    filterNames.addAll(getFilterNames()) // 获取所有滤镜的名称

    if (rxCache.allKeys.isEmpty()) { // 第一次加载会缓存所有滤镜的参数配置
        initFilterParamsConfig()
    }

    if (filterMaps.isEmpty()) {
        initFilterMap()
    }

    logger.info("MonicaImageProcess Version = $imageProcessVersion, OpenCV Version = $openCVVersion")

    val formats = ImageIO.getReaderFormatNames()
    logger.info("support format: ${formats.contentToString()}")

    if (state.algorithmUrlText.isNotEmpty()) {

        val status = try {
            val baseUrl = state.algorithmUrlText
            if (healthCheck(baseUrl)) {
                STATUS_HTTP_SERVER_OK
            } else {
                STATUS_HTTP_SERVER_FAILED
            }
        } catch (e:Exception) {
            STATUS_HTTP_SERVER_FAILED
        }

        if (status == STATUS_HTTP_SERVER_OK) {
            logger.info("算法服务可用")
        } else {
            logger.info("算法服务不可用")
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
    CartoonStatus         -> "图像动漫化"
    else                  -> "放大预览"
}