package cn.netdiscovery.monica.utils.extensions

import cn.netdiscovery.monica.utils.loadingDisplay
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extensions.`Coroutine+Extensions`
 * @author: Tony Shen
 * @date: 2024/8/28 18:28
 * @version: V1.0 <描述当前版本功能>
 */
fun CoroutineScope.launchWithLoading(block:()->Unit) {

    this.launch(IO) {
        loadingDisplay(block)
    }
}