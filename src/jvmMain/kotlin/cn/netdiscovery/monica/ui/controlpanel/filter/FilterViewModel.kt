package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.FilterParam
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.collator
import filterNames
import org.slf4j.Logger
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.ext.get

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
 * @author: Tony Shen
 * @date: 2024/5/8 12:09
 * @version: V1.0 <描述当前版本功能>
 */

class FilterViewModel {

    private val logger: Logger = logger<FilterViewModel>()

    fun applyFilterParams(state: ApplicationState) {
        if (state.rawImageFile == null)
            return

        val filterName = filterNames[selectedIndex.value]
        val list = mutableListOf<Param>()
        tempMap.forEach { (t, u) ->
            val value = when(t.second) {
                "Int"    -> u.toDoubleOrNull()?.takeIf { it % 1 == 0.0 }?.toInt()?:0
                "Float"  -> u.toFloat()
                "Double" -> u.toDouble()
                else     -> u
            }

            list.add(Param(t.first, t.second, value))
        }

        // 按照参数名首字母进行排序
        list.sortWith { o1, o2 -> collator.compare(o1.key, o2.key); }

        logger.info("sort params: $list")

        val filterParam = rxCache.get<FilterParam>(filterName)?.data
        filterParam?.params = list
        rxCache.saveOrUpdate(filterName, filterParam)
    }
}