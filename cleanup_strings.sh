#!/bin/bash

# 通用字符串资源文件清理脚本
# 用于检查和清理 XML 字符串资源文件中的重复项
# 作者: AI Assistant
# 版本: 1.0

# 移除 set -e，避免函数返回值导致脚本退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 默认参数
VERBOSE=false
DRY_RUN=false
BACKUP=true
AUTO_FIX=false

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项] <文件路径>"
    echo ""
    echo "选项:"
    echo "  -h, --help         显示此帮助信息"
    echo "  -v, --verbose      详细输出"
    echo "  -d, --dry-run      只检查，不修改文件"
    echo "  -n, --no-backup    不创建备份文件"
    echo "  -a, --auto-fix     自动修复重复项（保留第一次出现的版本）"
    echo ""
    echo "示例:"
    echo "  $0 strings_zh.xml                    # 检查文件"
    echo "  $0 -a strings_zh.xml                 # 自动修复重复项"
    echo "  $0 -v -d strings_zh.xml              # 详细检查，不修改"
    echo "  $0 -a -n strings_zh.xml strings_en.xml  # 自动修复多个文件，不备份"
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -v|--verbose)
                VERBOSE=true
                shift
                ;;
            -d|--dry-run)
                DRY_RUN=true
                shift
                ;;
            -n|--no-backup)
                BACKUP=false
                shift
                ;;
            -a|--auto-fix)
                AUTO_FIX=true
                shift
                ;;
            -*)
                echo -e "${RED}错误: 未知选项 $1${NC}"
                show_help
                exit 1
                ;;
            *)
                FILES+=("$1")
                shift
                ;;
        esac
    done
}

# 检查文件是否存在
check_file() {
    local file="$1"
    if [[ ! -f "$file" ]]; then
        echo -e "${RED}错误: 文件不存在: $file${NC}"
        return 1
    fi
    
    if [[ ! -r "$file" ]]; then
        echo -e "${RED}错误: 文件不可读: $file${NC}"
        return 1
    fi
    
    return 0
}

# 验证XML格式
validate_xml() {
    local file="$1"
    if ! xmllint --noout "$file" 2>/dev/null; then
        echo -e "${RED}警告: $file 不是有效的XML文件${NC}"
        return 1
    fi
    return 0
}

# 创建备份
create_backup() {
    local file="$1"
    local backup_file="${file}.backup.$(date +%Y%m%d_%H%M%S)"
    
    if cp "$file" "$backup_file"; then
        echo -e "${GREEN}已创建备份: $backup_file${NC}"
    else
        echo -e "${RED}错误: 无法创建备份文件${NC}"
        return 1
    fi
}

# 分析重复项
analyze_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    
    # 提取所有字符串名称和内容
    grep -o 'name="[^"]*"' "$file" | sed 's/name="//;s/"//' | sort | uniq -d > "$temp_file"
    
    if [[ ! -s "$temp_file" ]]; then
        echo -e "${GREEN}✓ 没有发现重复的字符串名称${NC}"
        rm -f "$temp_file"
        return 0
    fi
    
    local duplicate_count=$(wc -l < "$temp_file")
    echo -e "${YELLOW}发现 $duplicate_count 个重复的字符串名称:${NC}"
    
    if [[ "$VERBOSE" == "true" ]]; then
        echo ""
        while IFS= read -r name; do
            echo -e "${BLUE}重复项: $name${NC}"
            grep -n "name=\"$name\"" "$file" | while read -r line; do
                echo "  $line"
            done
            echo ""
        done < "$temp_file"
    else
        cat "$temp_file" | head -10
        if [[ $duplicate_count -gt 10 ]]; then
            echo "... 还有 $((duplicate_count - 10)) 个重复项"
        fi
    fi
    
    rm -f "$temp_file"
    return $duplicate_count
}

