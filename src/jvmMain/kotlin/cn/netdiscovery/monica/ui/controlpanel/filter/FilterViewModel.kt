package cn.netdiscovery.monica.ui.controlpanel.filter

import cn.netdiscovery.monica.rxcache.FilterParam
import cn.netdiscovery.monica.rxcache.Param
import cn.netdiscovery.monica.rxcache.rxCache
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.collator
import cn.netdiscovery.monica.utils.doFilter
import cn.netdiscovery.monica.utils.extensions.launchWithSuspendLoading
import cn.netdiscovery.monica.utils.extensions.safelyConvertToInt
import filterNames
import org.slf4j.Logger
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.ext.get
import kotlinx.coroutines.Job
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
    var job:Job? = null

    /**
     * 保存滤镜参数，并调用滤镜效果
     */
    fun applyFilter(state:ApplicationState, index:Int, paramMap:HashMap<Pair<String, String>, String>) {
        job = state.scope.launchWithSuspendLoading {
            val tempImage = state.currentImage!!

            val filterName = filterNames[index]

            val list = mutableListOf<Param>()
            paramMap.forEach { (t, u) ->
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

            val filterParam = rxCache.get<FilterParam>(filterName)?.data
            filterParam?.params = list
            rxCache.saveOrUpdate(filterName, filterParam) // 保存滤镜参数

            val array:MutableList<Any> = list.map { it.value }.toMutableList()
            logger.info("filterName: $filterName, array: $array")

            state.currentImage = doFilter(filterName,array,state)

            state.addQueue(tempImage)
        }
    }

    fun clear() {
        filterSelectedIndex.value = -1
        filterTempMap.clear()
        job?.cancel()
    }
}