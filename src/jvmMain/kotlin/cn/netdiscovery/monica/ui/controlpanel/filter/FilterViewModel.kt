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
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import java.util.*
import kotlin.math.min
import kotlin.math.max

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
    var previewJob:Job? = null

    private data class PreviewCacheKey(
        val baseImageId: Int,
        val filterName: String,
        val paramsHash: Int
    )

    private data class CachedPreview(
        val image: BufferedImage,
        val approxBytes: Long
    )

    private val previewCacheLock = Any()
    private val previewCacheMaxEntries = 80
    private val previewCacheMaxBytes = 256L * 1024L * 1024L // 256MB
    private var previewCacheBytes: Long = 0
    private val previewCache: LinkedHashMap<PreviewCacheKey, CachedPreview> =
        object : LinkedHashMap<PreviewCacheKey, CachedPreview>(64, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<PreviewCacheKey, CachedPreview>?): Boolean {
                return size > previewCacheMaxEntries
            }
        }

    private fun approxImageBytes(image: BufferedImage): Long {
        // 估算：每像素 4 bytes，做上限保护防止溢出
        val pixels = image.width.toLong() * image.height.toLong()
        return min(pixels * 4L, Long.MAX_VALUE / 4L)
    }

    private fun buildParamsHash(paramMap: Map<Pair<String, String>, String>): Int {
        // 需要稳定：排序后 hash
        val entries = paramMap.entries
            .sortedWith(compareBy({ it.key.first.lowercase() }, { it.key.second }, { it.value }))
        var acc = 1
        for (e in entries) {
            acc = 31 * acc + e.key.first.hashCode()
            acc = 31 * acc + e.key.second.hashCode()
            acc = 31 * acc + e.value.hashCode()
        }
        return acc
    }

    private fun clampIntParam(key: String, value: Int): Int {
        // 兜底：某些滤镜会把参数用作 step，必须 >0
        return when (key) {
            "blockSize" -> max(1, value)
            else -> value
        }
    }

    private fun getCachedPreview(key: PreviewCacheKey): BufferedImage? {
        synchronized(previewCacheLock) {
            return previewCache[key]?.image
        }
    }

    private fun putCachedPreview(key: PreviewCacheKey, image: BufferedImage) {
        val bytes = approxImageBytes(image)
        synchronized(previewCacheLock) {
            // 若已有旧值，先扣掉
            previewCache.remove(key)?.let { old ->
                previewCacheBytes -= old.approxBytes
            }
            previewCache[key] = CachedPreview(image = image, approxBytes = bytes)
            previewCacheBytes += bytes

            // 先按 entry 数做一次淘汰（LinkedHashMap 自带）
            while (previewCache.size > previewCacheMaxEntries) {
                val eldestKey = previewCache.entries.firstOrNull()?.key ?: break
                previewCache.remove(eldestKey)?.let { removed ->
                    previewCacheBytes -= removed.approxBytes
                }
            }

            // 再按内存上限做淘汰
            while (previewCacheBytes > previewCacheMaxBytes && previewCache.isNotEmpty()) {
                val eldestKey = previewCache.entries.firstOrNull()?.key ?: break
                previewCache.remove(eldestKey)?.let { removed ->
                    previewCacheBytes -= removed.approxBytes
                }
            }
        }
    }

    /**
     * 保存滤镜参数，并调用滤镜效果
     */
    fun applyFilter(
        state: ApplicationState,
        index: Int,
        paramMap: Map<Pair<String, String>, String>,
        sourceImage: BufferedImage? = null,
        pushHistory: Boolean = true
    ) {
        job = state.scope.launchWithSuspendLoading {
            val baseImage = sourceImage ?: state.rawImage ?: state.currentImage
            if (baseImage == null) return@launchWithSuspendLoading
            val tempImage = state.currentImage ?: baseImage

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

            state.currentImage = doFilter(
                filterName = filterName,
                array = array,
                image = baseImage
            )

            if (pushHistory) {
                state.addQueue(tempImage)
            }
        }
    }

    /**
     * 应用滤镜预览（不保存到历史记录，用于实时预览）
     */
    fun applyFilterPreview(
        state: ApplicationState,
        index: Int,
        paramMap: Map<Pair<String, String>, String>,
        debounceMs: Long = 0,
        onSuccess: (java.awt.image.BufferedImage) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        // 取消之前的预览任务
        previewJob?.cancel()
        
        previewJob = state.scope.launch {
            try {
                if (debounceMs > 0) {
                    delay(debounceMs)
                }
                // 使用原始图像进行预览，如果没有原始图像则使用当前图像
                val sourceImage = state.rawImage ?: state.currentImage
                if (sourceImage == null) {
                    return@launch
                }

                val filterName = filterNames[index]

                // Preview cache：相同滤镜 + 相同参数 + 相同基线图 -> 命中
                val cacheKey = PreviewCacheKey(
                    baseImageId = System.identityHashCode(sourceImage),
                    filterName = filterName,
                    paramsHash = buildParamsHash(paramMap)
                )
                getCachedPreview(cacheKey)?.let { cached ->
                    onSuccess(cached)
                    return@launch
                }

                val list = mutableListOf<Param>()
                paramMap.forEach { (t, u) ->
                    val value = when(t.second) {
                        "Int"    -> clampIntParam(t.first, u.safelyConvertToInt() ?: 0)
                        "Float"  -> u.toFloat()
                        "Double" -> u.toDouble()
                        else     -> u
                    }

                    list.add(Param(t.first, t.second, value))
                }

                // 按照参数名首字母进行排序
                list.sortWith { o1, o2 -> collator.compare(o1.key, o2.key); }

                val array: MutableList<Any> = list.map { it.value }.toMutableList()

                // 预览只处理图像，不触碰 state.currentImage，避免 UI 闪烁/并发风险
                val previewResult = doFilter(
                    filterName = filterName,
                    array = array,
                    image = sourceImage
                )
                putCachedPreview(cacheKey, previewResult)
                onSuccess(previewResult)
            } catch (e: Exception) {
                logger.error("Preview filter failed", e)
                onError(e)
            }
        }
    }

    fun clear() {
        if (job !=null && !job!!.isCancelled) {
            job?.cancel()
        }
        if (previewJob !=null && !previewJob!!.isCancelled) {
            previewJob?.cancel()
        }
        synchronized(previewCacheLock) {
            previewCache.clear()
            previewCacheBytes = 0
        }
    }
}