package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.config.isMac
import cn.netdiscovery.monica.config.isWindows
import cn.netdiscovery.monica.domain.*
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.ImageProcess
 * @author: Tony Shen
 * @date:  2024/7/14 21:22
 * @version: V1.0 <描述当前版本功能>
 */
object ImageProcess {

    private val loadPath by lazy{
        System.getProperty("compose.application.resources.dir") + File.separator
    }

    val resourcesDir by lazy {
        File(loadPath)
    }

    init {
        // 需要先加载图像处理库，否则无法通过 jni 调用算法
        loadMonicaImageProcess()
    }

    /**
     * 对于不同的平台加载的库是不同的: mac 是 dylib 库、windows 是 dll 库、linux 是 so 库
     */
    private fun loadMonicaImageProcess() {
        if (isMac) {
            System.load("${loadPath}libMonicaImageProcess.dylib")
        } else if (isWindows) {
            System.load("${loadPath}libraw.dll")
            System.load("${loadPath}libde265.dll")
            System.load("${loadPath}aom.dll")
            System.load("${loadPath}heif.dll")
            System.load("${loadPath}opencv_world481.dll")
            System.load("${loadPath}MonicaImageProcess.dll")
        } else {
            System.load("${loadPath}libMonicaImageProcess.so")
        }
    }

    /**
     * 该算法库的版本号
     */
    external fun getVersion():String

    /**
     * 当前使用的 OpenCV 的版本号
     */
    external fun getOpenCVVersion():String

    /**
     * 图像错切
     * @param 沿 x 方向
     * @param 沿 y 方向
     */
    external fun shearing(src: ByteArray, x:Float, y:Float):IntArray

    /**
     * 初始化图像调色模块
     */
    external fun initColorCorrection(src: ByteArray): Long

    /**
     * 图像调色
     */
    external fun colorCorrection(src: ByteArray, colorCorrectionSettings: ColorCorrectionSettings, cppObjectPtr:Long):IntArray

    /**
     * 删除 ColorCorrection
     */
    external fun deleteColorCorrection(cppObjectPtr:Long): Long

    /**
     * 直方图均衡化
     */
    external fun equalizeHist(src: ByteArray):IntArray

    /**
     * 限制对比度自适应直方图均衡
     */
    external fun clahe(src: ByteArray, clipLimit:Double, size:Int):IntArray

    /**
     * gamma 校正
     */
    external fun gammaCorrection(src: ByteArray,k:Float):IntArray

    /**
     * laplace 锐化，主要是 8 邻域卷积核
     */
    external fun laplaceSharpening(src: ByteArray):IntArray

    /**
     * USM 锐化
     */
    external fun unsharpMask(src: ByteArray, radius:Int, threshold:Int, amount:Int):IntArray

    /**
     * 自动色彩均衡
     */
    external fun ace(src: ByteArray, ratio:Int, radius:Int):IntArray

    /**
     * 转换成灰度图像
     */
    external fun cvtGray(src: ByteArray):IntArray

    /**
     * 阈值分割
     */
    external fun threshold(src: ByteArray, thresholdType1: Int, thresholdType2: Int):IntArray

    /**
     * 自适应阈值分割
     */
    external fun adaptiveThreshold(src: ByteArray, adaptiveMethod: Int, thresholdType: Int, blockSize:Int, c:Int):IntArray

    /**
     * 颜色分割
     */
    external fun inRange(src: ByteArray, hmin:Int, smin:Int, vmin:Int, hmax:Int, smax:Int, vmax:Int):IntArray

    /**
     * 实现 roberts 算子
     */
    external fun roberts(src: ByteArray):IntArray

    /**
     * 实现 prewitt 算子
     */
    external fun prewitt(src: ByteArray):IntArray

    /**
     * 实现 sobel 算子
     */
    external fun sobel(src: ByteArray):IntArray

    /**
     * 实现 laplace 算子
     */
    external fun laplace(src: ByteArray):IntArray

    /**
     * 实现 canny 算子
     */
    external fun canny(src: ByteArray, threshold1:Double, threshold2: Double, apertureSize:Int):IntArray

    /**
     * 实现 LoG 算子
     */
    external fun log(src: ByteArray):IntArray

    /**
     * 实现 DoG 算子
     */
    external fun dog(src: ByteArray, sigma1:Double, sigma2:Double, size:Int):IntArray

    /**
     * 轮廓分析
     */
    external fun contourAnalysis(src: ByteArray, binary: ByteArray, scalar:IntArray, contourFilterSettings: ContourFilterSettings, contourDisplaySettings: ContourDisplaySettings):IntArray

    /**
     * 实现高斯滤波
     */
    external fun gaussianBlur(src: ByteArray, ksize:Int, sigmaX: Double = 0.0, sigmaY: Double = 0.0):IntArray

    /**
     * 实现中值滤波
     */
    external fun medianBlur(src: ByteArray, ksize:Int):IntArray

    /**
     * 实现高斯双边滤波
     */
    external fun bilateralFilter(src: ByteArray, d:Int, sigmaColor:Double, sigmaSpace:Double):IntArray

    /**
     * 实现均值迁移滤波
     */
    external fun pyrMeanShiftFiltering(src: ByteArray, sp: Double, sr: Double):IntArray

    /**
     * 形态学操作
     */
    external fun morphologyEx(src: ByteArray, morphologicalOperationSettings: MorphologicalOperationSettings):IntArray

    /**
     * 模版匹配
     */
    external fun matchTemplate(src: ByteArray, template: ByteArray, scalar:IntArray, matchTemplateSettings: MatchTemplateSettings):IntArray

    /**
     * 解码相机拍摄的图片，例如 cr2、cr3 格式的图像
     */
    external fun decodeRawToBuffer(path: String, isPreview: Boolean): RawImage?

    /**
     * 解码 heif 格式的图像
     */
    external fun decodeHeif(path: String): HeifImage?
}