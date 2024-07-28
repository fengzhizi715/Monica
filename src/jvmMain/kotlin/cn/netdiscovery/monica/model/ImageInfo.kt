package cn.netdiscovery.monica.model

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.model.ImageInfo
 * @author: Tony Shen
 * @date: 2024/7/28 12:17
 * @version: V1.0 <描述当前版本功能>
 */
data class ImageInfo(val width:Int, val height:Int, val byteArray:ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageInfo

        if (width != other.width) return false
        if (height != other.height) return false
        return byteArray.contentEquals(other.byteArray)
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + byteArray.contentHashCode()
        return result
    }
}