package cn.netdiscovery.monica.imageprocess

import java.util.*


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.IntIntegralImage
 * @author: Tony Shen
 * @date: 2024/6/22 22:30
 * @version: V1.0 <描述当前版本功能>
 */

class IntIntegralImage {

    // sum index tables
    private lateinit var sum: IntArray
    private lateinit var squaresum: FloatArray
    private lateinit var image: ByteArray
    private var width = 0
    private var height = 0

    fun getImage(): ByteArray {
        return image
    }

    fun setImage(image: ByteArray) {
        this.image = image
    }

    fun getBlockSum(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val tl = sum[y1 * width + x1]
        val tr = sum[y2 * width + x1]
        val bl = sum[y1 * width + x2]
        val br = sum[y2 * width + x2]
        return br - bl - tr + tl
    }

    fun getBlockSquareSum(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        val tl = squaresum[y1 * width + x1]
        val tr = squaresum[y2 * width + x1]
        val bl = squaresum[y1 * width + x2]
        val br = squaresum[y2 * width + x2]
        return br - bl - tr + tl
    }

    fun calculate(w: Int, h: Int) {
        // 初始化积分图
        width = w + 1
        height = h + 1
        sum = IntArray(width * height)
        Arrays.fill(sum, 0)
        // 计算积分图
        var p1 = 0
        var p2 = 0
        var p3 = 0
        var p4: Int
        for (row in 1 until height) {
            for (col in 1 until width) {
                // 计算和查找表
                p1 = image[(row - 1) * w + col - 1].toInt() and 0xff  // p(x, y)
                p2 = sum[row * width + col - 1]                       // p(x-1, y)
                p3 = sum[(row - 1) * width + col]                     // p(x, y-1);
                p4 = sum[(row - 1) * width + col - 1]                 // p(x-1, y-1);
                sum[row * width + col] = p1 + p2 + p3 - p4
            }
        }
    }

    fun calculate(w: Int, h: Int, sqrtsum: Boolean) {
        width = w + 1
        height = h + 1
        sum = IntArray(width * height)
        squaresum = FloatArray(width * height)
        Arrays.fill(sum, 0)
        Arrays.fill(squaresum, 0f)
        // rows
        var p1 = 0
        var p2 = 0
        var p3 = 0
        var p4: Int
        var sp2 = 0f
        var sp3 = 0f
        var sp4 = 0f
        for (row in 1 until height) {
            for (col in 1 until width) {
                // 计算和查找表
                p1 = image[(row - 1) * w + col - 1].toInt() and 0xff   // p(x, y)
                p2 = sum[row * width + col - 1]                        // p(x-1, y)
                p3 = sum[(row - 1) * width + col]                      // p(x, y-1);
                p4 = sum[(row - 1) * width + col - 1]                  // p(x-1, y-1);
                sum[row * width + col] = p1 + p2 + p3 - p4

                // 计算平方查找表
                sp2 = squaresum[row * width + col - 1]                 // p(x-1, y)
                sp3 = squaresum[(row - 1) * width + col]               // p(x, y-1);
                sp4 = squaresum[(row - 1) * width + col - 1]           // p(x-1, y-1);
                squaresum[row * width + col] = p1 * p1 + sp2 + sp3 - sp4
            }
        }
    }
}