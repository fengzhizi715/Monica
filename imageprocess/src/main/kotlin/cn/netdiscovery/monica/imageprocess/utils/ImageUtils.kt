package cn.netdiscovery.monica.imageprocess.utils

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.ImageTranscoder
import org.apache.batik.util.XMLResourceDescriptor
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.awt.image.BufferedImage
import java.io.*
import javax.imageio.ImageIO
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.imageprocess.utils.ImageUtils
 * @author: Tony Shen
 * @date: 2025/2/21 18:16
 * @version: V1.0 <描述当前版本功能>
 */

/**
 * 把 BufferedImage 转换成文件，便于调试时使用
 */
@Throws(IOException::class)
fun writeImageFile(bi: BufferedImage, fileName:String, formatName:String = "png"):Boolean {
    return ImageIO.write(bi, formatName, File(fileName))
}


fun writeImageFileAsWebP(bi: BufferedImage, fileName:String):Boolean {

    val writers = ImageIO.getImageWritersByFormatName("webp")
    if (!writers.hasNext()) {
        println("不支持 WebP 格式，请确保 webp-imageio 插件已添加。")
        return false
    }

    val writer = writers.next()
    val output = ImageIO.createImageOutputStream(File(fileName))
    writer.output = output

    writer.write(null, javax.imageio.IIOImage(bi, null, null), null)

    output.close()
    writer.dispose()
    return true
}

fun loadAndFixSvg(inputSvgFile: File): Document {
    val parser = XMLResourceDescriptor.getXMLParserClassName()
    val factory = SAXSVGDocumentFactory(parser)
    val doc = factory.createDocument(inputSvgFile.toURI().toString())

    val svgNS = "http://www.w3.org/2000/svg"
    val xlinkNS = "http://www.w3.org/1999/xlink"
    val useTags = doc.getElementsByTagNameNS(svgNS, "use")

    val toRemove = mutableListOf<Element>()
    for (i in 0 until useTags.length) {
        val use = useTags.item(i) as Element
        val href = use.getAttributeNS(xlinkNS, "href")
        if (href.isNullOrBlank()) {
            toRemove.add(use)
        }
    }

    toRemove.forEach { it.parentNode?.removeChild(it) }
    println("清除非法 <use> 标签数: ${toRemove.size}")
    return doc
}

fun svgDocumentToBufferedImage(doc: Document, width: Float? = null, height: Float? = null): BufferedImage? {
    var image: BufferedImage? = null

    val transcoder = object : ImageTranscoder() {
        override fun createImage(w: Int, h: Int): BufferedImage {
            return BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        }

        override fun writeImage(img: BufferedImage, output: TranscoderOutput?) {
            image = img
        }
    }

    if (width != null) transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, width)
    if (height != null) transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, height)

    val inputStream = ByteArrayOutputStream().use { baos ->
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.transform(DOMSource(doc), StreamResult(baos))
        ByteArrayInputStream(baos.toByteArray())
    }

    val input = TranscoderInput(inputStream)

    try {
        transcoder.transcode(input, null)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return image
}

fun loadFixedSvgAsImage(inputFile: File, width: Float? = null, height: Float? = null): BufferedImage? {
    val doc = loadAndFixSvg(inputFile)
    return svgDocumentToBufferedImage(doc, width, height)
}

/**
 * 在无需解码整张图片的情况下，获取图像的尺寸
 */
fun getImageDimension(file: File): Pair<Int, Int>? {
    ImageIO.createImageInputStream(file)?.use { input ->
        val readers = ImageIO.getImageReaders(input)
        if (readers.hasNext()) {
            val reader = readers.next()
            reader.input = input
            val width = reader.getWidth(0)
            val height = reader.getHeight(0)
            reader.dispose()
            return width to height
        }
    }
    return null
}

/**
 * 判断图像是否大图
 */
fun isLargeImage(width: Int, height: Int): Boolean {
    val pixelCount = width * height
    val longSide = maxOf(width, height)

    return pixelCount > 12_000_000 || longSide > 4000 // 1200 万像素或长边超 4000px
}

fun clamp(c: Int): Int {
    return if (c > 255) 255 else if (c < 0) 0 else c
}

fun clamp(x: Int, a: Int, b: Int): Int {
    return if (x < a) a else if (x > b) b else x
}

/**
 * Clamp a value to an interval.
 * @param a the lower clamp threshold
 * @param b the upper clamp threshold
 * @param x the input parameter
 * @return the clamped value
 */
fun clamp(x: Float, a: Float, b: Float): Float {
    return if (x < a) a else if (x > b) b else x
}

fun premultiply(p: IntArray, offset: Int, length: Int) {
    var length = length
    length += offset
    for (i in offset until length) {
        val rgb = p[i]
        val a = (rgb shr 24) and 0xff
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff
        val f = a * (1.0f / 255.0f)
        r = (r * f).toInt()
        g = (g * f).toInt()
        b = (b * f).toInt()
        p[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}

fun unpremultiply(p: IntArray, offset: Int, length: Int) {
    var length = length
    length += offset
    for (i in offset until length) {
        val rgb = p[i]
        val a = (rgb shr 24) and 0xff
        var r = (rgb shr 16) and 0xff
        var g = (rgb shr 8) and 0xff
        var b = rgb and 0xff
        if (a != 0 && a != 255) {
            val f = 255.0f / a
            r = (r * f).toInt()
            g = (g * f).toInt()
            b = (b * f).toInt()
            if (r > 255) r = 255
            if (g > 255) g = 255
            if (b > 255) b = 255
            p[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        }
    }
}