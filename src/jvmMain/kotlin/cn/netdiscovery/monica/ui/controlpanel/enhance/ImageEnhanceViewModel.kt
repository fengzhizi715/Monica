package cn.netdiscovery.monica.ui.controlpanel.enhance

import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.clickLoadingDisplay
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.enhance.ImageEnhanceViewModel
 * @author: Tony Shen
 * @date: 2024/7/17 21:33
 * @version: V1.0 <描述当前版本功能>
 */
class ImageEnhanceViewModel {
    private val logger: Logger = logger<ImageEnhanceViewModel>()

    fun equalizeHist(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.equalizeHist(byteArray)
                    }, failure = { e ->
                        logger.error("equalizeHist is failed", e)
                    })
                }
            }
        }
    }

    fun clahe(state: ApplicationState, clipLimit:Double, size:Int) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.clahe(byteArray, clipLimit, size)
                    }, failure = { e ->
                        logger.error("clahe is failed", e)
                    })
                }
            }
        }
    }

    fun gammaCorrection(state: ApplicationState, gamma:Float) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.gammaCorrection(byteArray, gamma)
                    }, failure = { e ->
                        logger.error("gammaCorrection is failed", e)
                    })
                }
            }
        }
    }

    fun laplace(state: ApplicationState) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.laplace(byteArray)
                    }, failure = { e ->
                        logger.error("laplace is failed", e)
                    })
                }
            }
        }
    }

    fun unsharpMask(state: ApplicationState,radius:Int,threshold:Int,amount:Int) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.unsharpMask(byteArray,radius,threshold,amount)
                    }, failure = { e ->
                        logger.error("unsharpMask is failed", e)
                    })
                }
            }
        }
    }

    fun ace(state: ApplicationState, ratio:Int, radius:Int) {
        if (state.currentImage!=null) {
            state.scope.launch(IO) {
                clickLoadingDisplay {
                    OpenCVManager.invokeCV(state, action = { byteArray ->
                        ImageProcess.ace(byteArray,ratio,radius)
                    }, failure = { e ->
                        logger.error("ace is failed", e)
                    })
                }
            }
        }
    }
}