package cn.netdiscovery.monica.imageprocess.filter.blur

import cn.netdiscovery.monica.imageprocess.filter.base.ConvolveFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.blur.AverageFilter
 * @author: Tony Shen
 * @date: 2024/5/5 19:17
 * @version: V1.0 <描述当前版本功能>
 */
class AverageFilter: ConvolveFilter(matrix) {

    companion object {
        private val matrix = floatArrayOf(
            0.1f, 0.1f, 0.1f,
            0.1f, 0.2f, 0.1f,
            0.1f, 0.1f, 0.1f
        )
    }
}