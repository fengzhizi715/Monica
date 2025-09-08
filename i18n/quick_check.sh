#!/bin/bash

# i18n 文件快速检查脚本
# 功能：一键检查中英文文件同步性、重复项和行号一致性

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== i18n 文件快速检查 ===${NC}"
echo ""

# 检查脚本是否存在
if [[ ! -f "string_manager.sh" ]]; then
    echo -e "${RED}错误: string_manager.sh 不存在${NC}"
    exit 1
fi

if [[ ! -f "position_check.sh" ]]; then
    echo -e "${RED}错误: position_check.sh 不存在${NC}"
    exit 1
fi

# 检查脚本权限
if [[ ! -x "string_manager.sh" ]]; then
    echo -e "${YELLOW}警告: string_manager.sh 没有执行权限，正在修复...${NC}"
    chmod +x string_manager.sh
fi

if [[ ! -x "position_check.sh" ]]; then
    echo -e "${YELLOW}警告: position_check.sh 没有执行权限，正在修复...${NC}"
    chmod +x position_check.sh
fi

echo -e "${BLUE}1. 检查中英文文件同步性...${NC}"
echo "----------------------------------------"
./string_manager.sh -m
echo ""

echo -e "${BLUE}2. 检查重复项...${NC}"
echo "----------------------------------------"
./string_manager.sh -c -d
echo ""

echo -e "${BLUE}3. 检查行号一致性...${NC}"
echo "----------------------------------------"
./position_check.sh
echo ""

echo -e "${GREEN}=== 检查完成 ===${NC}"
echo ""
echo -e "${YELLOW}💡 提示:${NC}"
echo "  - 如果发现重复项，使用: ./string_manager.sh -c -f"
echo "  - 如果发现缺失翻译，请手动添加"
echo "  - 行号不一致是正常的，不影响功能"
echo ""
echo -e "${BLUE}📚 更多信息请查看: SCRIPT_USAGE_GUIDE.md${NC}"
