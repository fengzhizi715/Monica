# 图层系统概览

本文档记录 Monica 图层系统的最新实现，用于指导后续的功能扩展与维护。

## 核心目标

- 支持图像层与形状层的叠加管理，便于多图层编辑。
- 统一渲染与导出流程，避免重复绘制逻辑。
- 提供直观的 UI 面板，用于图层的增删、排序、锁定与重命名。

## 主要模块

| 模块 | 关键文件 | 功能 |
| --- | --- | --- |
| 图层抽象 | `editor/layer/Layer.kt` | 统一的图层基类，封装名称、可见性、透明度、锁定状态等属性。 |
| 图层管理 | `editor/layer/LayerManager.kt` | 负责图层增删改查、排序、激活状态同步，提供监听机制。使用 `StateFlow` 实现响应式更新。 |
| 图像层 | `editor/layer/ImageLayer.kt` | 保存背景位图及平移、缩放、旋转等变换信息。支持自动适应画布并居中显示。 |
| 形状层 | `editor/layer/ShapeLayer.kt` | 承载形状绘制数据（线段、矩形、多边形、文本等）。当前限制最多创建 1 个形状层。 |
| 渲染器 | `editor/layer/LayerRenderer.kt` | 顺序遍历图层并绘制到 Compose `DrawScope`，支持透明度合成。 |
| 导出管理 | `export/ExportManager.kt` | 调用渲染器输出 `ImageBitmap` 或 `BufferedImage`，确保导出尺寸与显示一致。 |
| 控制器 | `editor/EditorController.kt` | 整合管理器、渲染器、导出流程，并暴露工具切换、图层同步接口。限制形状层数量为 1。 |

## 工作流

```
用户交互 → EditorController → LayerManager → LayerRenderer → Canvas
                                 ↓
                           ExportManager
```

1. UI 侧（例如形状绘制视图）通过 `EditorController` 获取或创建图层。
2. 用户绘制的形状实时写入当前激活的 `ShapeLayer`。
3. `CanvasView` 调用 `LayerRenderer.drawAll()` 依次绘制每个图层，并根据透明度应用 `saveLayer`。
4. 导出功能复用渲染器，将所有图层合成为位图或 AWT 图像。

## UI 面板

文件：`ui/layer/LayerPanel.kt`

- 左侧卡片式列表展示所有图层（顶部为最新图层）。
- 支持：
  - 可见性勾选
  - 锁定/解锁（锁定后图标变红）
  - 重命名（内联编辑）
  - 上移/下移排序
  - 新建形状层（按钮显示"已达上限"当达到限制时）
  - 新建图像层
- 激活的图层使用浅色高亮和边框，提供即时视觉反馈。
- 当前激活的形状层显示"• 当前绘制"标识。

## 关键交互

- **初始化背景层**：`ShapeDrawingView` 在载入图像时，通过 `LaunchedEffect(imageBitmap)` 从 `LayerManager` 中查找名为"背景图层"的图层，如果不存在则创建，如果存在则更新图像。确保状态同步，避免使用本地状态变量。
- **形状写入**：每次拖动事件结束后，调用 `EditorController.replaceShapesInActiveLayer` 更新层数据。如果形状层已锁定，则禁止写入。在 `onDrag` 和 `onDragEnd` 中都会调用 `syncShapeLayer()` 同步形状数据。
- **图像层拖动**：当激活图层为图像层且未锁定时，可以直接拖动图像层调整位置。拖动时更新 `LayerTransform.translation`，该变换会在自动适应和居中之后应用。
- **导出**：点击保存按钮时，使用 `EditorController.exportBufferedImage` 获取合成结果。导出时使用显示尺寸（`ImageSizeCalculator.getImageDisplayPixelSize`），并考虑 Canvas padding（8.dp），确保导出结果与显示效果一致。

## 设计决策

### 形状层限制
- **限制数量**：当前实现限制最多创建 1 个形状层（`MAX_SHAPE_LAYERS = 1`），简化设计，避免多形状层带来的复杂性。
- **图像层无限制**：支持创建多个图像层，每个图像层可以独立拖动和变换。

