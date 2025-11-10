package cn.netdiscovery.monica.editor.layer

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * 负责管理图层列表的核心类，提供增删改查、排序以及事件监听能力。
 */
class LayerManager {

    private val lock = ReentrantLock()

    private val _layers = MutableStateFlow<List<Layer>>(emptyList())
    val layers: StateFlow<List<Layer>> = _layers.asStateFlow()

    private val _activeLayer = MutableStateFlow<Layer?>(null)
    val activeLayer: StateFlow<Layer?> = _activeLayer.asStateFlow()

    private val layerObservers = CopyOnWriteArraySet<LayerListObserver>()
    private val activeLayerObservers = CopyOnWriteArraySet<ActiveLayerObserver>()

    /**
     * 添加图层。默认追加到末尾，可通过 [index] 指定插入位置。
     */
    fun addLayer(layer: Layer, index: Int? = null) {
        lock.withLock {
            val mutable = _layers.value.toMutableList()
            val insertIndex = index?.coerceIn(0, mutable.size) ?: mutable.size
            mutable.add(insertIndex, layer)
            _layers.value = mutable.toList()
            _activeLayer.value = layer
        }
        layer.onAttach()
        notifyLayerObservers()
        notifyActiveLayerObservers()
    }

    /**
     * 根据 [layerId] 移除图层。
     * @return 被移除的图层实例，若未找到则返回 null。
     */
    fun removeLayer(layerId: UUID): Layer? {
        var removed: Layer? = null
        lock.withLock {
            val mutable = _layers.value.toMutableList()
            val index = mutable.indexOfFirst { it.id == layerId }
            if (index != -1) {
                removed = mutable.removeAt(index)
                _layers.value = mutable.toList()
                if (_activeLayer.value?.id == layerId) {
                    _activeLayer.value = mutable.lastOrNull()
                }
            }
        }
        removed?.onDetach()
        if (removed != null) {
            notifyLayerObservers()
            notifyActiveLayerObservers()
        }
        return removed
    }

    /**
     * 清空所有图层。
     */
    fun clear() {
        val detached: List<Layer>
        lock.withLock {
            detached = _layers.value
            _layers.value = emptyList()
            _activeLayer.value = null
        }
        detached.forEach { it.onDetach() }
        notifyLayerObservers()
        notifyActiveLayerObservers()
    }

    /**
     * 将图层上移一层。
     */
    fun moveLayerUp(layerId: UUID): Boolean = moveByOffset(layerId, 1)

    /**
     * 将图层下移一层。
     */
    fun moveLayerDown(layerId: UUID): Boolean = moveByOffset(layerId, -1)

    /**
     * 将图层移动到指定位置。
     */
    fun moveLayerTo(layerId: UUID, index: Int): Boolean {
        var moved = false
        lock.withLock {
            val mutable = _layers.value.toMutableList()
            val currentIndex = mutable.indexOfFirst { it.id == layerId }
            if (currentIndex != -1) {
                val targetIndex = index.coerceIn(0, mutable.lastIndex.coerceAtLeast(0))
                if (currentIndex != targetIndex) {
                    val layer = mutable.removeAt(currentIndex)
                    mutable.add(targetIndex, layer)
                    _layers.value = mutable.toList()
                    moved = true
                }
            }
        }
        if (moved) notifyLayerObservers()
        return moved
    }

    /**
     * 设置当前激活的图层。
     */
    fun setActiveLayer(layerId: UUID?) {
        var shouldNotify = false
        lock.withLock {
            val target = layerId?.let { id ->
                _layers.value.firstOrNull { it.id == id }
            }
            if (_activeLayer.value !== target) {
                _activeLayer.value = target
                shouldNotify = true
            }
        }
        if (shouldNotify) notifyActiveLayerObservers()
    }

    /**
     * 重命名图层。
     */
    fun renameLayer(layerId: UUID, newName: String): Boolean {
        var renamed = false
        lock.withLock {
            val layer = _layers.value.firstOrNull { it.id == layerId }
            if (layer != null && layer.name != newName) {
                layer.rename(newName)
                renamed = true
            }
        }
        if (renamed) notifyLayerObservers()
        return renamed
    }

