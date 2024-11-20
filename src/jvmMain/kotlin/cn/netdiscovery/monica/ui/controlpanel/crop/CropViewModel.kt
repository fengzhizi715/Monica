package cn.netdiscovery.monica.ui.controlpanel.crop

import cn.netdiscovery.monica.config.KEY_CROP_FIRST
import cn.netdiscovery.monica.config.KEY_CROP_SECOND
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.contentScalesIndex
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag1
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropFlag2
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.cropTypesIndex
import cn.netdiscovery.monica.utils.logger
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

    fun clearCropImageView() {
        cropTypesIndex.value = 0
        contentScalesIndex.value = 1

        cropFlag1.set(false)
        cropFlag2.set(false)
        rxCache.remove(KEY_CROP_FIRST)
        rxCache.remove(KEY_CROP_SECOND)
    }
}