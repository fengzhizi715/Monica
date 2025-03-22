package cn.netdiscovery.monica.imageprocess.domain

import cn.netdiscovery.monica.imageprocess.Colormap
import cn.netdiscovery.monica.imageprocess.math.mixColors


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.domain.ArrayColormap
 * @author: Tony Shen
 * @date:  2025/3/22 15:07
 * @version: V1.0 <描述当前版本功能>
 */
open class ArrayColormap(var map: IntArray = IntArray(256)) : Colormap, Cloneable {

    /**
     * Convert a value in the range 0..1 to an RGB color.
     * @param v a value in the range 0..1
     * @return an RGB color
     * @see .setColor
     */
    override fun getColor(v: Float): Int {
        /*
		v *= 255;
		int n = (int)v;
		float f = v-n;
		if (n < 0)
			return map[0];
		else if (n >= 255)
			return map[255];
		return ImageMath.mixColors(f, map[n], map[n+1]);
*/
        var n = (v * 255).toInt()
        if (n < 0) n = 0
        else if (n > 255) n = 255
        return map[n]
    }

    /**
     * Set the color at "index" to "color". Entries are interpolated linearly from
     * the existing entries at "firstIndex" and "lastIndex" to the new entry.
     * firstIndex < index < lastIndex must hold.
     * @param index the position to set
     * @param firstIndex the position of the first color from which to interpolate
     * @param lastIndex the position of the second color from which to interpolate
     * @param color the color to set
     */
    fun setColorInterpolated(index: Int, firstIndex: Int, lastIndex: Int, color: Int) {
        val firstColor = map[firstIndex]
        val lastColor = map[lastIndex]
        for (i in firstIndex..index) map[i] = mixColors((i - firstIndex).toFloat() / (index - firstIndex), firstColor, color)
        for (i in index until lastIndex) map[i] = mixColors((i - index).toFloat() / (lastIndex - index), color, lastColor)
    }

    /**
     * Set a range of the colormap, interpolating between two colors.
     * @param firstIndex the position of the first color
     * @param lastIndex the position of the second color
     * @param color1 the first color
     * @param color2 the second color
     */
    fun setColorRange(firstIndex: Int, lastIndex: Int, color1: Int, color2: Int) {
        for (i in firstIndex..lastIndex) map[i] = mixColors((i - firstIndex).toFloat() / (lastIndex - firstIndex), color1, color2)
    }

    /**
     * Set a range of the colormap to a single color.
     * @param firstIndex the position of the first color
     * @param lastIndex the position of the second color
     * @param color the color
     */
    fun setColorRange(firstIndex: Int, lastIndex: Int, color: Int) {
        for (i in firstIndex..lastIndex) map[i] = color
    }

    /**
     * Set one element of the colormap to a given color.
     * @param index the position of the color
     * @param color the color
     * @see .getColor
     */
    fun setColor(index: Int, color: Int) {
        map[index] = color
    }

    public override fun clone(): Any {
//        try {
            val g = super.clone() as ArrayColormap
            g.map = map.clone()
            return g
//        } catch (e: CloneNotSupportedException) {
//        }
//        return null
    }
}