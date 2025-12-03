import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import cn.netdiscovery.monica.config.*
import cn.netdiscovery.monica.di.viewModelModule
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.http.healthCheck
import cn.netdiscovery.monica.i18n.LocalizationManager
import cn.netdiscovery.monica.rxcache.getFilterNames
import cn.netdiscovery.monica.rxcache.initFilterMap
import cn.netdiscovery.monica.rxcache.initFilterParamsConfig
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.*
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.experiment
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.faceSwap
import cn.netdiscovery.monica.ui.controlpanel.cartoon.cartoon
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.colorCorrection
import cn.netdiscovery.monica.ui.controlpanel.colorpick.colorPick
import cn.netdiscovery.monica.ui.controlpanel.cropimage.cropImage
import cn.netdiscovery.monica.ui.controlpanel.doodle.drawImage
import cn.netdiscovery.monica.ui.controlpanel.filter.filter
import cn.netdiscovery.monica.ui.controlpanel.generategif.generateGif
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.shapeDrawing
import cn.netdiscovery.monica.ui.main.generalSettings
import cn.netdiscovery.monica.ui.main.mainView
import cn.netdiscovery.monica.ui.main.openURLDialog
import cn.netdiscovery.monica.ui.theme.CustomMaterialTheme
import cn.netdiscovery.monica.ui.main.showVersionInfo
import cn.netdiscovery.monica.ui.preview.PreviewViewModel
import cn.netdiscovery.monica.ui.showimage.showImage
import cn.netdiscovery.monica.ui.widget.PageLifecycle
import cn.netdiscovery.monica.ui.widget.centerToast
import cn.netdiscovery.monica.ui.widget.showLoading
import cn.netdiscovery.monica.exception.ErrorHandler
import cn.netdiscovery.monica.exception.ErrorState
import cn.netdiscovery.monica.ui.widget.topToast
import cn.netdiscovery.monica.utils.chooseImage
import cn.netdiscovery.monica.utils.getBufferedImage
import cn.netdiscovery.monica.utils.captureFullScreen
import cn.netdiscovery.monica.utils.loadScreenshotToState
import cn.netdiscovery.monica.ui.screenshot.showSwingScreenshotAreaSelector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.Koin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val filterNames = mutableListOf<String>()
val filterMaps = mutableMapOf<String, String>()

var loadingDisplay by mutableStateOf(false)
var openURLDialog by mutableStateOf(false)
var picUrl by mutableStateOf("")

var showVersion by mutableStateOf(false)
private var showTopToast by mutableStateOf(false)
private var topToastMessage by mutableStateOf("")
var showGeneralSettings by mutableStateOf(false)
var showScreenshotAreaSelector by mutableStateOf(false)

lateinit var mAppKoin: Koin

private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

