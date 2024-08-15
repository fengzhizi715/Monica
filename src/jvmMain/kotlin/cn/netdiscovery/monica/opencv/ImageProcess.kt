package cn.netdiscovery.monica.opencv

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

    val loadPath = System.getProperty("compose.application.resources.dir") + File.separator

    init {
        // 需要先加载图像处理库，否则无法通过 jni 调用算法
        LoadManager.load()
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
    external fun laplace(src: ByteArray):IntArray

    /**
     * USM 锐化
     */
    external fun unsharpMask(src: ByteArray, radius:Int, threshold:Int, amount:Int):IntArray

    /**
     * 自动色彩均衡
     */
    external fun ace(src: ByteArray, ratio:Int, radius:Int):IntArray

    /**
     * 初始化人脸检测模块
     */
    external fun initFaceDetect(faceProto:String,faceModel:String,
                                ageProto:String, ageModel:String,
                                genderProto:String,genderModel:String)

    /**
     * 人脸检测
     */
    external fun faceDetect(src: ByteArray):IntArray

    external fun initSketchDrawing(modelPath:String)


    external fun sketchDrawing(src: ByteArray):IntArray
}