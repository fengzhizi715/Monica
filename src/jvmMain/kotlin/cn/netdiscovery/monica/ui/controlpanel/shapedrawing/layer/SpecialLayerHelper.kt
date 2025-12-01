package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.ui.graphics.ImageBitmap
import java.util.UUID

/**
 * 专门处理特殊图层（如背景层）的辅助类
 * 集中管理背景层的查找、创建、更新等操作
 */
class SpecialLayerHelper(
    private val layerManager: LayerManager,
    private val backgroundLayerName: String = "背景图层"
) {
    
    private var cachedBackgroundLayer: ImageLayer? = null
    private var cachedBackgroundLayerId: UUID? = null

    /**
     * 获取背景层，如果不存在则返回 null
     * 使用缓存机制优化性能，自动验证缓存有效性
     */
    fun getBackgroundLayer(): ImageLayer? {
        // 先验证缓存是否有效
        val cached = validateBackgroundLayerCache()
        if (cached != null) {
            return cached
        }
        
        // 缓存失效或不存在，重新查找
        val found = layerManager.layers.value
            .firstOrNull { it.name == backgroundLayerName && it is ImageLayer } as? ImageLayer
        
        cachedBackgroundLayer = found
        cachedBackgroundLayerId = found?.id
        return found
    }
    
    /**
     * 获取或创建背景层
     * 如果不存在则创建新的背景层并添加到索引 0
     */
    fun getOrCreateBackgroundLayer(image: ImageBitmap): ImageLayer {
        val existing = getBackgroundLayer()
        if (existing != null) {
            return existing
        }
        
        val newLayer = ImageLayer(backgroundLayerName, image)
        layerManager.addLayer(newLayer, index = 0)
        cachedBackgroundLayer = newLayer
        cachedBackgroundLayerId = newLayer.id
        return newLayer
    }
    
    /**
     * 更新背景层图像
     * 如果背景层不存在，则创建新的
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
     * 移除背景层（谨慎使用，通常不应该删除背景层）
     * 此方法主要用于清理或重置场景
     */
    fun removeBackgroundLayer(): Boolean {
        val backgroundLayer = getBackgroundLayer()
        return if (backgroundLayer != null) {
            layerManager.removeLayer(backgroundLayer.id) != null
        } else {
            false
        }
    }
    
    /**
     * 获取背景层的尺寸（如果存在）
     */
    fun getBackgroundSize(): Pair<Float, Float>? {
        return getBackgroundLayer()?.image?.let { 
            Pair(it.width.toFloat(), it.height.toFloat()) 
        }
    }

    /**
     * 验证背景层缓存是否仍然有效
     */
    private fun validateBackgroundLayerCache(): ImageLayer? {
        val cachedId = cachedBackgroundLayerId ?: return null
        val cached = cachedBackgroundLayer
        
        // 检查缓存的背景层是否仍在图层列表中
        if (cached != null && cached.id == cachedId && 
            layerManager.layers.value.any { it.id == cachedId }) {
            return cached
        }
        
        // 缓存失效，清空缓存
        cachedBackgroundLayer = null
        cachedBackgroundLayerId = null
        return null
    }
}