fun main() = application {
    val trayState = rememberTrayState()

    val applicationState = rememberApplicationState(
        rememberCoroutineScope(),
        trayState
    )
    
    // 全局错误处理状态
    val errorState = remember { ErrorState() }

    PageLifecycle(
        onInit = {
            logger.info("首页启动时初始化")
            initData(applicationState)
        },
        onDisposeEffect = {
            logger.info("首页关闭")

            applicationState.clearImage()
            EditHistoryCenter.clearAll()

            logger.info("释放全部资源")
        }
    )

    lateinit var previewViewModel: PreviewViewModel

    Tray(
        state = trayState,
        icon = painterResource("images/launcher.ico"),
        menu = {
            Item(
                text = LocalizationManager.getString("software_version_info"),
                onClick = {
                    showVersion = true
                },
            )
            Item(
                text = LocalizationManager.getString("open_local_image"),
                onClick = {
                    chooseImage(applicationState) { file ->
                        val image = getBufferedImage(file, applicationState)
                        applicationState.rawImage = image
                        applicationState.currentImage = applicationState.rawImage
                        applicationState.rawImageFile = file
                    }
                },
            )
            Item(
                text = LocalizationManager.getString("load_network_image"),
                onClick = {
                    openURLDialog = true
                },
            )
            Separator()
            Item(
                text = LocalizationManager.getString("screenshot_full_screen"),
                onClick = {
                    // 全屏截图：先隐藏主窗口，延迟截图，再恢复主窗口
                    applicationState.window.isVisible = false

                    applicationState.scope.launch {
                        try {
                            delay(200) // 等待窗口隐藏动画完成
                            val screenshot = captureFullScreen()
                            delay(100)
                            // 在 AWT Event Dispatch Thread 中恢复窗口可见性
                            java.awt.EventQueue.invokeLater {
                                applicationState.window.isVisible = true
                                if (screenshot != null) {
                                    loadScreenshotToState(applicationState, screenshot)
                                }
                            }
                        } catch (e: Exception) {
                            logger.error("全屏截图失败", e)
                            // 确保窗口在错误时也能恢复
                            java.awt.EventQueue.invokeLater {
                                applicationState.window.isVisible = true
                            }
                        }
                    }
                },
            )
            Item(
                text = LocalizationManager.getString("screenshot_area"),
                onClick = {
                    // 区域选择：显示简化对话框，用户点击截图
                    showScreenshotAreaSelector = true
                },
            )
            Separator()
            Item(
                text = LocalizationManager.getString("save_image"),
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
        title = "${LocalizationManager.getString("monica_image_editor")} $appVersion",
        state = rememberWindowState(width = width, height = height).apply {
            position = WindowPosition(Alignment.BottomCenter)
        }) {

        KoinApplication(application = {
            mAppKoin = koin
            modules(viewModelModule)
        }) {
            previewViewModel         = koinInject()

            applicationState.window  = window

            CustomMaterialTheme(theme = applicationState.getCurrentThemeValue().also { 
                logger.info("主窗口使用主题: ${it.name}")
            }) {
                mainView(applicationState)
                
                // 全局错误处理 - 在 mainView 之后，确保在最顶层
                ErrorHandler(errorState)

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
                }

                applicationState.closePreviewWindow()
            },
            state = rememberWindowState().apply {
                position = WindowPosition(Alignment.Center)
                placement = if(isWindows) WindowPlacement.Maximized else WindowPlacement.Fullscreen
            }
        ) {
            CustomMaterialTheme(theme = applicationState.getCurrentThemeValue().also { 
                logger.info("预览窗口使用主题: ${it.name}")
            }) {
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
                        logger.info("enter ShapeDrawingViewRefactored")
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

    if (showScreenshotAreaSelector) {
        // 使用 Swing 实现的区域选择器（在 macOS 上更可靠）
        showSwingScreenshotAreaSelector(
            state = applicationState,
            onDismiss = {
                showScreenshotAreaSelector = false
            }
        )
    }
}

fun showTopToast(message:String) {
    topToastMessage = message
    showTopToast = true
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
    DoodleStatus          -> LocalizationManager.getString("window_title_doodle")
    ShapeDrawingStatus    -> LocalizationManager.getString("window_title_shape_drawing")
    ColorPickStatus       -> LocalizationManager.getString("window_title_color_pick")
    GenerateGifStatus     -> LocalizationManager.getString("window_title_generate_gif")
    CropSizeStatus        -> LocalizationManager.getString("window_title_crop_size")
    ColorCorrectionStatus -> LocalizationManager.getString("window_title_color_correction")
    FilterStatus          -> LocalizationManager.getString("window_title_filter")
    OpenCVDebugStatus     -> LocalizationManager.getString("window_title_opencv_debug")
    FaceSwapStatus        -> LocalizationManager.getString("window_title_face_swap")
    CartoonStatus         -> LocalizationManager.getString("window_title_cartoon")
    else                  -> LocalizationManager.getString("window_title_preview")
}