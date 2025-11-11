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
| 图层管理 | `editor/layer/LayerManager.kt` | 负责图层增删改查、排序、激活状态同步，提供监听机制。 |
| 图像层 | `editor/layer/ImageLayer.kt` | 保存背景位图及平移、缩放、旋转等变换信息。 |
| 形状层 | `editor/layer/ShapeLayer.kt` | 承载形状绘制数据（线段、矩形、多边形、文本等）。 |
| 渲染器 | `editor/layer/LayerRenderer.kt` | 顺序遍历图层并绘制到 Compose `DrawScope`。 |
| 导出管理 | `export/ExportManager.kt` | 调用渲染器输出 `ImageBitmap` 或 `BufferedImage`。 |
| 控制器 | `editor/EditorController.kt` | 整合管理器、渲染器、导出流程，并暴露工具切换、图层同步接口。 |

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
  - 锁定/解锁
  - 重命名（内联编辑）
  - 上移/下移排序
  - 新建形状层
- 激活的图层使用浅色高亮，提供即时视觉反馈。

## 关键交互

- **初始化背景层**：`ShapeDrawingView` 在载入图像时创建 `ImageLayer`，并保持同步。
- **形状写入**：每次拖动事件结束后，调用 `EditorController.replaceShapesInActiveLayer` 更新层数据。
- **导出**：点击保存按钮时，使用 `EditorController.exportBufferedImage` 获取合成结果。

## 测试覆盖

| 测试文件 | 覆盖点 |
| --- | --- |
| `src/jvmTest/.../LayerManagerTest.kt` | 图层添加、激活同步、排序、清空等行为。 |
| `src/jvmTest/.../ExportManagerTest.kt` | 图像层合成正确性。 |

> 当前测试依赖 `kotlin("test")`，位于 `build.gradle.kts` 的 `jvmTest` SourceSet 中。

## 后续待办

- 形状层透明度、混合模式等高级属性。
- 图层拖拽排序（UI 交互层面）。
- 控制器与其它工具模块（涂鸦、滤镜等）的整合策略。
- 渲染性能评估与缓存机制。

如需扩展新的图层类型，建议：

1. 新建 `Layer` 子类，实现数据结构与 `render()`。
2. 在 `LayerRenderer` 中添加对应的绘制分支。
3. 在 `LayerPanel` 中增加图标、操作项。
4. 补充单元测试覆盖新增逻辑。



