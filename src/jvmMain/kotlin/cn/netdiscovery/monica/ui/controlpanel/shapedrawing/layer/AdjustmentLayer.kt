package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import java.util.UUID

data class LayerAdjustment(
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f
)

class AdjustmentLayer(
    name: String,
    adjustment: LayerAdjustment = LayerAdjustment(),
    targetLayerIds: Set<UUID> = emptySet(),
    visible: Boolean = true,
    opacity: Float = 1f,
    locked: Boolean = false,
    blendMode: LayerBlendMode = LayerBlendMode.NORMAL,
    groupId: UUID? = null,
    id: UUID = UUID.randomUUID()
) : Layer(
    type = LayerType.ADJUSTMENT,
    id = id,
    name = name,
    visible = visible,
    opacity = opacity,
    locked = locked,
    blendMode = blendMode,
    groupId = groupId
) {

    var adjustment = adjustment
        private set

    var targetLayerIds = targetLayerIds
        private set

    fun updateAdjustment(newAdjustment: LayerAdjustment) {
        if (adjustment != newAdjustment) {
            adjustment = newAdjustment
            markDirty()
        }
    }

    fun updateTargets(newTargetLayerIds: Set<UUID>) {
        if (targetLayerIds != newTargetLayerIds) {
            targetLayerIds = newTargetLayerIds
            markDirty()
        }
    }
}
