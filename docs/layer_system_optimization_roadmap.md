# 图层系统优化路线图

本文档记录 Monica 图层系统的优化方向和实施计划，用于指导后续的功能扩展与性能提升。

**文档版本**: 1.0  
**最后更新**: 2024-12  
**维护者**: Monica 开发团队

---

## 📋 目录

- [一、功能扩展](#一功能扩展)
- [二、性能优化](#二性能优化)
- [三、代码质量提升](#三代码质量提升)
- [四、架构优化](#四架构优化)
- [五、用户体验改进](#五用户体验改进)
- [六、实施计划](#六实施计划)
- [七、技术债务清理](#七技术债务清理)
- [八、监控与评估](#八监控与评估)

---

## 一、功能扩展

### 1.1 UI 交互增强

#### 1.1.1 图层删除功能 ⭐ 高优先级
**当前状态**: `LayerPanel` 中没有删除按钮

**目标**:
- 在图层卡片中添加删除按钮（垃圾桶图标）
- 删除前显示确认对话框
- 防止误删除重要图层

**实施要点**:
```kotlin
// 在 LayerPanel.kt 中添加删除按钮
IconButton(
    onClick = {
        if (layer.name == "背景图层") {
            state.showTray("无法删除背景图层", "提示")
        } else {
            // 显示确认对话框
            showDeleteConfirmDialog = true
        }
    }
) {
    Icon(Icons.Default.Delete, "删除图层")
}
```

**技术细节**:
- 添加背景层删除保护机制
- 删除后自动激活上一个图层
- 支持撤销删除（如果实现撤销/重做功能）

**预计工作量**: 2-3 天

---

#### 1.1.2 拖拽排序 ⭐ 高优先级
**当前状态**: 仅支持上移/下移按钮

**目标**:
- 实现图层卡片拖拽排序
- 提供更直观的交互体验

**实施要点**:
```kotlin
// 使用 Compose 的拖拽 API
Modifier
    .pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            // 处理拖拽逻辑
        }
    }
```

**技术细节**:
- 使用 `Modifier.draggable()` 或 `Modifier.pointerInput()` 实现拖拽
- 拖拽时显示视觉反馈（高亮、阴影）
- 拖拽结束后更新图层顺序

**预计工作量**: 3-5 天

---

#### 1.1.3 图层缩略图预览
**当前状态**: 图层卡片仅显示类型图标

**目标**:
- 在图层卡片中显示缩略图
- 提升图层识别度

**实施要点**:
- 为 `ImageLayer` 生成缩略图（缓存）
- 为 `ShapeLayer` 生成预览图
- 使用 `remember` 缓存缩略图，避免重复计算

**预计工作量**: 2-3 天

---

### 1.2 图像层交互增强

#### 1.2.1 旋转和缩放交互 ⭐ 高优先级
**当前状态**: 仅支持拖动位置

**目标**:
- 添加旋转手柄和控制点
- 支持鼠标滚轮缩放
- 支持右键旋转

**实施要点**:
```kotlin
// 在图像层周围添加控制点
data class ImageLayerControls(
    val translation: Offset,
    val rotation: Float,
    val scale: Float,
    val pivot: Offset
)

// 添加交互处理
fun handleImageLayerTransform(
    layerId: UUID,
    transformType: TransformType,
    value: Float
)
```

**技术细节**:
- 在 Canvas 上绘制控制点和旋转手柄
- 检测鼠标悬停和拖动
- 更新 `LayerTransform` 的 `rotation` 和 `scaleX/Y`

**预计工作量**: 5-7 天

---

#### 1.2.2 图像层裁剪
**当前状态**: 不支持裁剪

**目标**:
- 支持裁剪区域选择
- 添加遮罩功能

**实施要点**:
- 在 `ImageLayer` 中添加 `cropRect` 属性
- 渲染时应用裁剪区域
- UI 上显示裁剪控制点

**预计工作量**: 7-10 天

---

### 1.3 形状层功能扩展

#### 1.3.1 解除形状层数量限制（可选）
**当前状态**: 限制最多 1 个形状层（`MAX_SHAPE_LAYERS = 1`）

**目标**:
- 评估是否需要支持多个形状层
- 如需要，重构相关逻辑

**考虑因素**:
- 用户需求是否强烈
- 实现复杂度
- 对现有代码的影响

**预计工作量**: 5-10 天（取决于重构范围）

---

#### 1.3.2 形状层分组
**当前状态**: 不支持分组

**目标**:
- 支持形状分组管理
- 分组级别的可见性/锁定控制

**预计工作量**: 10-15 天

---

## 二、性能优化

### 2.1 渲染性能优化

#### 2.1.1 图层渲染缓存 ⭐ 中优先级
**当前状态**: 每次重绘都重新渲染所有图层

**目标**:
- 对未变化的图层使用缓存
- 减少不必要的重绘

**实施要点**:
```kotlin
class LayerRenderer {
    private val renderCache = mutableMapOf<UUID, ImageBitmap>()
    
    fun drawAll(drawScope: DrawScope, layers: List<Layer>) {
        layers.forEach { layer ->
            if (layer.isDirty) {
                renderCache.remove(layer.id)
                layer.isDirty = false
            }
            
            val cached = renderCache[layer.id]
            if (cached != null && !layer.isDirty) {
                // 使用缓存
                drawScope.drawImage(cached)
            } else {
                // 重新渲染并缓存
                val rendered = renderLayer(layer, drawScope)
                renderCache[layer.id] = rendered
            }
        }
    }
}
```

**技术细节**:
- 在 `Layer` 基类中添加 `isDirty` 标记
- 图层属性变化时设置 `isDirty = true`
- 使用 `remember` 在 Compose 中缓存渲染结果

**预计工作量**: 5-7 天

---

#### 2.1.2 增量渲染
**当前状态**: 所有图层每次都重绘

**目标**:
- 只重绘变化的图层
- 优化 Compose 重组

**实施要点**:
- 使用 `LaunchedEffect` 监听图层变化
- 只更新变化的图层区域
- 使用 `Modifier.drawWithCache` 优化绘制

**预计工作量**: 7-10 天

---

#### 2.1.3 大图像优化
**当前状态**: 可能对超大图像性能不佳

**目标**:
- 对超大图像使用缩略图预览
- 导出时使用全分辨率

**实施要点**:
- 在 `ImageLayer` 中维护缩略图
- 渲染时使用缩略图，导出时使用原图
- 实现渐进式加载

**预计工作量**: 5-7 天

---

### 2.2 内存优化

#### 2.2.1 图像层内存管理
**目标**:
- 实现图像压缩/解压缩策略
- 对不可见图层延迟加载

**实施要点**:
- 使用 `SoftReference` 缓存图像
- 实现 LRU 缓存策略
- 对不可见图层不加载到内存

**预计工作量**: 7-10 天

---

#### 2.2.2 形状数据优化
**目标**:
- 使用更高效的数据结构
- 考虑使用 `Path` 对象缓存

**实施要点**:
- 评估当前 `SnapshotStateMap` 的性能
- 考虑使用 `Path` 对象缓存复杂形状
- 实现形状数据的序列化/反序列化

**预计工作量**: 3-5 天

---

## 三、代码质量提升

### 3.1 测试覆盖

#### 3.1.1 单元测试扩展
**当前状态**: 已有基础测试（`LayerManagerTest.kt`、`ExportManagerTest.kt`）

**目标**:
- 提高测试覆盖率到 80% 以上
- 覆盖边界情况和异常情况

**需要测试的场景**:
- `ImageLayer.render()` 的边界情况（零尺寸、空图像等）
- `LayerManager` 的并发安全测试
- 坐标转换器的各种场景
- 图层变换的数学计算

**预计工作量**: 10-15 天

---

#### 3.1.2 集成测试
**目标**:
- 图层合成导出测试
- UI 交互测试

**实施要点**:
- 使用 Compose 测试框架
- 测试图层操作的完整流程
- 测试导出结果的正确性

**预计工作量**: 7-10 天

---

### 3.2 错误处理

#### 3.2.1 异常处理完善
**目标**:
- 图像加载失败处理
- 渲染错误恢复机制

**实施要点**:
```kotlin
// 在 ImageLayer 中添加错误处理
fun updateImage(newImage: ImageBitmap?) {
    try {
        image = newImage
    } catch (e: Exception) {
        logger.error("更新图像失败", e)
        // 显示错误提示
        // 恢复上一个有效图像
    }
}
```

**预计工作量**: 3-5 天

---

#### 3.2.2 用户反馈改进
**目标**:
- 更明确的错误提示
- 操作成功/失败的 Toast 提示

**实施要点**:
- 统一错误消息格式
- 添加操作成功提示
- 提供错误恢复建议

**预计工作量**: 2-3 天

---

### 3.3 代码重构

#### 3.3.1 背景层管理抽象
**当前状态**: 背景层管理逻辑分散在 `ShapeDrawingView` 中

**目标**:
- 创建 `BackgroundLayerManager` 统一管理背景层

**实施要点**:
```kotlin
class BackgroundLayerManager(
    private val layerManager: LayerManager
) {
    private val BACKGROUND_LAYER_NAME = "背景图层"
    
    fun getOrCreateBackgroundLayer(image: ImageBitmap): ImageLayer {
        val existing = layerManager.layers.value
            .firstOrNull { it.name == BACKGROUND_LAYER_NAME && it is ImageLayer } 
            as? ImageLayer
        
        return existing ?: run {
            val newLayer = ImageLayer(BACKGROUND_LAYER_NAME, image)
            layerManager.addLayer(newLayer, index = 0)
            newLayer
        }
    }
    
    fun updateBackgroundLayer(image: ImageBitmap) {
        val layer = getOrCreateBackgroundLayer(image)
        layer.updateImage(image)
    }
}
```

**预计工作量**: 2-3 天

---

#### 3.3.2 图层操作命令模式
**目标**:
- 实现撤销/重做功能
- 使用命令模式封装图层操作

**实施要点**:
```kotlin
interface LayerCommand {
    fun execute()
    fun undo()
}

class AddLayerCommand(
    private val layerManager: LayerManager,
    private val layer: Layer
) : LayerCommand {
    override fun execute() {
        layerManager.addLayer(layer)
    }
    
    override fun undo() {
        layerManager.removeLayer(layer.id)
    }
}

class CommandManager {
    private val undoStack = mutableListOf<LayerCommand>()
    private val redoStack = mutableListOf<LayerCommand>()
    
    fun execute(command: LayerCommand) {
        command.execute()
        undoStack.add(command)
        redoStack.clear()
    }
    
    fun undo() {
        if (undoStack.isNotEmpty()) {
            val command = undoStack.removeLast()
            command.undo()
            redoStack.add(command)
        }
    }
    
    fun redo() {
        if (redoStack.isNotEmpty()) {
            val command = redoStack.removeLast()
            command.execute()
            undoStack.add(command)
        }
    }
}
```

**预计工作量**: 10-15 天

---

## 四、架构优化

### 4.1 图层类型扩展

#### 4.1.1 文本层（独立图层类型）
**当前状态**: 文本在形状层中

**目标**:
- 创建独立的 `TextLayer` 类型
- 提供更专业的文本编辑功能

**实施要点**:
```kotlin
class TextLayer(
    name: String,
    var text: String = "",
    var font: Font = Font.Default,
    var fontSize: Float = 16f,
    var color: Color = Color.Black,
    var position: Offset = Offset.Zero
) : Layer(
    type = LayerType.TEXT,
    name = name
) {
    override fun render(drawScope: DrawScope) {
        // 文本渲染逻辑
    }
}
```

**预计工作量**: 7-10 天

---

#### 4.1.2 调整层（Adjustment Layer）
**目标**:
- 亮度、对比度、色彩调整
- 不影响原始图像数据

**实施要点**:
```kotlin
class AdjustmentLayer(
    name: String,
    var brightness: Float = 0f,
    var contrast: Float = 1f,
    var saturation: Float = 1f
) : Layer(
    type = LayerType.ADJUSTMENT,
    name = name
) {
    override fun render(drawScope: DrawScope) {
        // 应用调整效果到下层图层
    }
}
```

**预计工作量**: 15-20 天

---

#### 4.1.3 滤镜层
**目标**:
- 模糊、锐化等效果
- 可叠加多个滤镜

**实施要点**:
```kotlin
enum class FilterType {
    BLUR,
    SHARPEN,
    EMBOSS,
    // ...
}

class FilterLayer(
    name: String,
    var filterType: FilterType,
    var intensity: Float = 1f
) : Layer(
    type = LayerType.FILTER,
    name = name
)
```

**预计工作量**: 20-30 天

---

### 4.2 混合模式支持

#### 4.2.1 实现混合模式
**目标**:
- 支持多种混合模式（Normal、Multiply、Screen 等）
- 在图层合成时应用混合模式

**实施要点**:
```kotlin
enum class BlendMode {
    NORMAL,
    MULTIPLY,
    SCREEN,
    OVERLAY,
    SOFT_LIGHT,
    HARD_LIGHT,
    COLOR_DODGE,
    COLOR_BURN,
    DARKEN,
    LIGHTEN,
    DIFFERENCE,
    EXCLUSION
}

class Layer {
    var blendMode: BlendMode = BlendMode.NORMAL
}

// 在 LayerRenderer 中应用混合模式
fun drawAll(drawScope: DrawScope, layers: List<Layer>) {
    layers.forEach { layer ->
        drawScope.drawIntoCanvas { canvas ->
            // 应用混合模式
            val paint = Paint().apply {
                blendMode = when (layer.blendMode) {
                    BlendMode.NORMAL -> BlendMode.SrcOver
                    BlendMode.MULTIPLY -> BlendMode.Multiply
                    // ...
                }
            }
            // 绘制图层
        }
    }
}
```

**预计工作量**: 15-20 天

---

### 4.3 图层组（Layer Group）

#### 4.3.1 实现图层分组
**目标**:
- 支持图层分组
- 组级别的可见性/锁定控制
- 嵌套分组支持

**实施要点**:
```kotlin
class LayerGroup(
    name: String,
    val children: MutableList<Layer> = mutableListOf()
) : Layer(
    type = LayerType.GROUP,
    name = name
) {
    fun addChild(layer: Layer) {
        children.add(layer)
    }
    
    fun removeChild(layerId: UUID) {
        children.removeAll { it.id == layerId }
    }
    
    override fun render(drawScope: DrawScope) {
        if (!visible) return
        children.forEach { child ->
            if (child.visible) {
                child.render(drawScope)
            }
        }
    }
}
```

**预计工作量**: 20-30 天

---

## 五、用户体验改进

### 5.1 快捷键支持 ⭐ 高优先级

#### 5.1.1 实现常用快捷键
**目标**:
- 提供键盘快捷键支持
- 提升操作效率

**快捷键列表**:
- `Ctrl/Cmd + D` - 复制图层
- `Delete` / `Backspace` - 删除图层
- `Ctrl/Cmd + G` - 创建图层组
- `Ctrl/Cmd + Shift + N` - 新建图层
- `Ctrl/Cmd + J` - 复制并新建图层
- `Ctrl/Cmd + Shift + ]` - 图层上移
- `Ctrl/Cmd + Shift + [` - 图层下移
- `Ctrl/Cmd + Z` - 撤销（如果实现）
- `Ctrl/Cmd + Shift + Z` - 重做（如果实现）

**实施要点**:
```kotlin
// 在 ShapeDrawingView 中添加键盘事件处理
Modifier.onKeyEvent { keyEvent ->
    when {
        keyEvent.isCtrlPressed && keyEvent.key == Key.D -> {
            // 复制图层
            true
        }
        keyEvent.key == Key.Delete -> {
            // 删除图层
            true
        }
        else -> false
    }
}
```

**预计工作量**: 5-7 天

---

### 5.2 图层搜索/过滤

#### 5.2.1 实现搜索功能
**目标**:
- 按名称搜索图层
- 按类型过滤
- 显示/隐藏空图层

**实施要点**:
```kotlin
@Composable
fun LayerPanel(
    editorController: EditorController,
    state: ApplicationState,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterType by remember { mutableStateOf<LayerType?>(null) }
    
    val filteredLayers = remember(layers, searchQuery, filterType) {
        layers.filter { layer ->
            (searchQuery.isEmpty() || layer.name.contains(searchQuery, ignoreCase = true)) &&
            (filterType == null || layer.type == filterType)
        }
    }
    
    // UI 实现
}
```

**预计工作量**: 3-5 天

---

### 5.3 批量操作

#### 5.3.1 实现多选功能
**目标**:
- 支持多选图层
- 批量锁定/解锁
- 批量重命名

**实施要点**:
```kotlin
class LayerManager {
    private val _selectedLayers = MutableStateFlow<Set<UUID>>(emptySet())
    val selectedLayers: StateFlow<Set<UUID>> = _selectedLayers.asStateFlow()
    
    fun selectLayer(layerId: UUID, multiSelect: Boolean = false) {
        if (multiSelect) {
            _selectedLayers.value = _selectedLayers.value.toMutableSet().apply {
                if (contains(layerId)) remove(layerId) else add(layerId)
            }
        } else {
            _selectedLayers.value = setOf(layerId)
        }
    }
    
    fun batchLock(locked: Boolean) {
        _selectedLayers.value.forEach { id ->
            setLayerLocked(id, locked)
        }
    }
}
```

**预计工作量**: 7-10 天

---

## 六、实施计划

### 6.1 短期计划（1-2 周）

**优先级**: ⭐⭐⭐ 最高

1. **图层删除功能**（2-3 天）
   - 添加删除按钮
   - 实现背景层保护
   - 添加确认对话框

2. **拖拽排序**（3-5 天）
   - 实现拖拽交互
   - 更新图层顺序

3. **错误处理完善**（2-3 天）
   - 完善异常处理
   - 改进用户提示

**总工作量**: 7-11 天

---

### 6.2 中期计划（1-2 月）

**优先级**: ⭐⭐ 高

1. **图像层旋转/缩放交互**（5-7 天）
   - 添加控制点
   - 实现交互逻辑

2. **渲染性能优化**（5-7 天）
   - 实现渲染缓存
   - 优化重绘逻辑

3. **撤销/重做功能**（10-15 天）
   - 实现命令模式
   - 添加撤销/重做 UI

4. **快捷键支持**（5-7 天）
   - 实现常用快捷键
   - 添加快捷键提示

**总工作量**: 25-36 天

---

### 6.3 长期计划（3-6 月）

**优先级**: ⭐ 中

1. **混合模式支持**（15-20 天）
   - 实现各种混合模式
   - 添加 UI 选择器

2. **图层组功能**（20-30 天）
   - 实现分组逻辑
   - 添加分组 UI

3. **调整层和滤镜层**（35-50 天）
   - 实现调整层
   - 实现滤镜层
   - 添加效果预览

4. **测试覆盖扩展**（10-15 天）
   - 扩展单元测试
   - 添加集成测试

**总工作量**: 80-115 天

---

## 七、技术债务清理

### 7.1 代码清理

#### 7.1.1 移除未使用的代码
- 检查是否有废弃的 API
- 清理注释掉的代码
- 移除未使用的导入

**预计工作量**: 1-2 天

---

#### 7.1.2 文档完善
- 添加 API 文档（KDoc）
- 创建使用示例
- 编写架构决策记录（ADR）

**预计工作量**: 3-5 天

---

#### 7.1.3 代码规范
- 统一命名规范
- 代码格式化
- 添加必要的注释

**预计工作量**: 2-3 天

---

### 7.2 依赖管理

#### 7.2.1 依赖更新
- 定期更新依赖版本
- 评估新版本的功能和性能改进
- 处理废弃的 API

**预计工作量**: 持续进行

---

## 八、监控与评估

### 8.1 性能监控

#### 8.1.1 关键指标
- **渲染帧率**: 目标 60 FPS
- **内存使用**: 监控峰值内存
- **导出耗时**: 目标 < 2 秒（普通图像）

**实施要点**:
```kotlin
// 添加性能监控
class PerformanceMonitor {
    fun measureRenderTime(block: () -> Unit): Long {
        val start = System.currentTimeMillis()
        block()
        return System.currentTimeMillis() - start
    }
    
    fun logMemoryUsage() {
        val runtime = Runtime.getRuntime()
        val used = runtime.totalMemory() - runtime.freeMemory()
        logger.info("内存使用: ${used / 1024 / 1024} MB")
    }
}
```

---

### 8.2 用户反馈

#### 8.2.1 反馈收集
- 收集使用痛点
- 功能需求优先级
- Bug 报告

**实施要点**:
- 在应用中添加反馈入口
- 定期收集用户意见
- 建立需求优先级评估机制

---

### 8.3 代码质量指标

#### 8.3.1 质量指标
- **测试覆盖率**: 目标 80% 以上
- **代码复杂度**: 使用工具分析（如 SonarQube）
- **技术债务**: 定期评估和清理

**实施要点**:
- 使用代码质量工具
- 定期代码审查
- 技术债务跟踪

---

## 九、风险评估

### 9.1 技术风险

1. **性能风险**
   - 大量图层可能导致性能下降
   - **缓解措施**: 实现渲染缓存和增量渲染

2. **兼容性风险**
   - 新功能可能影响现有功能
   - **缓解措施**: 充分测试，渐进式发布

3. **复杂度风险**
   - 功能增加可能导致代码复杂度上升
   - **缓解措施**: 代码重构，模块化设计

---

### 9.2 时间风险

1. **估算不准确**
   - 实际工作量可能超过估算
   - **缓解措施**: 预留缓冲时间，分阶段实施

2. **优先级冲突**
   - 多个高优先级任务可能冲突
   - **缓解措施**: 明确优先级，合理分配资源

---

## 十、总结

本路线图提供了图层系统优化的全面规划，涵盖了功能扩展、性能优化、代码质量提升、架构优化、用户体验改进等多个方面。

**建议实施顺序**:
1. 先完成短期计划（1-2 周），快速提升用户体验
2. 然后进行中期计划（1-2 月），优化性能和添加核心功能
3. 最后推进长期计划（3-6 月），实现高级功能和架构优化

**关键成功因素**:
- 持续的用户反馈收集
- 定期的性能监控和优化
- 代码质量保证（测试、文档、规范）
- 渐进式实施，避免大范围重构

---

**文档维护**: 本文档应随着项目进展定期更新，记录实际完成情况、遇到的问题和调整的计划。

