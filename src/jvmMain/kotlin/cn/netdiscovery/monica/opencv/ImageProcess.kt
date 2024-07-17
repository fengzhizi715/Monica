package cn.netdiscovery.monica.opencv

import cn.netdiscovery.monica.utils.isMac

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.opencv.ImageProcess
 * @author: Tony Shen
 * @date:  2024/7/14 21:22
 * @version: V1.0 <描述当前版本功能>
 */
object ImageProcess {

    init {
        if (isMac) {
            System.load("${FileUtil.loadPath}libMonicaImageProcess.dylib")
        }
    }

    external fun getVersion():String
    external fun getOpenCVVersion():String

    external fun equalizeHist(src: ByteArray):IntArray

    external fun gammaCorrection(src: ByteArray,k:Float):IntArray

    external fun laplace(src: ByteArray):IntArray

    external fun unsharpMask(src: ByteArray, radius:Int, threshold:Int, amount:Int):IntArray
}