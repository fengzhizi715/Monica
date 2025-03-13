package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransferFilter
import cn.netdiscovery.monica.imageprocess.math.bias
import cn.netdiscovery.monica.imageprocess.math.gain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.GainFilter
 * @author: Tony Shen
 * @date: 2025/3/13 10:52
 * @version: V1.0 <描述当前版本功能>
 */
class GainFilter(private val gain:Float = 0.5f, private val bias:Float = 0.5f): TransferFilter() {

    override fun transferFunction(v: Float): Float {
        var f = v
        f = gain(f, gain)
        f = bias(f, bias)
        return f
    }
}