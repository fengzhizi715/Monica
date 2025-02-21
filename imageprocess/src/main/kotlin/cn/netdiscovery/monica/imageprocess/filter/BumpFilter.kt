package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.ConvolveFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.BumpFilter
 * @author: Tony Shen
 * @date: 2024/5/5 18:06
 * @version: V1.0 <描述当前版本功能>
 */

class BumpFilter : ConvolveFilter(embossMatrix) {

    companion object {
        private val embossMatrix = floatArrayOf(
            -1.0f, -1.0f, 0.0f,
            -1.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f
        )
    }
}