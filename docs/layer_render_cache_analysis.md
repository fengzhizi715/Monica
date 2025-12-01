# 图层渲染缓存优化分析

## 当前实现分析

### 1. 渲染流程
- `CanvasView` 使用 `collectAsState()` 观察图层列表
- 每次图层变化都会触发 Canvas 重组和重绘
- `LayerRenderer.drawAll()` 遍历所有图层并调用 `render()`
- 每个图层都使用 `drawIntoCanvas` + `saveLayer`，有性能开销

### 2. 性能瓶颈
- **ImageLayer**: 每次重绘都重新计算变换（平移、旋转、缩放）
- **ShapeLayer**: 每次重绘都重新绘制所有形状
- **Canvas 重组**: 图层列表变化时，整个 Canvas 都会重组

## 优化方案对比

### 方案一：路线图中的 ImageBitmap 缓存（不推荐）

**问题**：
1. Compose 的 `DrawScope` 在每次重组时都会重新创建，不能直接缓存 `ImageBitmap`
2. 缓存需要考虑画布尺寸变化（Canvas 尺寸可能变化）
3. 需要考虑透明度、变换等属性的变化
4. 缓存失效逻辑复杂（何时清除缓存？）

**适用场景**：
- 静态图像，尺寸固定
- 不适用于动态变化的图层

### 方案二：Compose 级别的缓存（推荐）⭐

**核心思路**：
1. 使用 `remember` + `key()` 为每个图层创建独立的缓存
2. 使用 `Modifier.drawWithCache` 缓存绘制内容
3. 使用版本号/哈希值标记图层变化

**优势**：
- 利用 Compose 的缓存机制，自动管理生命周期
- 画布尺寸变化时自动失效
- 代码简洁，易于维护

**实现要点**：
```kotlin
@Composable
fun LayerRenderer(
    layers: List<Layer>,
    canvasSize: Size
) {
    layers.forEach { layer ->
        key(layer.id, layer.version) { // 版本号标记变化
            DrawScope.drawWithCache {
                // 缓存绘制内容
                onDrawBehind {
                    layer.render(this)
                }
            }
        }
    }
}
```

### 方案三：分层缓存策略（最佳）⭐⭐

**核心思路**：
1. **ImageLayer**: 缓存变换后的图像（使用 `drawWithCache`）
2. **ShapeLayer**: 使用 `remember` 缓存形状列表，避免重复计算
3. **组合优化**: 只重绘变化的图层区域

**实现要点**：

#### 1. Layer 基类添加版本号
```kotlin
abstract class Layer {
    private var _version by mutableStateOf(0L)
    val version: Long get() = _version
    
    protected fun markDirty() {
        _version++
    }
    
    // 属性变化时调用
    fun updateOpacity(alpha: Float) {
        opacity = alpha.coerceIn(0f, 1f)
        markDirty()
    }
}
```

#### 2. ImageLayer 缓存变换结果
```kotlin
@Composable
fun ImageLayerRenderer(
    layer: ImageLayer,
    canvasSize: Size
) {
    val cachedImage = remember(layer.id, layer.version, canvasSize) {
        // 计算变换后的图像
        renderToBitmap(layer, canvasSize)
    }
    
    Canvas(modifier = Modifier.drawWithCache {
        onDrawBehind {
            drawImage(cachedImage)
        }
    })
}
```

#### 3. ShapeLayer 缓存形状列表
```kotlin
@Composable
fun ShapeLayerRenderer(
    layer: ShapeLayer,
    canvasSize: Size
) {
    val shapes = remember(layer.id, layer.version) {
        // 缓存形状列表，避免重复计算
        layer.getAllShapes()
    }
    
    Canvas(modifier = Modifier) {
        shapes.forEach { shape ->
            drawShape(shape)
        }
    }
}
```

#### 4. LayerRenderer 优化
```kotlin
class LayerRenderer {
    fun drawAll(drawScope: DrawScope, layers: List<Layer>) {
        layers.forEach { layer ->
            if (!layer.visible || layer.opacity <= 0f) return@forEach
            
            // 使用 key 确保只有变化的图层才重绘
            key(layer.id, layer.version) {
                drawLayer(drawScope, layer)
            }
        }
    }
}
```

## 性能提升预估

### 当前性能
- 10 个图层，每次重绘耗时：~50ms
- 拖动图像层时：~16ms/frame（60fps 可能卡顿）

### 优化后性能
- 10 个图层，未变化时：~5ms（使用缓存）
- 拖动图像层时：~8ms/frame（只重绘变化的图层）

**提升**：约 5-10 倍性能提升

## 实施建议

### 阶段一：基础优化（2-3 天）
1. 在 `Layer` 基类添加 `version` 字段
2. 属性变化时调用 `markDirty()`
3. 使用 `key()` 优化 Compose 重组

### 阶段二：ImageLayer 缓存（2-3 天）
1. 使用 `remember` 缓存变换后的图像
2. 使用 `Modifier.drawWithCache` 缓存绘制内容
3. 处理画布尺寸变化

### 阶段三：ShapeLayer 优化（1-2 天）
1. 缓存形状列表
2. 优化形状绘制逻辑

### 阶段四：增量渲染（可选，3-5 天）
1. 只重绘变化的图层区域
2. 使用脏矩形技术

## 注意事项

1. **内存管理**：
   - 缓存会占用内存，需要设置上限
   - 使用 `SoftReference` 或 LRU 缓存

2. **缓存失效**：
   - 画布尺寸变化时自动失效
   - 图层属性变化时手动失效

3. **兼容性**：
   - 确保与现有代码兼容
   - 不影响导出功能

4. **测试**：
   - 测试大量图层场景
   - 测试频繁变化场景
   - 测试内存使用情况

## 结论

**推荐方案**：方案三（分层缓存策略）
- 性能提升明显
- 实现相对简单
- 易于维护和扩展
- 符合 Compose 最佳实践

**预计工作量**：5-7 天（与路线图一致）

**优先级**：中优先级（当前性能可接受，但优化后体验更好）