# 检查内容重复
check_content_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    
    # 提取所有字符串内容并检查重复
    grep -o '>.*</string>' "$file" | sed 's/>//;s/<\/string>//' | sort | uniq -d > "$temp_file"
    
    if [[ ! -s "$temp_file" ]]; then
        echo -e "${GREEN}✓ 没有发现重复的字符串内容${NC}"
        rm -f "$temp_file"
        return 0
    fi
    
    local content_duplicate_count=$(wc -l < "$temp_file")
    echo -e "${YELLOW}发现 $content_duplicate_count 个重复的字符串内容:${NC}"
    
    if [[ "$VERBOSE" == "true" ]]; then
        echo ""
        while IFS= read -r content; do
            echo -e "${BLUE}重复内容: $content${NC}"
            grep -n ">$content</string>" "$file" | while read -r line; do
                echo "  $line"
            done
            echo ""
        done < "$temp_file"
    else
        cat "$temp_file" | head -5
        if [[ $content_duplicate_count -gt 5 ]]; then
            echo "... 还有 $((content_duplicate_count - 5)) 个重复内容"
        fi
    fi
    
    rm -f "$temp_file"
    return $content_duplicate_count
}

# 自动修复重复项
auto_fix_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    local fixed_count=0
    
    # 获取重复的字符串名称
    grep -o 'name="[^"]*"' "$file" | sed 's/name="//;s/"//' | sort | uniq -d > "$temp_file"
    
    if [[ ! -s "$temp_file" ]]; then
        echo -e "${GREEN}没有需要修复的重复项${NC}"
        rm -f "$temp_file"
        return 0
    fi
    
    echo -e "${BLUE}开始自动修复重复项...${NC}"
    
    # 按行号倒序删除重复项（保留第一次出现的版本）
    while IFS= read -r name; do
        local lines=($(grep -n "name=\"$name\"" "$file" | cut -d: -f1))
        
        # 删除除第一个之外的所有重复项
        for ((i=${#lines[@]}-1; i>=1; i--)); do
            local line_num=${lines[$i]}
            if [[ "$DRY_RUN" == "false" ]]; then
                sed -i '' "${line_num}d" "$file"
                echo -e "${GREEN}已删除重复项: $name (第${line_num}行)${NC}"
            else
                echo -e "${YELLOW}将删除重复项: $name (第${line_num}行)${NC}"
            fi
            ((fixed_count++))
        done
    done < "$temp_file"
    
    rm -f "$temp_file"
    
    if [[ "$DRY_RUN" == "false" ]]; then
        echo -e "${GREEN}修复完成！共删除 $fixed_count 个重复项${NC}"
    else
        echo -e "${YELLOW}模拟完成！将删除 $fixed_count 个重复项${NC}"
    fi
    
    return $fixed_count
}

# 生成统计报告
generate_report() {
    local file="$1"
    local total_lines=$(wc -l < "$file")
    local total_strings=$(grep -c '<string name=' "$file" || echo 0)
    local unique_names=$(grep -o 'name="[^"]*"' "$file" | sort | uniq | wc -l)
    local duplicate_names=$(grep -o 'name="[^"]*"' "$file" | sort | uniq -d | wc -l)
    
    echo -e "${BLUE}=== 文件统计报告 ===${NC}"
    echo "文件: $file"
    echo "总行数: $total_lines"
    echo "字符串总数: $total_strings"
    echo "唯一字符串名称: $unique_names"
    echo "重复字符串名称: $duplicate_names"
    echo ""
}

# 主函数
main() {
    local FILES=()
    
    parse_args "$@"
    
    if [[ ${#FILES[@]} -eq 0 ]]; then
        echo -e "${RED}错误: 请指定要处理的文件${NC}"
        show_help
        exit 1
    fi
    
    echo -e "${BLUE}字符串资源文件清理工具 v1.0${NC}"
    echo ""
    
    for file in "${FILES[@]}"; do
        echo -e "${BLUE}处理文件: $file${NC}"
        echo "=================================="
        
        # 检查文件
        if ! check_file "$file"; then
            continue
        fi
        
        # 验证XML格式
        if ! validate_xml "$file"; then
            echo -e "${YELLOW}跳过XML格式验证...${NC}"
        fi
        
        # 生成统计报告
        generate_report "$file"
        
        # 分析重复项
        local name_duplicates=0
        analyze_duplicates "$file"
        name_duplicates=$?
        
        # 检查内容重复
        local content_duplicates=0
        check_content_duplicates "$file" || true
        content_duplicates=$?
        
        # 自动修复
        if [[ "$AUTO_FIX" == "true" && $name_duplicates -gt 0 ]]; then
            echo ""
            if [[ "$BACKUP" == "true" && "$DRY_RUN" == "false" ]]; then
                create_backup "$file"
            fi
            auto_fix_duplicates "$file"
        fi
        
        echo ""
    done
    
    echo -e "${GREEN}处理完成！${NC}"
}

# 运行主函数
main "$@"
