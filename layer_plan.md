我打算在形状绘制模块的基础上实现「图层系统（Layer System）」功能，从工程结构、逻辑流程、数据流、UI 层级 这四个角度系统梳理 Monica 的 Layer 流程图。

# 🎯一、整体目标

让 Monica 支持「多图层编辑」，例如：

背景层（原始图像）
若干个图像层（贴图、素材）
若干个形状层（矩形、文字、路径等）
最后合成导出成单张图

# 🧱 二、工程结构推荐

这个结构只是参考，实际以当前 Monica 为准
```
monica/
├── editor/
│   ├── LayerManager.kt          # 管理所有 Layer 的生命周期、顺序
│   ├── Layer.kt                 # 抽象类 / sealed class 定义
│   ├── ImageLayer.kt            # 图像层实现
│   ├── ShapeLayer.kt            # 形状层实现
│   └── LayerRenderer.kt         # 渲染与合成逻辑（OpenCV/Canvas）
│
├── shapes/
│   ├── Shape.kt                 # Drawable 接口 + 抽象类
│   ├── RectShape.kt
│   ├── CircleShape.kt
│   ├── PathShape.kt
│   └── ShapeTool.kt             # 形状编辑工具逻辑
│
├── ui/
│   ├── LayerPanel.kt            # 图层侧边栏（Compose）
│   ├── CanvasView.kt            # 中央画布，渲染 LayerManager
│   ├── Toolbar.kt               # 工具栏（选择、画形状、导入图像）
│   └── StatusBar.kt
│
└── export/
└── ExportManager.kt         # 导出与合成逻辑（flattenToBitmap）
```

# 🧠 三、核心流程图（高层逻辑）

```
[用户操作] → [UI 控件层] → [EditorController] → [LayerManager] → [各 Layer]
↓                          ↓                    ↓
选择工具                  管理 Layer 顺序       各 Layer 负责 draw()
↓                          ↓                    ↓
[Canvas 重新绘制] ←───── [LayerManager.drawAll(canvas)]
```

# 🔄 四、Layer 生命周期流程（详细版）
| 阶段           | 触发源               | 操作                                 | 主要类                               |
| ------------ | ----------------- | ---------------------------------- | --------------------------------- |
| **1. 添加图层**  | 用户点击“➕ 新图像层”按钮    | 创建 ImageLayer 对象，加入 LayerManager   | `LayerManager.addLayer()`         |
| **2. 绘制层**   | Compose Canvas 重绘 | LayerManager 依次调用每个 Layer 的 draw() | `Layer.draw()`                    |
| **3. 编辑形状**  | 鼠标拖拽 / 点击         | 若当前选中 ShapeLayer，则更新对应 Shape 的几何信息 | `ShapeLayer`, `ShapeTool`         |
| **4. 变换图像层** | 拖动 / 缩放 / 旋转      | 更新 ImageLayer 的 transform 属性       | `ImageLayer`                      |
| **5. 预览合成**  | 屏幕渲染              | 所有 Layer 在 Canvas 上按顺序叠加           | `LayerManager.drawAll()`          |
| **6. 导出**    | 点击“导出”按钮          | 将所有 Layer 渲染到单独 Bitmap，再写入文件       | `ExportManager.flattenToBitmap()` |

🧩 五、数据流逻辑（单次渲染管线）

```
   LayerManager.drawAll(canvas)
   ├── for layer in layers:
   │      ├── if (layer.visible)
   │      ├── apply layer.opacity
   │      ├── layer.draw(canvas)
   │      │    ├── ImageLayer → drawBitmap()
   │      │    └── ShapeLayer → for shape in shapes → shape.draw(canvas)
   │      └── restore state
   └── 合成完成
```

💡 每个 Layer 独立负责：

自己的渲染逻辑

自己的编辑状态（选中、锁定、透明度）

# 🧱 六、主要类的关系（UML 概要）

```
+------------------+
| LayerManager     |
|------------------|
| + addLayer()     |
| + removeLayer()  |
| + moveUp()       |
| + drawAll()      |
+--------+---------+
|
| 1..*
v
+---------------------+
| Layer (abstract)    |
|---------------------|
| name, visible, opacity |
| + draw(Canvas)      |
+---------------------+
/           \
/             \
+----------+     +-------------+
|ImageLayer|     |ShapeLayer   |
|----------|     |-------------|
|bitmap... |     |shapes[]     |
|draw()    |     |draw()       |
+----------+     +-------------+
|
| 1..*
v
+------------+
|Shape       |
|------------|
|draw(Canvas)|
+------------+
```

# 🧮 七、导出流程

```
[点击“导出”按钮]
↓
LayerManager.flattenToBitmap(width, height)
↓
生成 Bitmap
↓
bitmap.compress(PNG/JPEG)
↓
保存到磁盘或拷贝到剪贴板
```

💡 八、Compose 层（UI 交互流）

1️⃣ CanvasView.kt

```
Canvas(modifier = Modifier.fillMaxSize()) {
layerManager.drawAll(drawContext.canvas.nativeCanvas)
}
```

2️⃣ LayerPanel.kt

```
Column {
layerManager.getLayers().reversed().forEach { layer ->
Row {
Checkbox(layer.visible) { layer.visible = it }
Text(layer.name)
IconButton(onClick = { layerManager.moveLayerUp(layer) }) { Icon(...)}
}
}
Button(onClick = { layerManager.addLayer(ImageLayer("Image")) }) { Text("Add Image") }
}
```

3️⃣ Toolbar.kt

“添加图层”、“画矩形”、“移动工具”等按钮切换当前编辑状态

# 🧠 九、Cursor 中的协作建议

用 Cursor 协同开发，推荐拆成以下任务卡（每个卡片是一个 Cursor 项目块）：

| 模块           | 文件                                     | 描述                  |
| ------------ | -------------------------------------- | ------------------- |
| ✅ Layer 抽象系统 | `Layer.kt`, `LayerManager.kt`          | 定义所有 Layer 的接口与基础逻辑 |
| 🧩 图像层       | `ImageLayer.kt`                        | 加载、缩放、绘制 Bitmap     |
| 🧩 形状层       | `ShapeLayer.kt`, `Shape.kt`            | 矢量绘制逻辑              |
| 🎨 渲染与导出     | `LayerRenderer.kt`, `ExportManager.kt` | 合成输出                |
| 🧰 UI 面板     | `LayerPanel.kt`, `CanvasView.kt`       | 交互界面与事件响应           |
| 🧠 控制器       | `EditorController.kt`                  | 管理状态、当前选中层、工具模式     |

这样每个模块在 Cursor 里都可以独立编辑和预览。

# ✅ 十、总结（一句话版）

Monica 的 Layer 系统是一个三层架构：

控制层（Controller）：响应用户操作（选层、编辑、导出）
数据层（LayerManager + Layer）：维护图层结构与属性
渲染层（Canvas）：将所有图层合成为最终图像