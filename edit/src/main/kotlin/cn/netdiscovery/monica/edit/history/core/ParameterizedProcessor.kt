package cn.netdiscovery.monica.edit.history.core

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.edit.history.core.ParameterizedProcessor
 * @author: Tony Shen
 * @date:  2025/7/26 10:33
 * @version: V1.0 <描述当前版本功能>
 */
interface ParameterizedProcessor<T> {

    fun applyParams(params: T)

    fun getCurrentParams(): T
}