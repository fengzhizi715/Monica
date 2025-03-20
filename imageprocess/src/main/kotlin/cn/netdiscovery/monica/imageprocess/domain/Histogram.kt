package cn.netdiscovery.monica.imageprocess.domain

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.domain.Histogram
 * @author: Tony Shen
 * @date: 2025/3/20 19:47
 * @version: V1.0 <描述当前版本功能>
 */
class Histogram {
    companion object {
        const val RED = 0
        const val GREEN = 1
        const val BLUE = 2
        const val GRAY = 3
    }

    private lateinit var histogram: Array<IntArray>
    private var numSamples: Int = 0
    private lateinit var minValue: IntArray
    private lateinit var maxValue: IntArray
    private lateinit var minFrequency: IntArray
    private lateinit var maxFrequency: IntArray
    private lateinit var mean: FloatArray
    private var isGray: Boolean = true

    constructor()

    constructor(pixels: IntArray, w: Int, h: Int, offset: Int, stride: Int) {
        histogram = Array(3) { IntArray(256) }
        minValue = IntArray(4)
        maxValue = IntArray(4)
        minFrequency = IntArray(3)
        maxFrequency = IntArray(3)
        mean = FloatArray(3)

        numSamples = w * h
        isGray = true

        var index: Int
        for (y in 0 until h) {
            index = offset + y * stride
            for (x in 0 until w) {
                val rgb = pixels[index++]
                val r = (rgb shr 16) and 0xff
                val g = (rgb shr 8) and 0xff
                val b = rgb and 0xff
                histogram[RED][r]++
                histogram[GREEN][g]++
                histogram[BLUE][b]++
            }
        }

        for (i in 0 until 256) {
            if (histogram[RED][i] != histogram[GREEN][i] || histogram[GREEN][i] != histogram[BLUE][i]) {
                isGray = false
                break
            }
        }

        for (i in 0 until 3) {
            for (j in 0 until 256) {
                if (histogram[i][j] > 0) {
                    minValue[i] = j
                    break
                }
            }

            for (j in 255 downTo 0) {
                if (histogram[i][j] > 0) {
                    maxValue[i] = j
                    break
                }
            }

            minFrequency[i] = Int.MAX_VALUE
            maxFrequency[i] = 0
            for (j in 0 until 256) {
                minFrequency[i] = minOf(minFrequency[i], histogram[i][j])
                maxFrequency[i] = maxOf(maxFrequency[i], histogram[i][j])
                mean[i] += j * histogram[i][j].toFloat()
            }
            mean[i] /= numSamples.toFloat()
        }
        minValue[GRAY] = minOf(minValue[RED], minValue[GREEN], minValue[BLUE])
        maxValue[GRAY] = maxOf(maxValue[RED], maxValue[GREEN], maxValue[BLUE])
    }

    fun isGray(): Boolean = isGray

    fun getNumSamples(): Int = numSamples

    fun getFrequency(value: Int): Int = if (numSamples > 0 && isGray && value in 0..255) histogram[0][value] else -1

    fun getFrequency(channel: Int, value: Int): Int =
        if (numSamples < 1 || channel !in 0..2 || value !in 0..255) -1 else histogram[channel][value]

    fun getMinFrequency(): Int = if (numSamples > 0 && isGray) minFrequency[0] else -1

    fun getMinFrequency(channel: Int): Int = if (numSamples < 1 || channel !in 0..2) -1 else minFrequency[channel]

    fun getMaxFrequency(): Int = if (numSamples > 0 && isGray) maxFrequency[0] else -1

    fun getMaxFrequency(channel: Int): Int = if (numSamples < 1 || channel !in 0..2) -1 else maxFrequency[channel]

    fun getMinValue(): Int = if (numSamples > 0 && isGray) minValue[0] else -1

    fun getMinValue(channel: Int): Int = minValue[channel]

    fun getMaxValue(): Int = if (numSamples > 0 && isGray) maxValue[0] else -1

    fun getMaxValue(channel: Int): Int = maxValue[channel]

    fun getMeanValue(): Float = if (numSamples > 0 && isGray) mean[0] else -1.0F

    fun getMeanValue(channel: Int): Float = if (numSamples > 0 && channel in RED..BLUE) mean[channel] else -1.0F
}