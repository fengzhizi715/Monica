package cn.netdiscovery.monica.ui

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterConfig
 * @author: Tony Shen
 * @date: 2024/4/27 13:59
 * @version: V1.0 <描述当前版本功能>
 */
data class FilterParam(val name:String,val params: List<Triple<String,String,Any>>)

private val filters: MutableList<FilterParam> by lazy {
    mutableListOf<FilterParam>().apply {
        this.add(FilterParam("BoxBlurFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("hRadius","Int",5))
            this.add(Triple("vRadius","Int",5))
            this.add(Triple("iterations","Int",1))
        }))
        this.add(FilterParam("ConBriFilter", mutableListOf<Triple<String,String,Any>>().apply {
            this.add(Triple("contrast","Float",1.5f))
            this.add(Triple("brightness","Float",1.0f))
        }))
    }
}

fun filterParamMap(): MutableMap<String, Any> {

    val result = mutableMapOf<String,Any>()

    filters.forEach {filterParam->
        filterParam.params.forEach {
            result["${filterParam.name}_${it.first}"] = it.third
        }
    }

    return result
}

fun getFilterNames(): List<String> = filters.map { it.name }

fun getFilterParam(index:Int):FilterParam = filters[index]