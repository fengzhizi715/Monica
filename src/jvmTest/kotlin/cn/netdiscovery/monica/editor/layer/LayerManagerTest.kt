package cn.netdiscovery.monica.editor.layer

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LayerManagerTest {

    @Test
    fun `add layer updates active layer`() {
        val manager = LayerManager()
        val layer = ImageLayer(name = "Background", image = null)

        manager.addLayer(layer)

        assertEquals(1, manager.layers.value.size)
        assertEquals(layer.id, manager.activeLayer.value?.id)
    }

    @Test
    fun `remove active layer selects previous`() {
        val manager = LayerManager()
        val first = ImageLayer(name = "Layer 1", image = null)
        val second = ShapeLayer(name = "Layer 2")

        manager.addLayer(first)
        manager.addLayer(second)

        assertEquals(second.id, manager.activeLayer.value?.id)

        manager.removeLayer(second.id)

        assertEquals(1, manager.layers.value.size)
        assertEquals(first.id, manager.activeLayer.value?.id)
    }

    @Test
    fun `move layer up swaps ordering`() {
        val manager = LayerManager()
        val bottom = ShapeLayer("Bottom")
        val top = ShapeLayer("Top")

        manager.addLayer(bottom)
        manager.addLayer(top)

        assertEquals(listOf(bottom, top), manager.layers.value)

        manager.moveLayerUp(bottom.id)

        assertEquals(listOf(top, bottom), manager.layers.value)
    }

    @Test
    fun `clear removes all layers`() {
        val manager = LayerManager()
        manager.addLayer(ImageLayer("A", null))
        manager.addLayer(ImageLayer("B", null))

        manager.clear()

        assertTrue(manager.layers.value.isEmpty())
        assertNull(manager.activeLayer.value)
    }
}



