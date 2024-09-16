package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.getImageInfo
import cn.netdiscovery.monica.state.ApplicationState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.OpenCVManager
 * @author: Tony Shen
 * @date: 2024/8/13 19:54
 * @version: V1.0
 */
object OpenCVManager {

    /**
     * 封装调用 OpenCV 的方法
     *
     * @param state
     * @param action 通过 jni 调用 OpenCV 的方法
     * @param failure 失败的记录
     */
    fun invokeCV(state: ApplicationState,
                 action: (byteArray:ByteArray) -> IntArray,
                 failure: (e:Exception) -> Unit) {

        if (state.currentImage!=null) {
            val (width,height,byteArray) = state.currentImage!!.getImageInfo()

            try {
                val outPixels = action.invoke(byteArray)
                state.addQueue(state.currentImage!!)
                state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
            } catch (e:Exception) {
                failure.invoke(e)
            }
        }
    }

    /**
     * 初始化人脸检测的模块
     */
    fun initFaceDetectModule() {

        val faceProto = ImageProcess.resourcesDir.resolve("opencv_face_detector.pbtxt").absolutePath
        val faceModel = ImageProcess.resourcesDir.resolve("opencv_face_detector_uint8.pb").absolutePath
        val ageProto  = ImageProcess.resourcesDir.resolve("age_deploy.prototxt").absolutePath
        val ageModel  = ImageProcess.resourcesDir.resolve("age_net.caffemodel").absolutePath
        val genderProto = ImageProcess.resourcesDir.resolve("gender_deploy.prototxt").absolutePath
        val genderModel = ImageProcess.resourcesDir.resolve("gender_net.caffemodel").absolutePath

        ImageProcess.initFaceDetect(faceProto, faceModel, ageProto, ageModel, genderProto, genderModel)
    }

    /**
     * 初始化生成素描画的模块
     */
    fun initSketchDrawingModule() {

        val modelPath = ImageProcess.resourcesDir.resolve("opensketch_style_512x512.onnx").absolutePath

        ImageProcess.initSketchDrawing(modelPath)
    }

    /**
     * 初始化换脸的模块
     */
    fun initFaceSwapModule() {

        val yolov8FaceModelPath = ImageProcess.resourcesDir.resolve("yoloface_8n.onnx").absolutePath
        val face68LandmarksModePath = ImageProcess.resourcesDir.resolve("2dfan4.onnx").absolutePath
        val faceEmbeddingModePath = ImageProcess.resourcesDir.resolve("arcface_w600k_r50.onnx").absolutePath
        val faceSwapModePath = ImageProcess.resourcesDir.resolve("inswapper_128.onnx").absolutePath
        val faceSwapModePath2 = ImageProcess.resourcesDir.resolve("model_matrix.bin").absolutePath
        val faceEnhanceModePath = ImageProcess.resourcesDir.resolve("gfpgan_1.4.onnx").absolutePath

        ImageProcess.initFaceSwap(yolov8FaceModelPath, face68LandmarksModePath,
            faceEmbeddingModePath, faceSwapModePath,
            faceSwapModePath2, faceEnhanceModePath)
    }
}