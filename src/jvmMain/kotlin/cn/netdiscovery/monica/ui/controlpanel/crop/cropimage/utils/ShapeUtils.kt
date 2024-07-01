package cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.utils

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import cn.netdiscovery.monica.ui.controlpanel.crop.cropimage.model.AspectRatio
import org.jetbrains.skia.Matrix33
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.utils.ShapeUtils
 * @author: Tony Shen
 * @date: 2024/5/26 12:12
 * @version: V1.0 <描述当前版本功能>
 */

fun createPolygonPath(cx: Float, cy: Float, sides: Int, radius: Float): Path {

    val angle = 2.0 * Math.PI / sides

    return Path().apply {
        moveTo(
            cx + (radius * cos(0.0)).toFloat(),
            cy + (radius * sin(0.0)).toFloat()
        )
        for (i in 1 until sides) {
            lineTo(
                cx + (radius * cos(angle * i)).toFloat(),
                cy + (radius * sin(angle * i)).toFloat()
            )
        }
        close()
    }
}

fun createPolygonShape(sides: Int, degrees: Float = 0f): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->

        val radius = size.width.coerceAtMost(size.height) / 2
        addPath(
            createPolygonPath(
                cx = size.width / 2,
                cy = size.height / 2,
                sides = sides,
                radius = radius
            )
        )

        val matrix = Matrix33.makeRotate(degrees, size.width / 2, size.height / 2)
        this.asSkiaPath().transform(matrix)
    }
}


/**
 * Creates a [Rect] shape with given aspect ratio.
 */
fun createRectShape(aspectRatio: AspectRatio): GenericShape {
    return GenericShape { size: Size, _: LayoutDirection ->
        val value = aspectRatio.value

        val width = size.width
        val height = size.height
        val shapeSize =
            if (aspectRatio == AspectRatio.Original) Size(width, height)
            else if (value > 1) Size(width = width, height = width / value)
            else Size(width = height * value, height = height)

        addRect(Rect(offset = Offset.Zero, size = shapeSize))
    }
}

fun Path.scaleAndTranslatePath(
    width: Float,
    height: Float,
) {
    val pathSize = getBounds().size

    val matrix = Matrix33.makeScale(
        width / pathSize.width,
        height / pathSize.height
    )

    this.asSkiaPath().transform(matrix)

    val left = getBounds().left
    val top = getBounds().top

    translate(Offset(-left, -top))
}

/**
 * Build an outline from a shape using aspect ratio, shape and coefficient to scale
 *
 * @return [Triple] that contains left, top offset and [Outline]
 */
fun buildOutline(
    aspectRatio: AspectRatio,
    coefficient: Float,
    shape: Shape,
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
): Pair<Offset, Outline> {

    val (shapeSize, offset) = calculateSizeAndOffsetFromAspectRatio(aspectRatio, coefficient, size)

    val outline = shape.createOutline(
        size = shapeSize,
        layoutDirection = layoutDirection,
        density = density
    )
    return Pair(offset, outline)
}


/**
 * Calculate new size and offset based on [size], [coefficient] and [aspectRatio]
 *
 * For 4/3f aspect ratio with 1000px width, 1000px height with coefficient 1f
 * it returns Size(1000f, 750f), Offset(0f, 125f).
 */
fun calculateSizeAndOffsetFromAspectRatio(
    aspectRatio: AspectRatio,
    coefficient: Float,
    size: Size,
): Pair<Size, Offset> {
    val width = size.width
    val height = size.height

    val value = aspectRatio.value

    val newSize = if (aspectRatio == AspectRatio.Original) {
        Size(width * coefficient, height * coefficient)
    } else if (value > 1) {
        Size(
            width = coefficient * width,
            height = coefficient * width / value
        )
    } else {
        Size(width = coefficient * height * value, height = coefficient * height)
    }

    val left = (width - newSize.width) / 2
    val top = (height - newSize.height) / 2

    return Pair(newSize, Offset(left, top))
}

class Parallelogram(private val angle: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                val radian = (90 - angle) * Math.PI / 180
                val xOnOpposite = (size.height * tan(radian)).toFloat()
                moveTo(0f, size.height)
                lineTo(x = xOnOpposite, y = 0f)
                lineTo(x = size.width, y = 0f)
                lineTo(x = size.width - xOnOpposite, y = size.height)
                lineTo(x = xOnOpposite, y = size.height)
            }
        )
    }
}

class Diamond : Shape {

    /**
     * Creates the [Outline] for the diamond shape.
     *
     * @param size The [Size] of the diamond.
     * @param layoutDirection The [LayoutDirection] of the diamond.
     * @param density The [Density] of the diamond.
     * @return The [Outline] representing the diamond shape.
     */
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(

            Path().apply {
                val centerX = size.width / 2f
                val diamondCurve = 60f
                val width = size.width
                val height = size.height

                moveTo(x = 0f + diamondCurve, y = 0f)
                lineTo(x = width - diamondCurve, y = 0f)
                lineTo(x = width, y = diamondCurve)
                lineTo(x = centerX, y = height)
                lineTo(x = 0f, y = diamondCurve)

                close()
            }
        )
    }
}