# 字符串资源文件清理工具使用说明

## 概述
`cleanup_strings.sh` 是一个通用的字符串资源文件清理脚本，专门用于检查和清理 XML 字符串资源文件中的重复项。

## 功能特性
- ✅ 检查重复的字符串名称
- ✅ 检查重复的字符串内容
- ✅ 自动修复重复项（保留第一次出现的版本）
- ✅ 生成详细的统计报告
- ✅ 支持批量处理多个文件
- ✅ 自动创建备份文件
- ✅ 支持模拟运行（不实际修改文件）
- ✅ 彩色输出，易于阅读

## 使用方法

### 基本用法
```bash
# 检查单个文件
./cleanup_strings.sh strings_zh.xml

# 检查多个文件
./cleanup_strings.sh strings_zh.xml strings_en.xml

# 自动修复重复项
./cleanup_strings.sh -a strings_zh.xml

# 详细检查，不修改文件
./cleanup_strings.sh -v -d strings_zh.xml
```

### 命令行选项
- `-h, --help`         显示帮助信息
- `-v, --verbose`      详细输出（显示所有重复项的具体位置）
- `-d, --dry-run`      只检查，不修改文件
- `-n, --no-backup`    不创建备份文件
- `-a, --auto-fix`     自动修复重复项（保留第一次出现的版本）

### 使用示例

#### 1. 简单检查
```bash
./cleanup_strings.sh i18n/src/main/resources/strings/strings_zh.xml
```
输出示例：
```
字符串资源文件清理工具 v1.0

处理文件: i18n/src/main/resources/strings/strings_zh.xml
==================================
=== 文件统计报告 ===
文件: i18n/src/main/resources/strings/strings_zh.xml
总行数: 450
字符串总数: 342
唯一字符串名称: 341
重复字符串名称: 1

✓ 没有发现重复的字符串名称
✓ 没有发现重复的字符串内容
```

#### 2. 详细检查
```bash
./cleanup_strings.sh -v i18n/src/main/resources/strings/strings_zh.xml
```
会显示所有重复项的具体行号和内容。

#### 3. 自动修复
```bash
./cleanup_strings.sh -a i18n/src/main/resources/strings/strings_zh.xml
```
会自动删除重复项，保留第一次出现的版本，并创建备份文件。

#### 4. 模拟修复（推荐首次使用）
```bash
./cleanup_strings.sh -a -d i18n/src/main/resources/strings/strings_zh.xml
```
会显示将要删除的重复项，但不实际修改文件。

#### 5. 批量处理
```bash
./cleanup_strings.sh -a i18n/src/main/resources/strings/*.xml
```

## 输出说明

### 颜色含义
- 🔵 蓝色：信息性消息
- 🟢 绿色：成功消息
- 🟡 黄色：警告消息
- 🔴 红色：错误消息

### 统计报告
- **总行数**：文件的总行数
- **字符串总数**：`<string name=` 标签的总数
- **唯一字符串名称**：不重复的字符串名称数量
- **重复字符串名称**：重复的字符串名称数量

## 安全特性

### 自动备份
默认情况下，脚本会在修改文件前自动创建备份：
```
strings_zh.xml.backup.20241201_143022
```

### 模拟运行
使用 `-d` 选项可以模拟运行，查看将要进行的修改而不实际修改文件。

### XML 验证
脚本会验证文件是否为有效的 XML 格式。

## 注意事项

1. **备份文件**：建议在自动修复前先运行 `-d` 选项查看将要删除的内容
2. **内容重复**：脚本会区分名称重复和内容重复，只有名称重复才会被自动修复
3. **保留策略**：自动修复时保留第一次出现的版本，删除后续重复项
4. **文件格式**：脚本专门处理标准的 XML 字符串资源文件格式

## 故障排除

### 常见问题

1. **权限错误**
   ```bash
   chmod +x cleanup_strings.sh
   ```

2. **文件不存在**
   ```bash
   ls -la i18n/src/main/resources/strings/
   ```

3. **XML 格式错误**
   ```bash
   xmllint --noout strings_zh.xml
   ```

## 版本历史
- v1.0: 初始版本，支持基本的重复项检查和修复功能
