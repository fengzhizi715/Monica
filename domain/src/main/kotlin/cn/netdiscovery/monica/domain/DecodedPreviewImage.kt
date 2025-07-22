package cn.netdiscovery.monica.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.domain.DecodedPreviewImage
 * @author: Tony Shen
 * @date: 2025/7/21 12:40
 * @version: V1.0 <描述当前版本功能>
 */
data class DecodedPreviewImage(
    val nativePtr: Long,  // 对应 MonicaImageProcess 中 PyramidImage 对象的指针地址
    val width: Int,
    val height: Int,
    val previewImage: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecodedPreviewImage

        if (nativePtr != other.nativePtr) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (!previewImage.contentEquals(other.previewImage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nativePtr.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + previewImage.contentHashCode()
        return result
    }
}
