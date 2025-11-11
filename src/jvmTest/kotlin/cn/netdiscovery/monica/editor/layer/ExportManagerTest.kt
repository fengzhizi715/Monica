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
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import kotlin.test.Test
import kotlin.test.assertEquals

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

