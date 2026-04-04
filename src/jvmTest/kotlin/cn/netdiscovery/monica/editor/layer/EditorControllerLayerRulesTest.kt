package cn.netdiscovery.monica.editor.layer

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Rect
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.EditorController
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayerMaskShape
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerBlendMode
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EditorControllerLayerRulesTest {

    @Test
    fun `background layer cannot be removed renamed or moved`() {
        val controller = EditorController()
        val background = ImageLayer(EditorController.BACKGROUND_LAYER_NAME, ImageBitmap(8, 8))
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(background)
        controller.addLayer(overlay)

        assertFalse(controller.renameLayer(background.id, "Renamed Background"))
        assertFalse(controller.moveLayerUp(background.id))
        assertFalse(controller.moveLayerTo(background.id, 1))

        controller.removeLayer(background.id)

        val layers = controller.layerManager.layers.value
        assertEquals(2, layers.size)
        assertEquals(EditorController.BACKGROUND_LAYER_NAME, layers.first().name)
    }

    @Test
    fun `non background layer cannot move ahead of background`() {
        val controller = EditorController()
        val background = ImageLayer(EditorController.BACKGROUND_LAYER_NAME, ImageBitmap(8, 8))
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(background)
        controller.addLayer(overlay)

        assertFalse(controller.moveLayerTo(overlay.id, 0))

        val layers = controller.layerManager.layers.value
        assertEquals(background.id, layers.first().id)
        assertEquals(overlay.id, layers[1].id)
    }

    @Test
    fun `regular layer cannot be renamed to background name`() {
        val controller = EditorController()
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(overlay)

        assertFalse(controller.renameLayer(overlay.id, EditorController.BACKGROUND_LAYER_NAME))
        assertEquals("Overlay", controller.layerManager.layers.value.single().name)
    }

    @Test
    fun `set layer opacity updates layer through controller`() {
        val controller = EditorController()
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(overlay)

        assertTrue(controller.setLayerOpacity(overlay.id, 0.35f))
        assertEquals(0.35f, controller.layerManager.layers.value.single().opacity)
    }

    @Test
    fun `set layer blend mode updates layer through controller and participates in undo`() {
        val controller = EditorController()
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(overlay)

        assertTrue(controller.setLayerBlendMode(overlay.id, LayerBlendMode.MULTIPLY))
        assertEquals(LayerBlendMode.MULTIPLY, controller.layerManager.layers.value.single().blendMode)

        assertTrue(controller.undo())
        assertEquals(LayerBlendMode.NORMAL, controller.layerManager.layers.value.single().blendMode)
    }

    @Test
    fun `undo and redo restore layer state`() {
        val controller = EditorController()
        val overlay = ImageLayer("Overlay", ImageBitmap(4, 4))

        controller.addLayer(overlay)
        controller.renameLayer(overlay.id, "Overlay 2")

        assertTrue(controller.canUndo())
        assertTrue(controller.undo())
        assertEquals("Overlay", (controller.layerManager.layers.value.single() as ImageLayer).name)

        assertTrue(controller.canRedo())
        assertTrue(controller.redo())
        assertEquals("Overlay 2", (controller.layerManager.layers.value.single() as ImageLayer).name)
    }

    @Test
    fun `crop changes participate in undo history`() {
        val controller = EditorController()
        val overlay = ImageLayer("Overlay", ImageBitmap(20, 20))

        controller.addLayer(overlay)
        controller.updateImageLayerCrop(overlay.id, Rect(2f, 2f, 18f, 18f))

        assertEquals(Rect(2f, 2f, 18f, 18f), overlay.transform.cropRect)
        assertTrue(controller.undo())
        assertEquals(null, (controller.layerManager.layers.value.single() as ImageLayer).transform.cropRect)
    }

    @Test
    fun `selected layers can be grouped and batch locked`() {
        val controller = EditorController()
        val first = ImageLayer("Layer 1", ImageBitmap(8, 8))
        val second = ImageLayer("Layer 2", ImageBitmap(8, 8))

        controller.addLayer(first)
        controller.addLayer(second)
        controller.toggleLayerSelection(first.id)
        controller.toggleLayerSelection(second.id)

        val group = controller.createGroupFromSelection()

        assertNotNull(group)
        assertEquals(group.id, controller.layerManager.getLayerById(first.id)?.groupId)
        assertEquals(group.id, controller.layerManager.getLayerById(second.id)?.groupId)

        assertTrue(controller.setSelectedLayersLocked(true))
        assertTrue(controller.layerManager.getLayerById(first.id)?.locked == true)
        assertTrue(controller.layerManager.getLayerById(second.id)?.locked == true)
    }

    @Test
    fun `adjustment layer is created against selected targets`() {
        val controller = EditorController()
        val target = ImageLayer("Target", ImageBitmap(8, 8))

        controller.addLayer(target)
        controller.toggleLayerSelection(target.id)

        val adjustmentLayer = controller.createAdjustmentLayer()

        assertEquals(LayerType.ADJUSTMENT, adjustmentLayer.type)
        assertEquals(setOf(target.id), adjustmentLayer.targetLayerIds)
    }

    @Test
    fun `centered mask can be created and cleared`() {
        val controller = EditorController()
        val target = ImageLayer("Target", ImageBitmap(20, 20))

        controller.addLayer(target)

        assertTrue(controller.createCenteredMask(target.id, ImageLayerMaskShape.ELLIPSE))
        assertNotNull((controller.layerManager.getLayerById(target.id) as ImageLayer).transform.mask)

        assertTrue(controller.setImageLayerMask(target.id, null))
        assertNull((controller.layerManager.getLayerById(target.id) as ImageLayer).transform.mask)
    }
}
