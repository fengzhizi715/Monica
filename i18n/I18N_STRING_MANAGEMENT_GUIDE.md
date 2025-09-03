# 🌍 国际化字符串资源管理指南

## 📋 概述

本指南介绍 Monica 项目的国际化字符串资源管理工具集，帮助您高效管理和维护多语言字符串资源文件。

## 🛠️ 工具集

### 1. **string_manager.sh** - 综合管理工具
主要功能：清理重复项、检查缺失翻译、文件统计

### 2. **position_check.sh** - 位置比对工具  
主要功能：检查中英文文件中相同key的行号一致性

## 🎯 使用场景

| 场景 | 推荐工具 | 说明 |
|------|----------|------|
| 日常维护检查 | `string_manager.sh` | 快速检查重复项和缺失翻译 |
| 深度结构分析 | `position_check.sh` | 详细分析文件位置一致性 |
| 添加新字符串后 | 两个工具都使用 | 确保完整性和一致性 |
| 定期维护 | 两个工具都使用 | 全面检查资源文件状态 |

## 📖 详细使用指南

### 🔧 string_manager.sh 使用指南

#### 基本用法
```bash
cd i18n

# 查看帮助信息
./string_manager.sh -h

# 比对中英文文件（检查缺失翻译）
./string_manager.sh -m

# 清理重复项
./string_manager.sh -c src/main/resources/strings/strings_zh.xml

# 全功能模式
./string_manager.sh -a
```

#### 高级选项
```bash
# 详细输出
./string_manager.sh -v

# 只检查，不修改（模拟模式）
./string_manager.sh -d

# 自动修复重复项
./string_manager.sh -f

# 不创建备份
./string_manager.sh -n

# 组合使用
./string_manager.sh -c -v -d src/main/resources/strings/*.xml
```

#### 功能说明
- **清理模式** (`-c`): 检查并清理重复的字符串名称和内容
- **比对模式** (`-m`): 比对中英文文件，检查缺失的翻译
- **全功能模式** (`-a`): 同时执行清理和比对

### 📍 position_check.sh 使用指南

#### 基本用法
```bash
cd i18n

# 检查位置一致性
./position_check.sh

# 查看详细的不匹配信息
./position_check.sh -v
```

#### 输出说明
- **统计信息**: 文件行数、字符串数量、共同key数量
- **位置比对结果**: 不匹配的key数量、最大差异、平均差异
- **详细输出**: 每个不匹配key的具体行号和差异

## 🔄 工作流程

### 日常维护流程
```bash
# 1. 快速检查
./string_manager.sh -m

# 2. 检查重复项
./string_manager.sh -c src/main/resources/strings/*.xml

# 3. 位置一致性检查
./position_check.sh

# 4. 如有问题，查看详细信息
./position_check.sh -v
```

### 添加新字符串后
```bash
# 1. 检查新字符串是否添加正确
./string_manager.sh -m

# 2. 检查位置是否一致
./position_check.sh

# 3. 如有不一致，手动调整文件结构
```

### 定期维护（建议每周一次）
```bash
# 全面检查
./string_manager.sh -a
./position_check.sh -v
```

## 📊 输出解读

### string_manager.sh 输出
```
=== 文件统计报告 ===
文件: strings_zh.xml
总行数:      452
字符串总数: 348
唯一字符串名称:      348
重复字符串名称:       0

=== 中英文字符串资源文件比对 ===
中文文件字符串数量: 348
英文文件字符串数量: 348
✅ 没有缺失的英文翻译
✅ 没有缺失的中文翻译
```

### position_check.sh 输出
```
=== 中英文字符串资源文件位置比对 ===
中文文件字符串数量: 348
英文文件字符串数量: 348
共同key数量: 348

⚠️  发现 176 个key的行号不一致
  最大行号差异: 181 行
  平均行号差异: 18.0 行
```

## ⚠️ 注意事项

### 文件结构
- 确保两个文件都有相同的字符串key
- 建议保持相同功能的字符串在相同位置
- 定期检查文件结构一致性

### 备份策略
- 自动修复前会自动创建备份
- 备份文件格式：`filename.backup.YYYYMMDD_HHMMSS`
- 建议定期清理旧的备份文件

### 性能考虑
- 大文件处理可能需要一些时间
- 建议在文件较小时进行维护
- 避免在文件被其他程序使用时修改

## 🎯 最佳实践

### 1. 文件组织
- 按功能模块组织字符串
- 保持中英文文件结构一致
- 使用有意义的key名称

### 2. 维护频率
- **日常**: 添加新字符串后立即检查
- **每周**: 运行完整的位置比对
- **每月**: 全面清理和优化

### 3. 团队协作
- 建立字符串添加规范
- 使用版本控制跟踪变更
- 定期团队代码审查

## 🔧 故障排除

### 常见问题

#### 1. 脚本权限问题
```bash
chmod +x string_manager.sh position_check.sh
```

#### 2. 文件路径问题
```bash
# 确保在正确的目录
cd i18n
ls src/main/resources/strings/
```

#### 3. XML格式问题
```bash
# 检查XML格式
xmllint --noout src/main/resources/strings/strings_zh.xml
xmllint --noout src/main/resources/strings/strings_en.xml
```

### 错误信息解读
- **"文件不存在"**: 检查文件路径和权限
- **"XML格式错误"**: 检查XML语法
- **"权限被拒绝"**: 检查文件读写权限

## 📞 获取帮助

```bash
# 查看工具帮助
./string_manager.sh -h
./position_check.sh -h

# 查看文件状态
ls -la *.sh
wc -l src/main/resources/strings/*.xml
```

## 🎉 总结

这套工具集提供了完整的国际化字符串资源管理解决方案：

- **string_manager.sh**: 日常维护和清理
- **position_check.sh**: 深度分析和结构检查

通过合理使用这两个工具，您可以：
- 保持字符串资源文件的整洁
- 确保翻译的完整性
- 维护文件结构的一致性
- 提高国际化维护效率

---

**版本**: 1.0  
**最后更新**: 2025-09-03  
**维护者**: AI Assistant
