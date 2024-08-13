package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.utils.isWindows

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.DLManager
 * @author: Tony Shen
 * @date: 2024/8/13 19:54
 * @version: V1.0 深度学习的管理类
 */
object DLManager {

    fun initFaceDetectModule() {
        LoadManager.copyFaceDetectModels()

        val faceProto = if (isWindows) "${LoadManager.loadPath}opencv_face_detector.pbtxt" else "${ImageProcess.loadPath}opencv_face_detector.pbtxt"
        val faceModel = if (isWindows) "${LoadManager.loadPath}opencv_face_detector_uint8.pb" else "${ImageProcess.loadPath}opencv_face_detector_uint8.pb"
        val ageProto = if (isWindows) "${LoadManager.loadPath}age_deploy.prototxt" else "${ImageProcess.loadPath}age_deploy.prototxt"
        val ageModel = if (isWindows) "${LoadManager.loadPath}age_net.caffemodel" else "${ImageProcess.loadPath}age_net.caffemodel"
        val genderProto = if (isWindows) "${LoadManager.loadPath}gender_deploy.prototxt" else "${ImageProcess.loadPath}gender_deploy.prototxt"
        val genderModel = if (isWindows) "${LoadManager.loadPath}gender_net.caffemodel" else "${ImageProcess.loadPath}gender_net.caffemodel"

        ImageProcess.initFaceDetect(faceProto,faceModel, ageProto,ageModel, genderProto,genderModel)
    }
}