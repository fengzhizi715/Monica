package cn.netdiscovery.monica.rxcache

import cn.netdiscovery.monica.config.workDirectory
import com.safframework.rxcache.RxCache
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
            val cacheDirectory = File(workDirectory,"rxcache") // rxCache 持久层存放地址
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdirs()
            }

            OkioImpl(cacheDirectory)
        }
    }

    RxCache.getRxCache()
}