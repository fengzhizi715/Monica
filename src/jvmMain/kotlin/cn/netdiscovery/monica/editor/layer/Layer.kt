package cn.netdiscovery.monica.editor.layer

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
     * 重命名当前图层。
     */
    fun rename(newName: String) {
        if (name != newName) {
            name = newName
        }
    }

    /**
     * 切换图层可见性。
     */
    fun setVisibility(isVisible: Boolean) {
        visible = isVisible
    }

    /**
     * 更新透明度，范围自动限制在 0f..1f。
     */
    fun updateOpacity(alpha: Float) {
        opacity = alpha.coerceIn(0f, 1f)
    }

    /**
     * 切换锁定状态。
     */
    fun updateLocked(isLocked: Boolean) {
        locked = isLocked
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

