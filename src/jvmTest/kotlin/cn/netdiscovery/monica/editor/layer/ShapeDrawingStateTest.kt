package cn.netdiscovery.monica.editor.layer

import androidx.compose.ui.geometry.Offset
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.coordinate.CoordinateConverter
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.Shape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.model.ShapeProperties
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.state.ShapeDrawingState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ShapeDrawingStateTest {

    @Test
    fun `move selected line updates display and original coordinates`() {
        val state = ShapeDrawingState()
        val converter = CoordinateConverter(scaleX = 2f, scaleY = 3f)
        val display = Shape.Line(
            from = Offset(10f, 10f),
            to = Offset(20f, 20f),
            shapeProperties = ShapeProperties()
        )
        val original = converter.convertLineToOriginal(display)

        state.addShape(display.from, display, original)
        state.recordLastDrawnShape(display.from, "Line")

        val displayDelta = Offset(5f, 7f)
        val originalDelta = converter.displayDeltaToOriginal(displayDelta)

        assertTrue(state.moveSelectedShape(displayDelta, originalDelta))

        val movedDisplay = state.displayLines.values.single()
        val movedOriginal = state.originalLines.values.single()

        assertEquals(Offset(15f, 17f), movedDisplay.from)
        assertEquals(Offset(25f, 27f), movedDisplay.to)
        assertEquals(Offset(30f, 51f), movedOriginal.from)
        assertEquals(Offset(50f, 81f), movedOriginal.to)
    }
}
