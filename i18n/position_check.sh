#!/bin/bash

# 位置比对脚本
# 检查中英文配置文件中相同key的行号是否一致

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 文件路径
ZH_FILE="src/main/resources/strings/strings_zh.xml"
EN_FILE="src/main/resources/strings/strings_en.xml"

echo "=== 中英文字符串资源文件位置比对 ==="
echo ""

# 检查文件是否存在
if [[ ! -f "$ZH_FILE" ]]; then
    echo -e "${RED}错误: 中文文件不存在: $ZH_FILE${NC}"
    exit 1
fi

if [[ ! -f "$EN_FILE" ]]; then
    echo -e "${RED}错误: 英文文件不存在: $EN_FILE${NC}"
    exit 1
fi

# 创建临时文件存储key和行号的映射
ZH_TEMP=$(mktemp)
EN_TEMP=$(mktemp)

echo "1. 提取中文文件中的key和行号..."
grep -n 'name="[^"]*"' "$ZH_FILE" | sed 's/^\([0-9]*\):.*name="\([^"]*\)".*/\1:\2/' > "$ZH_TEMP"

echo "2. 提取英文文件中的key和行号..."
grep -n 'name="[^"]*"' "$EN_FILE" | sed 's/^\([0-9]*\):.*name="\([^"]*\)".*/\1:\2/' > "$EN_TEMP"

echo "3. 统计信息..."
ZH_COUNT=$(wc -l < "$ZH_TEMP")
EN_COUNT=$(wc -l < "$EN_TEMP")
echo "中文文件字符串数量: $ZH_COUNT"
echo "英文文件字符串数量: $EN_COUNT"
echo ""

# 找出共同的key
COMMON_KEYS=$(comm -12 <(cut -d: -f2 "$ZH_TEMP" | sort) <(cut -d: -f2 "$EN_TEMP" | sort))
COMMON_COUNT=$(echo "$COMMON_KEYS" | wc -l)
echo "4. 共同key数量: $COMMON_COUNT"
echo ""

# 检查行号差异
echo "5. 检查行号一致性..."
MISMATCHED_COUNT=0
TOTAL_DIFF=0
MAX_DIFF=0

echo "$COMMON_KEYS" | while read key; do
    ZH_LINE=$(grep ":$key$" "$ZH_TEMP" | cut -d: -f1)
    EN_LINE=$(grep ":$key$" "$EN_TEMP" | cut -d: -f1)
    
    if [[ -n "$ZH_LINE" && -n "$EN_LINE" ]]; then
        DIFF=$((ZH_LINE - EN_LINE))
        # 取绝对值
        if [[ $DIFF -lt 0 ]]; then
            ABS_DIFF=$((-DIFF))
        else
            ABS_DIFF=$DIFF
        fi
        
        if [[ $ABS_DIFF -gt 0 ]]; then
            echo -e "${YELLOW}行号不匹配: $key${NC}"
            echo "  中文文件第${ZH_LINE}行"
            echo "  英文文件第${EN_LINE}行"
            echo "  差异: $DIFF 行"
            echo ""
        fi
    fi
done

# 统计不匹配的数量
ACTUAL_MISMATCHED=$(echo "$COMMON_KEYS" | while read key; do
    ZH_LINE=$(grep ":$key$" "$ZH_TEMP" | cut -d: -f1)
    EN_LINE=$(grep ":$key$" "$EN_TEMP" | cut -d: -f1)
    
    if [[ -n "$ZH_LINE" && -n "$EN_LINE" ]]; then
        DIFF=$((ZH_LINE - EN_LINE))
        # 取绝对值
        if [[ $DIFF -lt 0 ]]; then
            ABS_DIFF=$((-DIFF))
        else
            ABS_DIFF=$DIFF
        fi
        
        if [[ $ABS_DIFF -gt 0 ]]; then
            echo "1"
        fi
    fi
done | wc -l)

# 计算总差异
ACTUAL_TOTAL_DIFF=$(echo "$COMMON_KEYS" | while read key; do
    ZH_LINE=$(grep ":$key$" "$ZH_TEMP" | cut -d: -f1)
    EN_LINE=$(grep ":$key$" "$EN_TEMP" | cut -d: -f1)
    
    if [[ -n "$ZH_LINE" && -n "$EN_LINE" ]]; then
        DIFF=$((ZH_LINE - EN_LINE))
        # 取绝对值
        if [[ $DIFF -lt 0 ]]; then
            ABS_DIFF=$((-DIFF))
        else
            ABS_DIFF=$DIFF
        fi
        echo "$ABS_DIFF"
    fi
done | awk '{sum+=$1} END {print sum}')

# 计算最大差异
ACTUAL_MAX_DIFF=$(echo "$COMMON_KEYS" | while read key; do
    ZH_LINE=$(grep ":$key$" "$ZH_TEMP" | cut -d: -f1)
    EN_LINE=$(grep ":$key$" "$EN_TEMP" | cut -d: -f1)
    
    if [[ -n "$ZH_LINE" && -n "$EN_LINE" ]]; then
        DIFF=$((ZH_LINE - EN_LINE))
        # 取绝对值
        if [[ $DIFF -lt 0 ]]; then
            ABS_DIFF=$((-DIFF))
        else
            ABS_DIFF=$DIFF
        fi
        echo "$ABS_DIFF"
    fi
done | sort -n | tail -1)

echo "6. 位置比对结果:"
if [[ $ACTUAL_MISMATCHED -eq 0 ]]; then
    echo -e "${GREEN}✅ 所有共同key的行号都一致！${NC}"
else
    echo -e "${YELLOW}⚠️  发现 $ACTUAL_MISMATCHED 个key的行号不一致${NC}"
    echo "  最大行号差异: $ACTUAL_MAX_DIFF 行"
    if [[ $ACTUAL_MISMATCHED -gt 0 ]]; then
        AVERAGE_DIFF=$(echo "scale=1; $ACTUAL_TOTAL_DIFF / $ACTUAL_MISMATCHED" | bc 2>/dev/null || echo "N/A")
        echo "  平均行号差异: $AVERAGE_DIFF 行"
    fi
fi

# 清理临时文件
rm "$ZH_TEMP" "$EN_TEMP"

echo ""
echo "=== 位置比对完成 ==="