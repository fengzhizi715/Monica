package cn.netdiscovery.monica.utils.extension

import cn.netdiscovery.monica.utils.loadingDisplay
import com.safframework.kotlin.coroutines.IO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.extension.`Coroutine+Extensions`
 * @author: Tony Shen
 * @date: 2024/8/28 18:28
 * @version: V1.0 <描述当前版本功能>
 */
fun CoroutineScope.launchWithLoading(block:()->Unit) {

    this.launch(IO) {
        loadingDisplay(block)
    }
}