package cn.netdiscovery.monica.imageprocess.filter

import cn.netdiscovery.monica.imageprocess.filter.base.TransferFilter

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.filter.SolarizeFilter
 * @author: Tony Shen
 * @date: 2025/3/19 20:44
 * @version: V1.0 <描述当前版本功能>
 */
class SolarizeFilter: TransferFilter() {

    override fun transferFunction(v: Float): Float {
        return if (v > 0.5f) 2 * (v - 0.5f) else 2 * (0.5f - v)
    }
}