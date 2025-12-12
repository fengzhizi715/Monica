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

---

## 未来优化方向（路线图建议）

### P0（高收益 / 低风险，建议优先）

- **提交任务的并发与取消策略**  
  - 当前“松手即提交”在用户频繁操作时可能产生提交排队；建议在提交前取消上一次未完成的提交任务，仅保留最后一次松手的提交（类似“last-write-wins”）。
  - 预览任务与提交任务建议分别管理，避免互相 cancel 造成 UI 抖动。

- **预览缓存进一步完善**
  - 目前缓存 key 基于 `identityHashCode(baseImage)`；若后续引入“滤镜叠加链”，可扩展为 `baseImageFingerprint + chainHash + paramsHash`（或至少在 sourceImageOverride 场景下保证 cacheKey 取对）。
  - 可增加简单命中率统计（默认关闭），便于性能回归。

- **枚举型参数的系统化支持**
  - 已对 `ColorFilter.style`、`NatureFilter.style` 做了下拉选择；建议把更多类似参数（如 `gridType`、`waveType` 等）统一纳入 `FilterParamMetaRegistry` 的 `enumOptions`。
  - 枚举项建议改为外部配置（json），降低 Kotlin 侧维护成本。

### P1（高收益 / 中等工程量）

- **非破坏式滤镜栈（专业编辑器体验）**
  - 当前“方式1”支持滤镜叠加，但本质是“破坏式”写回 `currentImage`；建议升级为滤镜栈（A→B→C）：
    - UI：支持新增/删除/排序/启用/禁用滤镜条目；
    - 计算：以基线图为输入重算整条链（可配合分段缓存）；
    - 历史：一次“应用/确认”生成一个历史节点，或者按滤镜条目粒度记录。
  - 优点：可编辑、可回溯、符合 PS/Lightroom 预期；缺点：需要明确栈的存储与性能策略。

- **参数元数据完全配置化**
  - 将 `FilterParamMeta`（min/max/step/decimals/enumOptions）迁移到 `resources/common/filterParamMeta.json`（或扩展现有 `filterConfig.json`），Kotlin 只保留类型默认兜底与少量安全约束（例如 step>0）。
  - 这样可以由产品/算法侧直接调整范围与枚举定义，不需要改代码。

- **提交与撤销语义更精确**
  - 当前每次松手都会 push 历史；可考虑“合并提交窗口”（例如 500ms 内多次提交合并为一次历史），减少 undo 栈污染。
  - 也可增加“预览模式”开关：只预览不入历史，用户确认后再统一落盘。

### P2（体验增强 / 可持续维护）

- **导出能力落地（与滤镜结果一致）**
  - Export 需要明确导出的是：当前画布效果（含叠加）还是仅某个滤镜结果。
  - 建议支持：导出当前效果 / 导出原图 / 导出带滤镜栈元数据（用于二次编辑）。

- **测试与回归保障**
  - 为关键交互补充自动化验证：列表筛选点击不乱、BlockFilter step 不为 0、枚举下拉可用、缓存命中不串图、清除滤镜恢复正确。

- **可访问性与键盘操作**
  - 下拉/按钮/缩放控件补齐 i18n 的 `contentDescription`；
  - 为参数控件支持键盘上下调整与快捷键（更像桌面编辑器）。


