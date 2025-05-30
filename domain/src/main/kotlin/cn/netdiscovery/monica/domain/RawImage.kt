package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.domain.RawImage
 * @author: Tony Shen
 * @date: 2025/5/30 14:48
 * @version: V1.0 <描述当前版本功能>
 */
data class RawImage(val data: ByteArray, val width: Int, val height: Int, val channels: Int)