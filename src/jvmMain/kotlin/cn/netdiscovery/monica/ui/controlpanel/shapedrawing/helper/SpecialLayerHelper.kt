package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.helper

import androidx.compose.ui.graphics.ImageBitmap
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.ImageLayer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.Layer
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerManager
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer.LayerType
import java.util.UUID

/**
 * 背景层助手类，集中管理特殊图层（如背景层）的逻辑
 * 包括缓存管理、查询、创建和更新
 */
class SpecialLayerHelper(
    private val layerManager: LayerManager,
    private val backgroundLayerName: String = BACKGROUND_LAYER_NAME
) {
    private var cachedBackgroundLayer: ImageLayer? = null
    private var cachedBackgroundLayerId: UUID? = null

    companion object {
        const val BACKGROUND_LAYER_NAME = "背景图层"
    }

    /**
     * 获取背景层，带缓存机制
     */
    fun getBackgroundLayer(): ImageLayer? {
        // 验证缓存是否仍然有效
        validateCache()
        if (cachedBackgroundLayer != null) {
            return cachedBackgroundLayer
        }

        // 缓存失效或不存在，重新查找
        val found = findBackgroundLayer()
        cachedBackgroundLayer = found
        cachedBackgroundLayerId = found?.id
        return found
    }

    /**
     * 获取或创建背景层
     */
    fun getOrCreateBackgroundLayer(image: ImageBitmap): ImageLayer {
        val existing = getBackgroundLayer()
        if (existing != null) {
            return existing
        }

        // 创建新的背景层
        val newLayer = ImageLayer(backgroundLayerName, image)
        layerManager.addLayer(newLayer, index = 0)
        cachedBackgroundLayer = newLayer
        cachedBackgroundLayerId = newLayer.id
        return newLayer
    }

    /**
     * 更新背景层图像
     */
    fun updateBackgroundLayer(image: ImageBitmap) {
        val layer = getOrCreateBackgroundLayer(image)
        layer.updateImage(image)
    }

    /**
     * 检查是否存在背景层
     */
    fun hasBackgroundLayer(): Boolean {
        return getBackgroundLayer() != null
    }

    /**
     * 移除背景层
     */
    fun removeBackgroundLayer(): Boolean {
        val bgLayer = getBackgroundLayer() ?: return false
        invalidateCache()
        return layerManager.removeLayer(bgLayer.id) != null
    }

    /**
     * 检查是否为背景层
     */
    fun isBackgroundLayer(layer: Layer): Boolean {
        return layer.name == backgroundLayerName && layer.type == LayerType.IMAGE
    }

    /**
     * 验证缓存的有效性
     */
    private fun validateCache() {
        if (cachedBackgroundLayer == null || cachedBackgroundLayerId == null) {
            return
        }

        // 检查缓存的图层是否仍在图层管理器中
        val stillExists = layerManager.getLayerById(cachedBackgroundLayerId!!) != null
        if (!stillExists) {
            invalidateCache()
        }
    }

    /**
     * 清空缓存
     */
    private fun invalidateCache() {
        cachedBackgroundLayer = null
        cachedBackgroundLayerId = null
    }

    /**
     * 在图层管理器中查找背景层
     */
    private fun findBackgroundLayer(): ImageLayer? {
        return layerManager.layers.value
            .firstOrNull { isBackgroundLayer(it) } as? ImageLayer
    }
}

