package cn.netdiscovery.monica.ui.controlpanel.crop

import cn.netdiscovery.monica.config.KEY_CROP_FIRST
import cn.netdiscovery.monica.config.KEY_CROP_SECOND
import cn.netdiscovery.monica.imageprocess.*
import cn.netdiscovery.monica.opencv.ImageProcess
import cn.netdiscovery.monica.opencv.OpenCVManager
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.contentScalesIndex
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag1
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag2
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropTypesIndex
import cn.netdiscovery.monica.utils.extension.launchWithLoading
import cn.netdiscovery.monica.utils.loadingDisplay
import cn.netdiscovery.monica.utils.logger
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.launch
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 11:46
 * @version: V1.0 <描述当前版本功能>
 */
class CropViewModel {
    private val logger: Logger = logger<CropViewModel>()

    fun flip(state: ApplicationState) {

        state.currentImage?.let {
            state.addQueue(it)
            state.currentImage = it.flipHorizontally()
        }
    }

    fun rotate(state: ApplicationState) {

        state.currentImage?.let {
            state.addQueue(it)
            state.currentImage = it.rotate(-90.0)
        }
    }

    fun resize(width:Int, height:Int, state: ApplicationState) {

        state.currentImage?.let {
            if (width == it.width && height == it.height) {
                return@let
            }

            val resizedImage = it.resize(width, height)
            state.addQueue(it)
            state.currentImage = resizedImage
        }
    }

    fun shearing(x:Float, y:Float, state: ApplicationState) {

        state.currentImage?.let {
            if (x == 0f && y == 0f) {
                return@let
            }

            state.scope.launchWithLoading {

                OpenCVManager.invokeCV(state, action = { byteArray ->
                    ImageProcess.shearing(byteArray, x, y)
                }, failure = { e ->
                    logger.error("shearing is failed", e)
                })
            }
        }
    }

    fun clearCropImageView() {
        cropTypesIndex.value = 0
        contentScalesIndex.value = 1

        cropFlag1.set(false)
        cropFlag2.set(false)
        rxCache.remove(KEY_CROP_FIRST)
        rxCache.remove(KEY_CROP_SECOND)
    }
}