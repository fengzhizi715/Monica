package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.collator
import filterNames
import java.text.Collator
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 12:09
 * @version: V1.0 <描述当前版本功能>
 */
class FilterViewModel {

    fun applyFilterParams(state: ApplicationState) {
        if (state.rawImageFile == null)
            return

        val filterName = filterNames[selectedIndex.value]
        val list = mutableListOf<Triple<String,String,Any>>()
        tempMap.forEach { (t, u) ->
            val value = when(t.second) {
                "Int"    -> u.toInt()
                "Float"  -> u.toFloat()
                "Double" -> u.toDouble()
                else     -> u
            }

            list.add(Triple(t.first, t.second, value))
        }

        // 按照参数名首字母进行排序
        list.sortWith { o1, o2 -> collator.compare(o1.first, o2.first); }

        println("sort params: $list")
        rxCache.saveOrUpdate(filterName, list)
    }
}