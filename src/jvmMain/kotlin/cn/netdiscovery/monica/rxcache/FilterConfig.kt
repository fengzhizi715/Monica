package cn.netdiscovery.monica.rxcache

import cn.netdiscovery.monica.imageprocess.filter.GradientFilter
import com.safframework.rxcache.ext.get

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterConfig
 * @author: Tony Shen
 * @date: 2024/4/27 13:59
 * @version: V1.0 <描述当前版本功能>
 */
data class FilterParam(var name:String,var params: List<Triple<String,String,Any>>)

private val filters: MutableList<FilterParam> by lazy {
    mutableListOf<FilterParam>().apply {
        this.add(FilterParam("BilateralFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("ds","Double",1.0))
            this.add(Triple("rs","Double",1.0))
        }))
        this.add(FilterParam("BoxBlurFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("ConBriFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("contrast","Float",1.5f))
            this.add(Triple("brightness","Float",1.0f))
        }))
        this.add(FilterParam("GammaFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("gamma","Double",0.5))
        }))
        this.add(FilterParam("GaussianFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",5.0f))
        }))
        this.add(FilterParam("GradientFilter", mutableListOf()))
        this.add(FilterParam("GrayFilter", mutableListOf()))
        this.add(FilterParam("SpotlightFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("factor","Int",1))
        }))
    }
}

fun saveFilterParams(){
    filters.forEach {
        rxCache.saveOrUpdate(it.name, it.params)
    }
}

fun getFilterNames(): List<String> = filters.map { it.name }

fun getFilterParam(filterName:String): List<Triple<String,String,Any>>? =
    rxCache.get<List<Triple<String,String,Any>>>(filterName)?.data