package cn.netdiscovery.monica.utils

import java.text.Collator
import java.util.*

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.TextUtils
 * @author: Tony Shen
 * @date: 2024/5/11 14:05
 * @version: V1.0 <描述当前版本功能>
 */
val collator:Collator by lazy {
    Collator.getInstance(Locale.UK)
}