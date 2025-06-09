package cn.netdiscovery.monica.utils

import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.ImageFormatDetector
 * @author: Tony Shen
 * @date: 2025/6/4 16:29
 * @version: V1.0 基于文件的文件头，来判断文件的格式
 */
enum class ImageFormat {
    JPEG, PNG, WEBP, HEIC, AVIF, BMP, TIFF, GIF,
    PSD, HDR, SVG,
    CR2, CR3, ARW, NEF, ORF, RAF, RW2, DNG,
    UNKNOWN
}

fun ImageFormat.isRaw(): Boolean = this in listOf(
    ImageFormat.CR2, ImageFormat.CR3, ImageFormat.ARW,
    ImageFormat.NEF, ImageFormat.RAF, ImageFormat.ORF,
    ImageFormat.RW2, ImageFormat.DNG
)

object ImageFormatDetector {

    fun detectFormat(file: File): ImageFormat {
        val header = readFileHeader(file, 32)

        return when {
            // JPEG
            header.startsWith(0xFF, 0xD8, 0xFF) -> ImageFormat.JPEG

            // PNG
            header.startsWith("89504E470D0A1A0A") -> ImageFormat.PNG

            // WEBP (RIFFxxxxWEBP)
            header.startsWith("52494646") && header.slice(8, 12).toAscii() == "WEBP" -> ImageFormat.WEBP

            // HEIC
            header.slice(4, 12).toAscii().startsWith("ftypheic") -> ImageFormat.HEIC

            // AVIF
            header.slice(4, 12).toAscii().startsWith("ftypavif") -> ImageFormat.AVIF

            // BMP
            header.startsWith("424D") -> ImageFormat.BMP

            // GIF
            header.startsWith("47494638") -> ImageFormat.GIF

            // PSD
            header.startsWith("38425053") -> ImageFormat.PSD

            // HDR (ASCII header)
            header.startsWith("#?R".toByteArray(StandardCharsets.US_ASCII)) -> ImageFormat.HDR

            // SVG (text-based, starts with <svg or <?xml)
            header.toAscii().trimStart().startsWith("<svg") || header.toAscii().trimStart().startsWith("<?xml") -> ImageFormat.SVG

            // Canon CR2
            header.startsWith("49492A00") && header.size >= 12 && header.slice(8, 10).contentEquals("CR".toByteArray()) -> ImageFormat.CR2

            // Canon CR3 (ISO BMFF format with ftypcrx)
            header.slice(4, 12).toAscii().startsWith("ftypcrx") -> ImageFormat.CR3

            // Sony ARW
            header.startsWith("49492A00") && header.slice(8, 12).contentEquals("ARW ".toByteArray()) -> ImageFormat.ARW

            // Nikon NEF
            header.startsWith("4D4D002A") && header.slice(8, 12).contentEquals("NEF".toByteArray()) -> ImageFormat.NEF

            // Olympus ORF
            header.slice(0, 4).contentEquals("IIRO".toByteArray()) -> ImageFormat.ORF

            // Panasonic RW2
            header.startsWith("49492A00") && header.slice(8, 12).contentEquals("RW2".toByteArray()) -> ImageFormat.RW2

            // Fuji RAF
            header.startsWith("4655494A") -> ImageFormat.RAF

            // DNG
            header.startsWith("49492A00") && header.containsSubsequence("Adobe".toByteArray()) -> ImageFormat.DNG

            // TIFF fallback
            header.startsWith("49492A00") || header.startsWith("4D4D002A") -> ImageFormat.TIFF

            else -> ImageFormat.UNKNOWN
        }
    }

    fun getImageFormat(file: File): String? {
        val imageFormat = detectFormat(file)

        return if (imageFormat!=ImageFormat.UNKNOWN) {
            imageFormat.name.lowercase()
        } else {
            null
        }
    }

    // 读取文件前 N 字节作为头部
    private fun readFileHeader(file: File, size: Int): ByteArray {
        FileInputStream(file).use { input ->
            return input.readNBytes(size)
        }
    }

    // 二进制 startsWith 判断
    private fun ByteArray.startsWith(vararg bytes: Int): Boolean {
        if (this.size < bytes.size) return false
        for (i in bytes.indices) {
            if (this[i].toInt() and 0xFF != bytes[i]) return false
        }
        return true
    }

    // Hex 字符串形式 startsWith 判断
    private fun ByteArray.startsWith(hex: String): Boolean {
        val bytes = hex.chunked(2).map { it.toInt(16) }
        return startsWith(*bytes.toIntArray())
    }

    // ByteArray 对比
    private fun ByteArray.startsWith(prefix: ByteArray): Boolean {
        if (this.size < prefix.size) return false
        for (i in prefix.indices) {
            if (this[i] != prefix[i]) return false
        }
        return true
    }

    // ByteArray 区间切片
    private fun ByteArray.slice(from: Int, to: Int): ByteArray {
        return copyOfRange(from, to.coerceAtMost(this.size))
    }

    // 判断是否包含某一子序列
    private fun ByteArray.containsSubsequence(seq: ByteArray): Boolean {
        outer@ for (i in 0..(this.size - seq.size)) {
            for (j in seq.indices) {
                if (this[i + j] != seq[j]) continue@outer
            }
            return true
        }
        return false
    }

    // 将 ByteArray 转成 ASCII 字符串（安全用于文件头分析）
    private fun ByteArray.toAscii(): String = String(this, Charsets.US_ASCII)
}