### 背景层识别
- **识别方式**：通过图层名称 `"背景图层"` 来识别背景层，而不是通过图像尺寸或其他属性。这种方式更可靠，不受图像尺寸变化影响。
- **渲染差异**：
  - 背景层：只应用自动适应和居中（`fitScale` 和 `centerOffset`），不应用用户定义的变换（`transform.translation`、`transform.rotation`、`transform.scaleX/Y`）。
  - 用户添加的图像层：先应用自动适应和居中，再应用用户定义的变换。这样可以确保图像层在自动适应后，用户还可以进一步调整位置、旋转和缩放。

### 坐标系统
- **统一坐标**：使用显示尺寸（`ImageSizeCalculator.getImageDisplayPixelSize`）作为坐标基准，确保绘制、显示和导出的一致性。导出时也会使用相同的显示尺寸，并减去 Canvas padding（8.dp × 2 = 16.dp），确保导出结果与显示效果完全一致。
- **坐标转换器**：`CoordinateConverter` 通过 `remember(state.currentImage, density.density)` 创建，当图像或密度变化时会自动重新计算转换比例，确保坐标转换的准确性。

### 安全保护
- **除零保护**：`ImageLayer.render()` 中在计算缩放比例前检查 `bitmap.width`、`bitmap.height`、`canvasWidth`、`canvasHeight` 是否大于 0，如果任一值为 0 或负数则直接返回，防止除零错误。
- **锁定检查**：
  - 在 `EditorController.addShapeToActiveLayer` 和 `replaceShapesInActiveLayer` 中检查形状层是否锁定，锁定状态下禁止修改。
  - 在 `EditorController.canDrawOnActiveShapeLayer()` 中检查当前激活的形状层是否锁定，用于 UI 交互前的验证。
  - 在 `ShapeDrawingView` 的拖动事件处理中，如果形状层已锁定，会显示提示并阻止绘制操作。

## 测试覆盖

| 测试文件 | 覆盖点 |
| --- | --- |
| `src/jvmTest/.../LayerManagerTest.kt` | 图层添加、激活同步、排序、清空等行为。 |
| `src/jvmTest/.../ExportManagerTest.kt` | 图像层合成正确性。 |

> 当前测试依赖 `kotlin("test")`，位于 `build.gradle.kts` 的 `jvmTest` SourceSet 中。

## 已知问题与修复

### 已修复的问题

1. **除零错误保护**（2024-12）
   - 问题：`ImageLayer.render()` 在 `bitmap.width` 或 `bitmap.height` 为 0 时可能发生除零错误。
   - 修复：添加安全检查，在渲染前验证尺寸有效性。

2. **坐标转换器不更新**（2024-12）
   - 问题：`CoordinateConverter` 使用 `remember` 无依赖项，图像尺寸变化时不更新。
   - 修复：添加 `state.currentImage` 和 `density.density` 作为依赖项。

3. **背景层状态同步**（2024-12）
   - 问题：使用本地 `backgroundLayer` 状态变量（`remember { mutableStateOf<ImageLayer?>(null) }`），与 `LayerManager` 不同步。如果用户通过其他方式修改了背景层，本地状态不会更新。
   - 修复：移除了本地状态变量，改为在 `LaunchedEffect(imageBitmap)` 中直接从 `LayerManager.layers.value` 查找背景层，确保状态一致性。

4. **导出尺寸不一致**（2024-12）
   - 问题：导出时使用原始像素尺寸，与显示尺寸不一致。
   - 修复：导出时使用显示尺寸，并考虑 Canvas padding（8.dp），确保导出结果与显示效果一致。

## 后续待办

- 形状层透明度、混合模式等高级属性。
- 图层拖拽排序（UI 交互层面，当前仅支持上移/下移按钮）。
- 控制器与其它工具模块（涂鸦、滤镜等）的整合策略。
- 渲染性能评估与缓存机制。
- 背景层删除保护（如果未来在 `LayerPanel` 中添加删除功能，需要防止删除背景层）。
- 图像层的旋转和缩放交互（当前仅支持拖动位置）。

> 📋 **详细优化路线图**: 请参考 [图层系统优化路线图](./layer_system_optimization_roadmap.md) 获取完整的优化计划、实施细节和时间估算。

如需扩展新的图层类型，建议：

1. 新建 `Layer` 子类，实现数据结构与 `render()`。
2. 在 `LayerRenderer` 中添加对应的绘制分支。
3. 在 `LayerPanel` 中增加图标、操作项。
4. 补充单元测试覆盖新增逻辑。



