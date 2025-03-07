package cn.netdiscovery.monica.utils.extensions

import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`Any+Extensions`
 * @author: Tony Shen
 * @date: 2025/3/7 14:24
 * @version: V1.0 <描述当前版本功能>
 */
fun Any.printConstructorParamsWithValues() {
    val kClass = this::class
    val constructor = kClass.primaryConstructor

    if (constructor != null) {
        val paramValues = constructor.parameters.associateWith { param ->
            val paramName = param.name ?: "unknown"
            val property = kClass.memberProperties.find { it.name == paramName }

            property?.let {
                it.isAccessible = true  // 允许访问 private 属性
                it.getter.call(this)
            }
        }

        println("${kClass.simpleName} constructor parameters:")
        paramValues.forEach { (param, value) ->
            println("${param.name} = $value")
        }
    }
}