package cn.netdiscovery.monica.imageprocess.math

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.math.Functions
 * @author: Tony Shen
 * @date: 2025/3/10 11:49
 * @version: V1.0 <描述当前版本功能>
 */
interface Function1D {
    fun evaluate(x: Float): Float
}

interface Function2D {
    fun evaluate(x: Float, y: Float): Float
}

interface Function3D {
    fun evaluate(x: Float, y: Float, z: Float): Float
}