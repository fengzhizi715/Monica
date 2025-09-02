# 🚀 快速参考卡片

## 常用命令

| 命令 | 说明 |
|------|------|
| `./cleanup_strings.sh file.xml` | 检查文件 |
| `./cleanup_strings.sh -a file.xml` | 自动修复 |
| `./cleanup_strings.sh -d file.xml` | 只检查，不修改 |
| `./cleanup_strings.sh -v file.xml` | 详细输出 |
| `./cleanup_strings.sh -h` | 显示帮助 |

## 安全使用流程

```bash
# 1. 检查当前状态
./cleanup_strings.sh strings_zh.xml

# 2. 预览将要删除的内容
./cleanup_strings.sh -a -d strings_zh.xml

# 3. 执行修复
./cleanup_strings.sh -a strings_zh.xml

# 4. 验证结果
./cleanup_strings.sh strings_zh.xml
```

## 批量处理

```bash
# 检查所有文件
./cleanup_strings.sh *.xml

# 修复所有文件
./cleanup_strings.sh -a *.xml
```

## 注意事项

- ✅ 自动创建备份文件
- ✅ 保留第一次出现的字符串
- ✅ 只修复名称重复的项
- ⚠️ 首次使用建议先运行 `-d` 选项
