# 🚀 字符串资源管理工具快速参考

## 📋 工具概述

`string_manager.sh` 是一个综合的字符串资源管理工具，合并了清理和比对功能，提供一站式的字符串资源管理解决方案。

## 🎯 主要功能

### 1. 清理模式 (`-c`)
- 检查重复的字符串名称
- 检查重复的字符串内容
- 自动修复重复项
- 生成详细的统计报告

### 2. 比对模式 (`-m`)
- 比对中英文资源文件
- 检测缺失的翻译
- 显示详细的差异信息

### 3. 全功能模式 (`-a`)
- 同时执行清理和比对
- 一键完成所有检查

## 📖 基本用法

### 比对中英文文件
```bash
cd i18n
./string_manager.sh -m
```

### 检查单个文件
```bash
cd i18n
./string_manager.sh -c src/main/resources/strings/strings_zh.xml
```

### 全功能模式
```bash
cd i18n
./string_manager.sh -a
```

## 🔧 高级选项

### 清理模式选项
| 选项 | 说明 | 示例 |
|------|------|------|
| `-v, --verbose` | 详细输出 | `-c -v file.xml` |
| `-d, --dry-run` | 只检查，不修改 | `-c -d file.xml` |
| `-n, --no-backup` | 不创建备份 | `-c -n file.xml` |
| `-f, --auto-fix` | 自动修复重复项 | `-c -f file.xml` |

### 组合使用
```bash
# 详细检查，不修改
./string_manager.sh -c -v -d src/main/resources/strings/strings_zh.xml

# 自动修复，不备份
./string_manager.sh -c -f -n src/main/resources/strings/strings_zh.xml

# 全功能 + 自动修复
./string_manager.sh -a -f
```

## 📊 输出说明

### 颜色含义
- 🔵 **蓝色**: 信息性消息
- 🟢 **绿色**: 成功消息
- 🟡 **黄色**: 警告消息
- 🔴 **红色**: 错误消息

### 统计报告
- **总行数**: 文件的总行数
- **字符串总数**: `<string name=` 标签的总数
- **唯一字符串名称**: 不重复的字符串名称数量
- **重复字符串名称**: 重复的字符串名称数量

## 🎯 工作流程

### 日常维护
```bash
# 1. 比对中英文文件
./string_manager.sh -m

# 2. 检查重复项
./string_manager.sh -c src/main/resources/strings/*.xml

# 3. 自动修复（如果需要）
./string_manager.sh -c -f src/main/resources/strings/*.xml
```

### 一键检查
```bash
# 全功能模式，自动修复
./string_manager.sh -a -f
```

## ⚠️ 注意事项

1. **备份**: 自动修复前会创建备份文件
2. **安全**: 使用 `-d` 选项先预览将要进行的修改
3. **文件**: 比对模式会自动使用默认的中英文文件路径
4. **权限**: 确保脚本有执行权限

## 📞 获取帮助

```bash
./string_manager.sh -h
```

## 🎉 优势

- **功能整合**: 一个工具完成所有字符串管理任务
- **自动化**: 支持自动修复和批量处理
- **安全性**: 自动备份和预览功能
- **易用性**: 清晰的命令行界面和帮助信息
- **高效性**: 快速检测和处理大量字符串资源

---

**版本**: 2.0  
**作者**: AI Assistant  
**位置**: `i18n/string_manager.sh`
