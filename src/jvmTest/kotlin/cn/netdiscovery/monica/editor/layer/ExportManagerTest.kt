package cn.netdiscovery.monica.editor.layer

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMask
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMaskShape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerAdjustment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorControllerExportTest {

    @Test
    fun `exportImageBitmap composes image layers`() {
        val editorController = EditorController()

        val redBitmap = createSolidBitmap(Color.Red, 8, 8)
        val imageLayer = ImageLayer(name = "背景图层", image = redBitmap)

        editorController.addLayer(imageLayer)

        val result = editorController.exportImageBitmap(
            width = 8,
            height = 8,
            density = Density(1f)
        )

        val buffered = result.toAwtImage()
        val pixel = buffered.getRGB(4, 4)
        assertEquals(Color.Red.toArgb(), pixel)
    }

    @Test
    fun `exportImageBitmap applies image layer opacity once`() {
        val editorController = EditorController()

        val blueBitmap = createSolidBitmap(Color.Blue, 8, 8)
        val imageLayer = ImageLayer(name = "Foreground", image = blueBitmap)
        imageLayer.updateOpacity(0.5f)

        editorController.addLayer(imageLayer)

        val result = editorController.exportImageBitmap(
            width = 8,
            height = 8,
            density = Density(1f)
        )

        val pixel = result.toAwtImage().getRGB(4, 4)
        val alpha = pixel ushr 24 and 0xFF
        assertTrue(alpha in 120..135, "expected alpha near 128 but was $alpha")
    }

    @Test
    fun `adjustment layer changes exported pixels non destructively`() {
        val editorController = EditorController()

        val grayBitmap = createSolidBitmap(Color(0xFF808080), 8, 8)
        val imageLayer = ImageLayer(name = "Foreground", image = grayBitmap)
        editorController.addLayer(imageLayer)
        editorController.createAdjustmentLayer(targetLayerIds = setOf(imageLayer.id)).apply {
            updateAdjustment(LayerAdjustment(brightness = 0.2f, contrast = 1.2f, saturation = 1f))
        }

        val result = editorController.exportImageBitmap(
            width = 8,
            height = 8,
            density = Density(1f)
        )

        val pixel = result.toAwtImage().getRGB(4, 4)
        val red = pixel shr 16 and 0xFF
        assertTrue(red > 128, "expected adjusted pixel to become brighter, but was $red")
    }

    @Test
    fun `image mask clips exported pixels`() {
        val editorController = EditorController()

        val redBitmap = createSolidBitmap(Color.Red, 20, 20)
        val imageLayer = ImageLayer(name = "Masked", image = redBitmap)
        imageLayer.updateTransform(
            imageLayer.transform.copy(
                mask = ImageLayerMask(
                    rect = Rect(4f, 4f, 16f, 16f),
                    shape = ImageLayerMaskShape.ELLIPSE
                )
            )
        )
        editorController.addLayer(imageLayer)

        val result = editorController.exportImageBitmap(
            width = 20,
            height = 20,
            density = Density(1f)
        )

        val cornerAlpha = result.toAwtImage().getRGB(1, 1) ushr 24 and 0xFF
        val centerAlpha = result.toAwtImage().getRGB(10, 10) ushr 24 and 0xFF
        assertTrue(cornerAlpha < 10, "expected masked corner to be transparent, but was $cornerAlpha")
        assertTrue(centerAlpha > 240, "expected masked center to remain visible, but was $centerAlpha")
    }

    private fun createSolidBitmap(color: Color, width: Int, height: Int): ImageBitmap {
        val bitmap = ImageBitmap(width, height)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
        }
        canvas.drawRect(Rect(0f, 0f, width.toFloat(), height.toFloat()), paint)
        return bitmap
    }
}
