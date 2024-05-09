package cn.netdiscovery.monica.rxcache

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
        this.add(FilterParam("AverageFilter", mutableListOf()))
        this.add(FilterParam("BilateralFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("ds","Double",1.0))
            this.add(Triple("rs","Double",1.0))
        }))
        this.add(FilterParam("BlockFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("blockSize","Int",2))
        }))
        this.add(FilterParam("BoxBlurFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("BumpFilter", mutableListOf()))
        this.add(FilterParam("ConBriFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("brightness","Float",1.0f))
            this.add(Triple("contrast","Float",1.5f))
        }))
        this.add(FilterParam("CropFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("x","Int",0))
            this.add(Triple("y","Int",0))
            this.add(Triple("w","Int",32))
            this.add(Triple("h","Int",32))
        }))
        this.add(FilterParam("EmbossFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("colorConstant","Int",100))
        }))
        this.add(FilterParam("GammaFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("gamma","Double",0.5))
        }))
        this.add(FilterParam("GaussianFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",5.0f))
        }))
        this.add(FilterParam("GradientFilter", mutableListOf()))
        this.add(FilterParam("GrayFilter", mutableListOf()))
        this.add(FilterParam("HighPassFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",10f))
        }))
        this.add(FilterParam("LaplaceSharpenFilter", mutableListOf()))
        this.add(FilterParam("MotionFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("angle","Float",0f))
            this.add(Triple("distance","Float",0f))
            this.add(Triple("zoom","Float",0.4f))
        }))
        this.add(FilterParam("OilPaintFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("intensity","Int",40))
            this.add(Triple("ksize","Int",10))
        }))
        this.add(FilterParam("SepiaToneFilter", mutableListOf()))
        this.add(FilterParam("SharpenFilter", mutableListOf()))
        this.add(FilterParam("SpotlightFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("factor","Int",1))
        }))
        this.add(FilterParam("USMFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",2.0f))
            this.add(Triple("amount","Float",0.5f))
            this.add(Triple("threshold","Int",1))
        }))
        this.add(FilterParam("VariableBlurFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("WhiteImageFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("beta","Double",1.1))
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