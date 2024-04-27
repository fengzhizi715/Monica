package cn.netdiscovery.monica.ui

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.FilterConfig
 * @author: Tony Shen
 * @date: 2024/4/27 13:59
 * @version: V1.0 <描述当前版本功能>
 */
data class FilterParam(val name:String,val params: Map<String,String>)

private val filterList by lazy {
    mutableListOf<FilterParam>().apply {
        this.add(FilterParam("BoxBlurFilter", mutableMapOf<String, String>().apply {
            this["hRadius"] = "Int"
            this["vRadius"] = "Int"
            this["iterations"] = "Int"
        }))
        this.add(FilterParam("ConBriFilter", mutableMapOf<String, String>().apply {
            this["contrast"] = "Float"
            this["brightness"] = "Float"
        }))
    }
}


fun getFilterNames(): List<String> {
    return filterList.map {
        it.name
    }
}

fun getFilterParam(index:Int):FilterParam {
   return filterList[index]
}