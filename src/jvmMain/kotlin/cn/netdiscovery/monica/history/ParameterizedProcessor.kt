package cn.netdiscovery.monica.history

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.ParameterizedProcessor
 * @author: Tony Shen
 * @date:  2025/7/26 10:33
 * @version: V1.0 各处理模块需要实现的统一接口
 */
interface ParameterizedProcessor<T, R> {

    fun applyParams(params: T)

    fun getCurrentParams(): T

    fun process(): R
}