package cn.netdiscovery.monica.rxcache

import Monica.config.BuildConfig
import cn.netdiscovery.monica.config.workDirectory
import cn.netdiscovery.monica.utils.AppDirs
import com.safframework.rxcache.RxCache
import com.safframework.rxcache.ext.get
import com.safframework.rxcache.ext.persistence
import com.safframework.rxcache.memory.impl.FIFOMemoryImpl
import com.safframework.rxcache.persistence.okio.OkioImpl
import java.io.File

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.rxcache.RxCacheMananger
 * @author: Tony Shen
 * @date: 2024/4/28 20:03
 * @version: V1.0 <描述当前版本功能>
 */
val rxCache: RxCache by lazy {
    RxCache.config {
        RxCache.Builder().memory(FIFOMemoryImpl()).persistence {
            OkioImpl(AppDirs.cacheDir)
        }
    }

    RxCache.getRxCache()
}


fun getFilterParam(filterName:String):List<Param>? = rxCache.get<FilterParam>(filterName)?.data?.params

fun getFilterRemark(filterName:String):String? = rxCache.get<FilterParam>(filterName)?.data?.remark

fun clearData() = rxCache.clear()