    /**
     * 更新图层可见性。
     */
    fun setLayerVisibility(layerId: UUID, visible: Boolean): Boolean {
        var updated = false
        lock.withLock {
            val layer = _layers.value.firstOrNull { it.id == layerId }
            if (layer != null && layer.visible != visible) {
                layer.setVisibility(visible)
                updated = true
            }
        }
        if (updated) notifyLayerObservers()
        return updated
    }

    /**
     * 更新图层透明度。
     */
    fun setLayerOpacity(layerId: UUID, opacity: Float): Boolean {
        var updated = false
        lock.withLock {
            val layer = _layers.value.firstOrNull { it.id == layerId }
            val targetOpacity = opacity.coerceIn(0f, 1f)
            if (layer != null && layer.opacity != targetOpacity) {
                layer.updateOpacity(targetOpacity)
                updated = true
            }
        }
        if (updated) notifyLayerObservers()
        return updated
    }

    /**
     * 更新图层锁定状态。
     */
    fun setLayerLocked(layerId: UUID, locked: Boolean): Boolean {
        var updated = false
        lock.withLock {
            val layer = _layers.value.firstOrNull { it.id == layerId }
            if (layer != null && layer.locked != locked) {
                layer.updateLocked(locked)
                updated = true
            }
        }
        if (updated) notifyLayerObservers()
        return updated
    }

    /**
     * 根据 ID 获取图层。
     */
    fun getLayerById(layerId: UUID): Layer? = lock.withLock {
        _layers.value.firstOrNull { it.id == layerId }
    }

    /**
     * 批量替换当前图层列表，并指定激活层。
     */
    fun replaceLayers(layers: List<Layer>, activeLayerId: UUID? = null) {
        val previous = lock.withLock {
            val snapshot = _layers.value
            _layers.value = layers.toList()
            _activeLayer.value = activeLayerId?.let { id ->
                layers.firstOrNull { it.id == id } ?: layers.lastOrNull()
            }
            snapshot
        }
        previous.forEach { it.onDetach() }
        layers.forEach { it.onAttach() }
        notifyLayerObservers()
        notifyActiveLayerObservers()
    }

    /**
     * 注册图层列表监听器，返回用于移除监听的函数。
     */
    fun addLayerObserver(observer: LayerListObserver, notifyImmediately: Boolean = true): () -> Unit {
        layerObservers.add(observer)
        if (notifyImmediately) {
            observer.onLayersChanged(_layers.value)
        }
        return { layerObservers.remove(observer) }
    }

    /**
     * 注册激活图层监听器，返回用于移除监听的函数。
     */
    fun addActiveLayerObserver(observer: ActiveLayerObserver, notifyImmediately: Boolean = true): () -> Unit {
        activeLayerObservers.add(observer)
        if (notifyImmediately) {
            observer.onActiveLayerChanged(_activeLayer.value)
        }
        return { activeLayerObservers.remove(observer) }
    }

    private fun moveByOffset(layerId: UUID, offset: Int): Boolean {
        var moved = false
        lock.withLock {
            if (offset == 0) return false
            val mutable = _layers.value.toMutableList()
            val currentIndex = mutable.indexOfFirst { it.id == layerId }
            if (currentIndex != -1 && mutable.isNotEmpty()) {
                val targetIndex = (currentIndex + offset).coerceIn(0, mutable.lastIndex)
                if (currentIndex != targetIndex) {
                    val layer = mutable.removeAt(currentIndex)
                    mutable.add(targetIndex, layer)
                    _layers.value = mutable.toList()
                    moved = true
                }
            }
        }
        if (moved) notifyLayerObservers()
        return moved
    }

    private fun notifyLayerObservers() {
        if (layerObservers.isEmpty()) return
        val snapshot = _layers.value
        layerObservers.forEach { it.onLayersChanged(snapshot) }
    }

    private fun notifyActiveLayerObservers() {
        if (activeLayerObservers.isEmpty()) return
        val active = _activeLayer.value
        activeLayerObservers.forEach { it.onActiveLayerChanged(active) }
    }
}

fun interface LayerListObserver {
    fun onLayersChanged(layers: List<Layer>)
}

fun interface ActiveLayerObserver {
    fun onActiveLayerChanged(activeLayer: Layer?)
}

