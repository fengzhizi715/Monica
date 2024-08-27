package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.imageprocess.getImageInfo
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.isWindows

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
        val (width,height,byteArray) = state.currentImage!!.getImageInfo()

        try {
            val outPixels = action.invoke(byteArray)
            state.addQueue(state.currentImage!!)
            state.currentImage = BufferedImages.toBufferedImage(outPixels,width,height)
        } catch (e:Exception) {
            failure.invoke(e)
        }
    }

    /**
     * 初始化人脸检测的模块
     */
    fun initFaceDetectModule() {
        LoadManager.copyFaceDetectModels()

        val faceProto = if (isWindows) "${LoadManager.loadPath}opencv_face_detector.pbtxt" else "${ImageProcess.loadPath}opencv_face_detector.pbtxt"
        val faceModel = if (isWindows) "${LoadManager.loadPath}opencv_face_detector_uint8.pb" else "${ImageProcess.loadPath}opencv_face_detector_uint8.pb"
        val ageProto = if (isWindows) "${LoadManager.loadPath}age_deploy.prototxt" else "${ImageProcess.loadPath}age_deploy.prototxt"
        val ageModel = if (isWindows) "${LoadManager.loadPath}age_net.caffemodel" else "${ImageProcess.loadPath}age_net.caffemodel"
        val genderProto = if (isWindows) "${LoadManager.loadPath}gender_deploy.prototxt" else "${ImageProcess.loadPath}gender_deploy.prototxt"
        val genderModel = if (isWindows) "${LoadManager.loadPath}gender_net.caffemodel" else "${ImageProcess.loadPath}gender_net.caffemodel"

        ImageProcess.initFaceDetect(faceProto, faceModel, ageProto, ageModel, genderProto, genderModel)
    }

    /**
     * 初始化生成素描画的模块
     */
    fun initSketchDrawingModule() {
        LoadManager.copySketchDrawingModel()

        val modelPath = if (isWindows) "${LoadManager.loadPath}opensketch_style_512x512.onnx" else "${ImageProcess.loadPath}opensketch_style_512x512.onnx"

        ImageProcess.initSketchDrawing(modelPath)
    }

    /**
     * 初始化换脸的模块
     */
    fun initFaceSwapModule() {
        LoadManager.copyFaceSwapModel()

        val yolov8FaceModelPath = if (isWindows) "${LoadManager.loadPath}yoloface_8n.onnx" else "${ImageProcess.loadPath}yoloface_8n.onnx"
        val face68LandmarksModePath = if (isWindows) "${LoadManager.loadPath}2dfan4.onnx" else "${ImageProcess.loadPath}2dfan4.onnx"

        ImageProcess.initFaceSwap(yolov8FaceModelPath, face68LandmarksModePath)
    }
}