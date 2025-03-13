package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransferFilter
import kotlin.math.exp

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.ExposureFilter
 * @author: Tony Shen
 * @date: 2025/3/12 18:10
 * @version: V1.0 <描述当前版本功能>
 */
class ExposureFilter(private val exposure:Float = 1f): TransferFilter(){

    override fun transferFunction(f: Float): Float {
        return 1 - exp((-f * exposure).toDouble()).toFloat()
    }
}