package cn.netdiscovery.monica.ui.preview

import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.selectedIndex
import cn.netdiscovery.monica.utils.clickLoadingDisplayWithSuspend
import cn.netdiscovery.monica.utils.doFilter
import cn.netdiscovery.monica.utils.hsl
import filterNames
import kotlinx.coroutines.launch
import java.text.Collator
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.preview.PreviewViewModel
 * @author: Tony Shen
 * @date: 2024/5/7 20:30
 * @version: V1.0 <描述当前版本功能>
 */
class PreviewViewModel {

    fun recoverImage(state: ApplicationState) {
        state.currentImage = state.rawImage
        state.clearQueue()
    }

    fun getLastImage(state: ApplicationState) {
        val lastImage = state.getLastImage()
        if (lastImage!=null)
            state.currentImage = lastImage
    }

    fun previewImage(state: ApplicationState) {
        state.scope.launch {
            clickLoadingDisplayWithSuspend {
                if (state.isHLS) {
                    state.currentImage = hsl(state.currentImage!!, state.saturation, state.hue, state.luminance)
                }

                if(state.isFilter) {
                    if (selectedIndex.value == 0) return@clickLoadingDisplayWithSuspend

                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName)

                    if (params!=null) {
                        // 按照参数名首字母进行排序
                        Collections.sort(params) { o1, o2 -> Collator.getInstance(Locale.UK).compare(o1.first, o2.first) }
                        println("sort params: $params")
                    }

                    val array = mutableListOf<Any>()

                    params?.forEach {
                        array.add(it.third)
                    }

                    println("filterName: $filterName, array: $array")

                    state.addQueue(state.currentImage!!)
                    state.currentImage = doFilter(filterName,array,state)
                }
            }
        }
    }
}