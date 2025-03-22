package cn.netdiscovery.monica.imageprocess

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.Colormap
 * @author: Tony Shen
 * @date:  2025/3/22 15:05
 * @version: V1.0 <描述当前版本功能>
 */
interface Colormap {
    /**
     * Convert a value in the range 0..1 to an RGB color.
     * @param v a value in the range 0..1
     * @return an RGB color
     */
    fun getColor(v: Float): Int
}