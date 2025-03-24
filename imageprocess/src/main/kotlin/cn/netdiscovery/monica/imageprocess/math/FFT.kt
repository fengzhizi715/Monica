package cn.netdiscovery.monica.imageprocess.math

import kotlin.math.max
import kotlin.math.sin

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.math.FFT
 * @author: Tony Shen
 * @date: 2025/3/14 15:36
 * @version: V1.0 <描述当前版本功能>
 */
class FFT(logN: Int) {
    // Weighting factors
    protected var w1: FloatArray
    protected var w2: FloatArray
    protected var w3: FloatArray

    init {
        // Prepare the weighting factors
        w1 = FloatArray(logN)
        w2 = FloatArray(logN)
        w3 = FloatArray(logN)
        var N = 1
        for (k in 0..<logN) {
            N = N shl 1
            val angle = -2.0 * Math.PI / N
            w1[k] = sin(0.5 * angle).toFloat()
            w2[k] = -2.0f * w1[k] * w1[k]
            w3[k] = sin(angle).toFloat()
        }
    }

    private fun scramble(n: Int, real: FloatArray, imag: FloatArray) {
        var j = 0

        for (i in 0..<n) {
            if (i > j) {
                var t = real[j]
                real[j] = real[i]
                real[i] = t
                t = imag[j]
                imag[j] = imag[i]
                imag[i] = t
            }
            var m = n shr 1
            while (j >= m && m >= 2) {
                j -= m
                m = m shr 1
            }
            j += m
        }
    }

    private fun butterflies(n: Int, logN: Int, direction: Int, real: FloatArray, imag: FloatArray) {
        var N = 1

        for (k in 0..<logN) {
            var w_re: Float
            var w_im: Float
            var temp_re: Float
            var temp_im: Float
            var wt: Float
            val half_N = N
            N = N shl 1
            wt = direction * w1[k]
            val wp_re = w2[k]
            val wp_im = direction * w3[k]
            w_re = 1.0f
            w_im = 0.0f
            for (offset in 0..<half_N) {
                var i = offset
                while (i < n) {
                    val j = i + half_N
                    val re = real[j]
                    val im = imag[j]
                    temp_re = (w_re * re) - (w_im * im)
                    temp_im = (w_im * re) + (w_re * im)
                    real[j] = real[i] - temp_re
                    real[i] += temp_re
                    imag[j] = imag[i] - temp_im
                    imag[i] += temp_im
                    i += N
                }
                wt = w_re
                w_re = wt * wp_re - w_im * wp_im + w_re
                w_im = w_im * wp_re + wt * wp_im + w_im
            }
        }
        if (direction == -1) {
            val nr = 1.0f / n
            for (i in 0..<n) {
                real[i] *= nr
                imag[i] *= nr
            }
        }
    }

    fun transform1D(real: FloatArray, imag: FloatArray, logN: Int, n: Int, forward: Boolean) {
        scramble(n, real, imag)
        butterflies(n, logN, if (forward) 1 else -1, real, imag)
    }

    fun transform2D(real: FloatArray, imag: FloatArray, cols: Int, rows: Int, forward: Boolean) {
        val log2cols = log2(cols)
        val log2rows = log2(rows)
        val n = max(rows.toDouble(), cols.toDouble()).toInt()
        val rtemp = FloatArray(n)
        val itemp = FloatArray(n)

        // FFT the rows
        for (y in 0..<rows) {
            val offset = y * cols
            System.arraycopy(real, offset, rtemp, 0, cols)
            System.arraycopy(imag, offset, itemp, 0, cols)
            transform1D(rtemp, itemp, log2cols, cols, forward)
            System.arraycopy(rtemp, 0, real, offset, cols)
            System.arraycopy(itemp, 0, imag, offset, cols)
        }

        // FFT the columns
        for (x in 0..<cols) {
            var index = x
            for (y in 0..<rows) {
                rtemp[y] = real[index]
                itemp[y] = imag[index]
                index += cols
            }
            transform1D(rtemp, itemp, log2rows, rows, forward)
            index = x
            for (y in 0..<rows) {
                real[index] = rtemp[y]
                imag[index] = itemp[y]
                index += cols
            }
        }
    }

    private fun log2(n: Int): Int {
        var m = 1
        var log2n = 0

        while (m < n) {
            m *= 2
            log2n++
        }
        return if (m == n) log2n else -1
    }
}