package cn.netdiscovery.monica.rxcache

import cn.netdiscovery.monica.config.KEY_FILTER_REMARK
import com.safframework.rxcache.ext.get

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterConfig
 * @author: Tony Shen
 * @date: 2024/4/27 13:59
 * @version: V1.0 <描述当前版本功能>
 */
data class FilterParam(val name:String, val remark:String? = null,  var params: List<Triple<String,String,Any>>)

private val filters: MutableList<FilterParam> by lazy {
    mutableListOf<FilterParam>().apply {
        this.add(FilterParam("AverageFilter", params = mutableListOf()))
        this.add(FilterParam("BilateralFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("ds","Double",1.0))
            this.add(Triple("rs","Double",1.0))
        }))
        this.add(FilterParam("BlockFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("blockSize","Int",2))
        }))
        this.add(FilterParam("BoxBlurFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("BumpFilter", params = mutableListOf()))
        this.add(FilterParam("ColorFilter", remark = "ColorFilter 支持选择0-11，共12种风格。\n" +
                "0:AUTUMN,1:BONE,2:COOL,3:HOT,4:HSV,5:JET,6:OCEAN\n" +
                "7:PINK,8:RAINBOW,9:SPRING,10:SUMMER,11:WINTER",
        params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("style","Int",0))
        }))
        this.add(FilterParam("ConBriFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("brightness","Float",1.0f))
            this.add(Triple("contrast","Float",1.5f))
        }))
        this.add(FilterParam("CropFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("x","Int",0))
            this.add(Triple("y","Int",0))
            this.add(Triple("w","Int",32))
            this.add(Triple("h","Int",32))
        }))
        this.add(FilterParam("EmbossFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("colorConstant","Int",100))
        }))
        this.add(FilterParam("GammaFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("gamma","Double",0.5))
        }))
        this.add(FilterParam("GaussianFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",5.0f))
        }))
        this.add(FilterParam("GradientFilter", params = mutableListOf()))
        this.add(FilterParam("GrayFilter", params = mutableListOf()))
        this.add(FilterParam("HighPassFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",10f))
        }))
        this.add(FilterParam("LaplaceSharpenFilter", params = mutableListOf()))
        this.add(FilterParam("MotionFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("angle","Float",0f))
            this.add(Triple("distance","Float",0f))
            this.add(Triple("zoom","Float",0.4f))
        }))
        this.add(FilterParam("OilPaintFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("intensity","Int",40))
            this.add(Triple("ksize","Int",10))
        }))
        this.add(FilterParam("SepiaToneFilter", params = mutableListOf()))
        this.add(FilterParam("SharpenFilter", params = mutableListOf()))
        this.add(FilterParam("SpotlightFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("factor","Int",1))
        }))
        this.add(FilterParam("StrokeAreaFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("size","Double",10.0))
        }))
        this.add(FilterParam("USMFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("radius","Float",2.0f))
            this.add(Triple("amount","Float",0.5f))
            this.add(Triple("threshold","Int",1))
        }))
        this.add(FilterParam("VariableBlurFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("VignetteFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("fade","Int",35))
            this.add(Triple("vignetteWidth","Int",50))
        }))
        this.add(FilterParam("WhiteImageFilter", params = mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("beta","Double",1.1))
        }))
    }
}

fun saveFilterParams(){
    filters.forEach {
        rxCache.saveOrUpdate(it.name, it.params)
    }
}

fun saveFilterRemark(){
    filters.forEach {
        rxCache.saveOrUpdate(KEY_FILTER_REMARK + it.name, it.remark)
    }
}

fun getFilterNames(): List<String> = filters.map { it.name }

fun getFilterParam(filterName:String): List<Triple<String,String,Any>>? =
    rxCache.get<List<Triple<String,String,Any>>>(filterName)?.data

fun getFilterRemark(filterName:String):String? {
    return rxCache.get<String>(KEY_FILTER_REMARK + filterName)?.data
}