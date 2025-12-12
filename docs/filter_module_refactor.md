# 滤镜模块 UI 重构与优化说明（2025-12）

## 背景与目标

本次重构的核心目标：

- **提升 UI 可用性与一致性**：对齐、间距、状态提示更清晰，符合图像编辑软件的交互预期。
- **保持业务逻辑不变/可控演进**：在不破坏滤镜算法实现的前提下，整理 UI 状态与交互。
- **提升性能与稳定性**：拖动体验更顺滑，避免频繁计算与 CPU 抖动；修复已知崩溃点与错位问题。

---

## 关键交互语义（最终形态）

### 1）拖动即提交（去掉 Apply 按钮）

- **滤镜选择**：点击某个滤镜后，立即在编辑器画布上应用一次（并记录一次历史）。
- **参数调整**：
  - 拖动过程中：仍会以 **300ms 抽样**方式触发预览（降低计算频率）。
  - 松手后：**立即提交**到编辑器画布（并记录一次历史），避免多次历史碎片化。
  - 文本输入：输入过程只做预览；按 `Done` 后提交一次。

> 说明：提交时使用“进入滤镜模块前的基线图”作为输入，避免在 `currentImage` 上反复叠加导致效果漂移。

### 2）Reset / Cancel / 清除滤镜

- **Reset（重置滤镜）**：恢复当前滤镜的默认参数，并立即提交一次（记录历史）。
- **清除滤镜**：恢复到进入滤镜模块前的效果（基线图），记录一次历史，并取消滤镜选中态。
- **Cancel**：用于取消未提交的预览态（例如仅有 previewImage），回到上次提交参数快照并清理预览。

---

## UI 结构拆分与状态管理

### 1）去全局状态（多实例安全）

移除文件级全局变量 `filterSelectedIndex` / `filterTempMap`，改为在 `filter()` 内使用 `remember` 状态：

- `selectedIndexState`
- `paramMap`（`mutableStateMapOf`）
- `appliedParamSnapshot`
- `baseImageSnapshot`（进入模块前基线图）

避免了多窗口/多次进入模块时状态串扰的问题。

### 2）右侧面板底栏固定

修复了右侧面板滚动区域占满高度导致底部按钮区域不可见的问题：滚动区使用 `weight(1f)`，底栏固定展示。

### 3）收起时参数摘要

当参数区收起时，展示“参数摘要”卡片：

- 默认/已调整项数量
- 展示部分差异项
- Reset 提示（引导用户使用底部按钮）

---

## 参数范围与格式化（配置化）

新增参数 UI 元信息：

- `FilterParamMeta(min, max, step, decimals)`
- `FilterParamMetaRegistry.resolve(filterName, param)`：统一解析范围、步长、显示小数位。

并新增默认参数构建工具：

- `buildDefaultParamMap(filterName)`：用于初始化/Reset/判断是否处于默认参数状态。
- Float/Double 默认值按 `decimals` 统一格式化，避免 UI 显示不一致。

### BlockFilter 安全修复（step=0 崩溃）

问题：`BlockFilter` 内部将 `blockSize` 用于 `range.step(blockSize)`，当 `blockSize=0` 会直接抛异常。

修复策略（三道防线）：

1. `FilterParamMetaRegistry` 为 `blockSize` 设置 `min=1`。
2. 默认参数构建时按 meta.min 对 Int 进行 clamp（即使缓存里有 0 也会被纠正）。
3. `BlockFilter` 构造函数内防御性修复：`max(1, blockSize)`，彻底杜绝 crash。

---

## 性能优化

### 1）Slider 抽样预览（300ms）+ 松手提交

拖动过程中不实时提交，降低重算频率；松手后一次性提交，历史更干净。

### 2）预览缓存（同滤镜 + 同参数 hash 命中）

在 `FilterViewModel.applyFilterPreview()` 中引入 LRU 预览缓存：

- Key：`baseImageId(identityHashCode) + filterName + paramsHash(稳定排序后 hash)`
- 策略：LRU + 双阈值淘汰（条目数 + 估算内存上限）
- `clear()` 时会清空缓存，避免跨页面持有内存

收益：重复参数回退/来回拖动时命中缓存，CPU 更稳、预览更顺滑。

---

## Bug 修复汇总

- **搜索列表点击/选中错位**：修复 `itemsIndexed` 使用位置 index 误当真实 filterIndex 的问题。
- **英文硬编码**：如 `No Image` 等占位文案改为 i18n。
- **右侧按钮不显示**：滚动区域 `fillMaxSize()` 挤掉底栏的问题修复为 `weight(1f)`。
- **BlockFilter step=0 崩溃**：如上“三道防线”修复，并处理缓存里持久化为 0 的脏数据。

---

## 国际化（i18n）新增/补充 Key（节选）

- `no_image` / `no_filters_found`
- `param_summary` / `param_summary_default` / `param_summary_changed_count` / `param_summary_reset_hint`
- `clear_filter`

---

## 涉及文件清单（主要）

- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterView.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterListPanel.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterPreviewArea.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterAdjustmentPanel.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterViewModel.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterParamMeta.kt`
- `src/jvmMain/kotlin/cn/netdiscovery/monica/ui/controlpanel/filter/FilterParamDefaults.kt`
- `imageprocess/src/main/kotlin/cn/netdiscovery/monica/imageprocess/filter/BlockFilter.kt`
- `i18n/src/main/resources/strings/strings_zh.xml`
- `i18n/src/main/resources/strings/strings_en.xml`

---

## 已知未完成项 / 后续建议

- **导出功能**：`FilterTopAppBar` 的 Export 仍为 TODO。
- **日志与可观测性**：当前仍存在少量 `logger.info(...)`（如 FilterView 生命周期/FilterViewModel applyFilter）。建议后续统一降噪或增加 debug 开关。
- **可访问性**：个别 `contentDescription` 仍为英文（如 Zoom In），可按 i18n 统一。


