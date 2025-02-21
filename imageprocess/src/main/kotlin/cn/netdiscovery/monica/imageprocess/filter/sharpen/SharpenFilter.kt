package cn.netdiscovery.monica.imageprocess.filter.sharpen

import cn.netdiscovery.monica.imageprocess.filter.base.ConvolveFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.sharpen.SharpenFilter
 * @author: Tony Shen
 * @date: 2024/5/5 20:56
 * @version: V1.0 <描述当前版本功能>
 */
class SharpenFilter: ConvolveFilter(sharpenMatrix) {

    companion object {
        private val sharpenMatrix = floatArrayOf(
            0.0f, -0.2f,  0.0f,
            -0.2f,  1.8f, -0.2f,
            0.0f, -0.2f,  0.0f
        )
    }
}