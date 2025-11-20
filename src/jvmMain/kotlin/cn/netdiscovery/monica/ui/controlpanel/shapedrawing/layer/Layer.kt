package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.drawscope.DrawScope
import java.util.UUID

/**
 * 图层类型枚举
 *
 * 目前仅支持图像层与形状层，未来可扩展到调整层、文本层等。
 */
enum class LayerType {
    IMAGE,
    SHAPE
}

/**
 * Layer 抽象基类，封装所有图层的公共属性与生命周期。
 *
 * @property type 图层类型
 * @property id 图层唯一标识
 * @property name 图层名称
 * @property visible 是否可见
 * @property opacity 透明度，范围 0f..1f
 * @property locked 是否锁定（锁定后禁止编辑/变换）
 */
@Stable
abstract class Layer(
    val type: LayerType,
    val id: UUID = UUID.randomUUID(),
    name: String,
    visible: Boolean = true,
    opacity: Float = 1f,
    locked: Boolean = false
) {

    var name by mutableStateOf(name)
        private set

    var visible by mutableStateOf(visible)
        private set

    var opacity by mutableStateOf(opacity.coerceIn(0f, 1f))
        private set

    var locked by mutableStateOf(locked)
        private set

    /**
     * 图层版本号，用于标记图层变化，优化渲染缓存
     * 当图层属性变化时，版本号递增，触发缓存失效
     * 
     * 注意：使用普通变量而非 State，避免在 Compose 中读取时触发不必要的重组
     * 版本号主要用于标记变化，不直接参与 Compose 状态管理
     */
    private var _version = 0L
    val version: Long get() = _version

    /**
     * 标记图层为脏状态，递增版本号
     * 子类在属性变化时应调用此方法
     */
    protected fun markDirty() {
        _version++
    }

    /**
     * 重命名当前图层。
     */
    fun rename(newName: String) {
        if (name != newName) {
            name = newName
            markDirty()
        }
    }

    /**
     * 切换图层可见性。
     */
    fun setVisibility(isVisible: Boolean) {
        if (visible != isVisible) {
            visible = isVisible
            markDirty()
        }
    }

    /**
     * 更新透明度，范围自动限制在 0f..1f。
     */
    fun updateOpacity(alpha: Float) {
        val newOpacity = alpha.coerceIn(0f, 1f)
        if (opacity != newOpacity) {
            opacity = newOpacity
            markDirty()
        }
    }

    /**
     * 切换锁定状态。
     */
    fun updateLocked(isLocked: Boolean) {
        if (locked != isLocked) {
            locked = isLocked
            markDirty()
        }
    }

    /**
     * 当图层被加入 LayerManager 时回调。
     */
    open fun onAttach() {}

    /**
     * 当图层被移出 LayerManager 时回调。
     */
    open fun onDetach() {}

    /**
     * 将当前图层绘制到指定的 [DrawScope] 中。
     *
     * 默认实现为空，由具体图层自行实现。
     */
    open fun render(drawScope: DrawScope) = Unit
}

