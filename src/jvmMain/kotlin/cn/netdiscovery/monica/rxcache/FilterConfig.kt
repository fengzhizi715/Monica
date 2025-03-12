package cn.netdiscovery.monica.rxcache

import cn.netdiscovery.monica.opencv.ImageProcess
import com.safframework.rxcache.reflect.TypeToken
import com.safframework.rxcache.utils.GsonUtils
import filterMaps
import java.io.File

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
    val desc: String?,
    val remark: String?,
    var params: List<Param>
)

// 参数对应的数据类
data class Param(
    val key: String,
    val type: String,
    val value: Any
)

private val filters: List<FilterParam> by lazy {

    val fileName = ImageProcess.resourcesDir.resolve("filterConfig.json").absolutePath
    val jsonContent = File(fileName).readText(Charsets.UTF_8)
    val type = object : TypeToken<List<FilterParam>>() {}.type

    val list: List<FilterParam> = GsonUtils.fromJson(jsonContent, type)

    list
}

fun initFilterParamsConfig(){

    filterMaps.clear()
    filters.forEach {
        rxCache.saveOrUpdate(it.name, it)
        filterMaps[it.name] = it.desc?:""
    }
}

fun initFilterMap() {
    filters.forEach {
        filterMaps[it.name] = it.desc?:""
    }
}

fun getFilterNames():List<String> = filters.map { it.name }