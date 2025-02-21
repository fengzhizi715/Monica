package cn.netdiscovery.monica.imageprocess.filter.sharpen

import cn.netdiscovery.monica.imageprocess.filter.base.ConvolveFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.sharpen.LaplaceSharpenFilter
 * @author: Tony Shen
 * @date: 2024/5/5 21:05
 * @version: V1.0 <描述当前版本功能>
 */
class LaplaceSharpenFilter: ConvolveFilter(sharpenMatrix) {

    companion object {
        private val sharpenMatrix = floatArrayOf(
            -1f, -1f,  -1f,
            -1f,  9f, -1f,
            -1f, -1f,  -1f,
        )
    }
}