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

    init { // 对于不同的平台加载的库是不同的，mac 是 dylib 库，windows 是 dll 库，linux 是 so 库
        if (isMac) {
            System.load("${FileUtil.loadPath}libMonicaImageProcess.dylib")
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
     * 直方图均衡化
     */
    external fun equalizeHist(src: ByteArray):IntArray

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
}