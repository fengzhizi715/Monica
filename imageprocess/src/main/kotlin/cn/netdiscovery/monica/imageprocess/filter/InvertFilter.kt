package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.PointFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.InvertFilter
 * @author: Tony Shen
 * @date: 2025/3/11 21:16
 * @version: V1.0 <描述当前版本功能>
 */
class InvertFilter:PointFilter() {

    init {
        canFilterIndexColorModel = true
    }

    override fun filterRGB(x: Int, y: Int, rgb: Int): Int {
        val a = rgb and 0xff000000.toInt()
        return a or (rgb.inv() and 0x00ffffff)
    }
}