package cn.netdiscovery.monica.rxcache

import cn.netdiscovery.monica.opencv.ImageProcess
import com.safframework.rxcache.ext.get
import com.safframework.rxcache.reflect.TypeToken
import com.safframework.rxcache.utils.GsonUtils
import java.io.File
import java.io.Serializable

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterConfig
 * @author: Tony Shen
 * @date: 2024/4/27 13:59
 * @version: V1.0 <描述当前版本功能>
 */
data class FilterParam(
    val name: String,
    val remark: String?,
    var params: List<Param>
): Serializable

// 参数对应的数据类
data class Param(
    val key: String,
    val type: String,
    val value: Any
): Serializable

private val filters: List<FilterParam> by lazy {

    val fileName = ImageProcess.resourcesDir.resolve("filterConfig.json").absolutePath
    val jsonContent = File(fileName).readText(Charsets.UTF_8)
    val type = object : TypeToken<List<FilterParam>>() {}.type

    val list: List<FilterParam> = GsonUtils.fromJson(jsonContent, type)

    list
}

fun saveFilterParamsAndRemark(){
    filters.forEach {
        rxCache.saveOrUpdate(it.name, it)
    }
}

fun getFilterNames():List<String> = filters.map { it.name }

fun getFilterParam(filterName:String):List<Param>? = rxCache.get<FilterParam>(filterName)?.data?.params

fun getFilterRemark(filterName:String):String? = rxCache.get<FilterParam>(filterName)?.data?.remark