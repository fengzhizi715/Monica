# i18n 脚本快速参考

## 🚀 快速开始

### 基本检查
```bash
# 检查中英文文件是否同步
./string_manager.sh -m

# 检查重复项（不修改文件）
./string_manager.sh -c -d

# 检查行号一致性
./position_check.sh
```

### 修复重复项
```bash
# 自动修复重复项
./string_manager.sh -c -f
```

## 📋 常用命令

| 命令 | 功能 |
|------|------|
| `./string_manager.sh --help` | 显示帮助信息 |
| `./string_manager.sh -m` | 比对中英文文件差异 |
| `./string_manager.sh -c -d` | 检查重复项（不修改） |
| `./string_manager.sh -c -f` | 自动修复重复项 |
| `./string_manager.sh -c -v -d` | 详细检查重复项 |
| `./string_manager.sh -a -f` | 全功能模式 |
| `./position_check.sh` | 检查行号一致性 |

## ⚡ 一键检查脚本

创建一个检查脚本 `quick_check.sh`：

```bash
#!/bin/bash
echo "=== i18n 文件快速检查 ==="
echo ""

echo "1. 检查中英文文件同步性..."
./string_manager.sh -m
echo ""

echo "2. 检查重复项..."
./string_manager.sh -c -d
echo ""

echo "3. 检查行号一致性..."
./position_check.sh
echo ""

echo "=== 检查完成 ==="
```

使用方法：
```bash
chmod +x quick_check.sh
./quick_check.sh
```

## 🔧 故障排除

### 权限问题
```bash
chmod +x string_manager.sh position_check.sh
```

### 文件不存在
```bash
ls -la src/main/resources/strings/
```

### 语法检查
```bash
bash -n string_manager.sh
bash -n position_check.sh
```

## 📊 输出解读

### ✅ 正常状态
- 中英文文件字符串数量相同
- 无缺失翻译
- 无重复字符串名称

### ⚠️ 需要注意
- 有重复字符串内容（正常，不同key可以有相同内容）
- 行号不一致（正常，文件结构可能不同）

### ❌ 需要修复
- 有重复字符串名称
- 有缺失的翻译
- 文件不同步
