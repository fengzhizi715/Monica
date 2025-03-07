package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.FilterParam
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.collator
import cn.netdiscovery.monica.utils.doFilter
import cn.netdiscovery.monica.utils.extension.safelyConvertToInt
import cn.netdiscovery.monica.utils.loadingDisplayWithSuspend
import filterNames
import org.slf4j.Logger
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.ext.get
import kotlinx.coroutines.launch
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

    private val logger: Logger = logger<FilterViewModel>()

    fun updateFilterParams(state: ApplicationState) {
        if (state.rawImageFile == null)
            return

        val filterName = filterNames[selectedIndex.value]
        val list = mutableListOf<Param>()
        tempMap.forEach { (t, u) ->
            val value = when(t.second) {
                "Int"    -> u.safelyConvertToInt()?:0
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

    fun applyFilter(state:ApplicationState) {
        state.scope.launch {
            loadingDisplayWithSuspend {
                val tempImage = state.currentImage!!

                val filterName = filterNames[selectedIndex.value]

                val params = getFilterParam(filterName) // 从缓存中获取滤镜的参数信息

                if (params!=null) {
                    // 按照参数名首字母进行排序
                    Collections.sort(params) { o1, o2 -> collator.compare(o1.key, o2.key) }
                    logger.info("filterName: $filterName, sort params: $params")
                }

                val array = mutableListOf<Any>()

                params?.forEach {

                    val value = when(it.type) {
                        "Int"    -> it.value.toString().safelyConvertToInt()?:0
                        "Float"  -> it.value.toString().toFloat()
                        "Double" -> it.value.toString().toDouble()
                        else     -> it.value
                    }

                    array.add(value)
                }

                logger.info("filterName: $filterName, array: $array")

                state.currentImage = doFilter(filterName,array,state)

                state.addQueue(tempImage)
            }
        }
    }
}