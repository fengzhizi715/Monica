package cn.netdiscovery.monica.ui.controlpanel.shapedrawing.layer

import java.util.UUID

data class LayerGroup(
    val id: UUID = UUID.randomUUID(),
    val name: String
)
