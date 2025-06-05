package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.domain.HeifImage
 * @author: Tony Shen
 * @date: 2025/6/4 20:21
 * @version: V1.0 <描述当前版本功能>
 */
data class HeifImage(
    val width: Int,
    val height: Int,
    val pixels: IntArray // ARGB 格式
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as HeifImage

        if (width != other.width) return false
        if (height != other.height) return false
        if (!pixels.contentEquals(other.pixels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + pixels.contentHashCode()
        return result
    }
}
