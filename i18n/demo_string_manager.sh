#!/bin/bash

# 字符串资源管理工具演示脚本
# 展示 string_manager.sh 的各种用法

echo "=== 字符串资源管理工具演示 ==="
echo ""

echo "1. 查看帮助信息..."
echo "命令: ./string_manager.sh -h"
echo "---"
./string_manager.sh -h | head -20
echo "---"
echo ""

echo "2. 比对中英文文件..."
echo "命令: ./string_manager.sh -m"
echo "---"
./string_manager.sh -m
echo "---"
echo ""

echo "3. 检查单个文件的重复项..."
echo "命令: ./string_manager.sh -c src/main/resources/strings/strings_zh.xml"
echo "---"
./string_manager.sh -c src/main/resources/strings/strings_zh.xml
echo "---"
echo ""

echo "=== 常用命令总结 ==="
echo ""
echo "基本用法:"
echo "  ./string_manager.sh -m                    # 比对中英文文件"
echo "  ./string_manager.sh -c file.xml          # 检查单个文件"
echo "  ./string_manager.sh -a                    # 全功能模式"
echo ""
echo "高级用法:"
echo "  ./string_manager.sh -c -f file.xml       # 自动修复重复项"
echo "  ./string_manager.sh -c -v -d file.xml    # 详细检查，不修改"
echo "  ./string_manager.sh -c -n file.xml       # 不创建备份"
echo "  ./string_manager.sh -a -f                # 全功能 + 自动修复"
echo ""
echo "=== 演示完成 ==="
