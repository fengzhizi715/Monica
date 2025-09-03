#!/bin/bash

# 快速使用示例脚本
# 用于演示 cleanup_strings.sh 的各种用法

echo "=== 字符串资源文件清理工具使用示例 ==="
echo ""

# 设置文件路径
ZH_FILE="src/main/resources/strings/strings_zh.xml"
EN_FILE="src/main/resources/strings/strings_en.xml"

echo "1. 检查中文文件..."
./cleanup_strings.sh "$ZH_FILE"
echo ""

echo "2. 检查英文文件..."
./cleanup_strings.sh "$EN_FILE"
echo ""

echo "3. 详细检查中文文件..."
./cleanup_strings.sh -v "$ZH_FILE"
echo ""

echo "4. 模拟修复英文文件（推荐首次使用）..."
./cleanup_strings.sh -a -d "$EN_FILE"
echo ""

echo "5. 批量检查所有文件..."
./cleanup_strings.sh "$ZH_FILE" "$EN_FILE"
echo ""

echo "=== 其他常用命令 ==="
echo ""
echo "# 自动修复所有文件（会创建备份）"
echo "./cleanup_strings.sh -a $ZH_FILE $EN_FILE"
echo ""
echo "# 详细检查，不修改文件"
echo "./cleanup_strings.sh -v -d $ZH_FILE $EN_FILE"
echo ""
echo "# 自动修复，不创建备份"
echo "./cleanup_strings.sh -a -n $ZH_FILE $EN_FILE"
echo ""
echo "# 查看帮助信息"
echo "./cleanup_strings.sh -h"
echo ""
echo "=== 注意事项 ==="
echo "1. 首次使用建议先运行 -d 选项查看将要删除的内容"
echo "2. 自动修复前会自动创建备份文件"
echo "3. 脚本只修复名称重复的项，内容重复的项需要手动处理"
echo "4. 建议在版本控制系统中提交前使用此脚本清理"
echo "5. 脚本位置：i18n/cleanup_strings.sh"
echo "6. 使用前需要先进入 i18n 目录：cd i18n"
