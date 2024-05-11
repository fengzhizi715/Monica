package cn.netdiscovery.monica.ui.preview

import cn.netdiscovery.monica.imageprocess.saveImage
import cn.netdiscovery.monica.rxcache.getFilterParam
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.filter.selectedIndex
import cn.netdiscovery.monica.utils.*
import filterNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.Collator
import java.util.*
import javax.swing.JFileChooser

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
                if (state.currentImage == null)
                    return@clickLoadingDisplayWithSuspend

                if (!state.isHLS && (!state.isFilter || (state.isFilter && selectedIndex.value == 0)))  {
                    return@clickLoadingDisplayWithSuspend
                }

                var tempImage = state.currentImage!!

                if (state.isHLS) {
                    state.currentImage = hsl(state.currentImage!!, state.saturation, state.hue, state.luminance)
                }

                if(state.isFilter) {
                    val filterName = filterNames[selectedIndex.value]

                    val params = getFilterParam(filterName)

                    if (params!=null) {
                        // 按照参数名首字母进行排序
                        Collections.sort(params) { o1, o2 -> collator.compare(o1.first, o2.first) }
                        println("sort params: $params")
                    }

                    val array = mutableListOf<Any>()

                    params?.forEach {
                        array.add(it.third)
                    }

                    println("filterName: $filterName, array: $array")

                    state.currentImage = doFilter(filterName,array,state)
                }

                state.addQueue(tempImage)
            }
        }
    }

    fun saveImage(state: ApplicationState) {
        showFileSelector(
            isMultiSelection = false,
            selectionMode = JFileChooser.DIRECTORIES_ONLY,
            selectionFileFilter = null
        ) {
            state.scope.launch(Dispatchers.IO) {
                val outputPath = it[0].absolutePath
                val saveFile = File(outputPath).getUniqueFile(state.rawImageFile?: File("${currentTime()}.png"))
                state.currentImage!!.saveImage(saveFile)
                state.showTray(msg = "保存成功（${outputPath}）")
            }
        }
    }

    fun clearImage(state: ApplicationState) {
        state.clearImage()
    }
}