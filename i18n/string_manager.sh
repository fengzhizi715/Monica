#!/bin/bash

# 字符串资源文件综合管理工具
# 功能：检查重复项、清理重复项、比对中英文文件
# 作者: AI Assistant
# 版本: 2.0

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
COMPARE_MODE=false
CLEANUP_MODE=false
POSITION_MODE=false

# 显示帮助信息
show_help() {
    echo "字符串资源文件综合管理工具 v2.0"
    echo ""
    echo "用法: $0 [选项] <文件路径>"
    echo ""
    echo "模式选项:"
    echo "  -c, --cleanup      清理模式：检查并清理重复项"
    echo "  -m, --compare      比对模式：比对中英文文件差异"
    echo "  -p, --position     位置比对模式：检查相同key的行号是否一致"
    echo "  -a, --all          全功能模式：清理 + 比对"
    echo ""
    echo "清理模式选项:"
    echo "  -v, --verbose      详细输出"
    echo "  -d, --dry-run      只检查，不修改文件"
    echo "  -n, --no-backup    不创建备份文件"
    echo "  -f, --auto-fix     自动修复重复项（保留第一次出现的版本）"
    echo ""
    echo "示例:"
    echo "  $0 -c src/main/resources/strings/strings_zh.xml                    # 清理单个文件"
    echo "  $0 -c -f src/main/resources/strings/strings_zh.xml                 # 自动修复重复项"
    echo "  $0 -m                                                              # 比对中英文文件"
    echo "  $0 -p                                                              # 检查行号一致性"
    echo "  $0 -a -f                                                            # 全功能模式，自动修复"
    echo "  $0 -c -v -d src/main/resources/strings/*.xml                       # 详细检查，不修改"
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -c|--cleanup)
                CLEANUP_MODE=true
                shift
                ;;
            -m|--compare)
                COMPARE_MODE=true
                shift
                ;;
            -a|--all)
                CLEANUP_MODE=true
                COMPARE_MODE=true
                shift
                ;;
            -p|--position)
                POSITION_MODE=true
                shift
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
            -f|--auto-fix)
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

# 创建备份文件
create_backup() {
    local file="$1"
    if [[ "$BACKUP" == true && "$DRY_RUN" == false ]]; then
        local timestamp=$(date +"%Y%m%d_%H%M%S")
        local backup_file="${file}.backup.${timestamp}"
        cp "$file" "$backup_file"
        echo -e "${GREEN}已创建备份: $backup_file${NC}"
    fi
}

# 分析重复的字符串名称
analyze_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    
    # 提取所有字符串名称
    grep -o 'name="[^"]*"' "$file" | sed 's/name="//g' | sed 's/"//g' | sort > "$temp_file"
    
    # 查找重复项
    local duplicates=$(sort "$temp_file" | uniq -d)
    
    if [[ -z "$duplicates" ]]; then
        echo -e "${GREEN}✓ 没有发现重复的字符串名称${NC}"
        rm "$temp_file"
        return 0
    else
        echo -e "${YELLOW}发现 $(echo "$duplicates" | wc -l | tr -d ' ') 个重复的字符串名称:${NC}"
        echo "$duplicates" | head -10
        if [[ $(echo "$duplicates" | wc -l) -gt 10 ]]; then
            echo "... 还有 $(($(echo "$duplicates" | wc -l) - 10)) 个重复项"
        fi
        
        if [[ "$VERBOSE" == true ]]; then
            echo ""
            echo "重复项详情:"
            echo "$duplicates" | while read name; do
                echo ""
                echo "重复项: $name"
                grep -n "name=\"$name\"" "$file" | while read line; do
                    echo "  $line"
                done
            done
        fi
        
        rm "$temp_file"
        return 1
    fi
}

# 检查重复的字符串内容
check_content_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    
    # 提取所有字符串内容
    grep -o '>.*<' "$file" | sed 's/^>//' | sed 's/<$//' | sort > "$temp_file"
    
    # 查找重复内容
    local duplicates=$(sort "$temp_file" | uniq -d)
    
    if [[ -z "$duplicates" ]]; then
        echo -e "${GREEN}✓ 没有发现重复的字符串内容${NC}"
        rm "$temp_file"
        return 0
    else
        echo -e "${YELLOW}发现 $(echo "$duplicates" | wc -l | tr -d ' ') 个重复的字符串内容:${NC}"
        echo "$duplicates" | head -10
        if [[ $(echo "$duplicates" | wc -l) -gt 10 ]]; then
            echo "... 还有 $(($(echo "$duplicates" | wc -l) - 10)) 个重复内容"
        fi
        
        if [[ "$VERBOSE" == true ]]; then
            echo ""
            echo "重复内容详情:"
            echo "$duplicates" | while read content; do
                echo ""
                echo "重复内容: $content"
                grep -n ">$content<" "$file" | while read line; do
                    echo "  $line"
                done
            done
        fi
        
        rm "$temp_file"
        return 0
    fi
}

# 自动修复重复项
auto_fix_duplicates() {
    local file="$1"
    local temp_file=$(mktemp)
    
    # 获取重复的字符串名称
    grep -o 'name="[^"]*"' "$file" | sed 's/name="//g' | sed 's/"//g' | sort | uniq -d > "$temp_file"
    
    if [[ ! -s "$temp_file" ]]; then
        echo -e "${GREEN}没有重复项需要修复${NC}"
        rm "$temp_file"
        return 0
    fi
    
    echo -e "${BLUE}开始自动修复重复项...${NC}"
    
    # 为每个重复项找到所有行号并删除除第一个之外的所有行
    while read duplicate_name; do
        local line_numbers=$(grep -n "name=\"$duplicate_name\"" "$file" | cut -d: -f1 | tail -n +2)
        
        if [[ -n "$line_numbers" ]]; then
            echo "$line_numbers" | while read line_num; do
                if [[ "$DRY_RUN" == true ]]; then
                    echo -e "${YELLOW}将删除重复项: $duplicate_name (第${line_num}行)${NC}"
                else
                    sed -i '' "${line_num}d" "$file"
                    echo -e "${GREEN}已删除重复项: $duplicate_name (第${line_num}行)${NC}"
                fi
            done
        fi
    done < "$temp_file"
    
    if [[ "$DRY_RUN" == true ]]; then
        echo -e "${YELLOW}模拟完成！将删除 $(wc -l < "$temp_file") 个重复项${NC}"
    else
        echo -e "${GREEN}修复完成！共删除 $(wc -l < "$temp_file") 个重复项${NC}"
    fi
    
    rm "$temp_file"
}

# 生成文件统计报告
generate_report() {
    local file="$1"
    local total_lines=$(wc -l < "$file")
    local total_strings=$(grep -c 'name="[^"]*"' "$file")
    local unique_names=$(grep -o 'name="[^"]*"' "$file" | sed 's/name="//g' | sed 's/"//g' | sort | uniq | wc -l)
    local duplicate_names=$(($total_strings - $unique_names))
    
    echo "=== 文件统计报告 ==="
    echo "文件: $file"
    echo "总行数:      $total_lines"
    echo "字符串总数: $total_strings"
    echo "唯一字符串名称:      $unique_names"
    echo "重复字符串名称:        $duplicate_names"
    echo ""
}

# 清理模式主函数
cleanup_mode() {
    local has_errors=0
    
    # 如果没有指定文件，使用默认的中英文文件
    if [[ ${#FILES[@]} -eq 0 ]]; then
        FILES=("src/main/resources/strings/strings_zh.xml" "src/main/resources/strings/strings_en.xml")
    fi
    
    for file in "${FILES[@]}"; do
        echo "处理文件: $file"
        echo "=================================="
        
        if ! check_file "$file"; then
            has_errors=1
            continue
        fi
        
        if ! validate_xml "$file"; then
            has_errors=1
            continue
        fi
        
        generate_report "$file"
        
        local name_duplicates=0
        local content_duplicates=0
        
        analyze_duplicates "$file"
        name_duplicates=$?
        
        check_content_duplicates "$file"
        content_duplicates=$?
        
        if [[ "$AUTO_FIX" == true && $name_duplicates -ne 0 ]]; then
            if [[ "$DRY_RUN" == false ]]; then
                create_backup "$file"
            fi
            auto_fix_duplicates "$file"
        fi
        
        echo "处理完成！"
        echo ""
    done
    
    return $has_errors
}

# 位置比对模式主函数
position_compare_mode() {
    local zh_file="src/main/resources/strings/strings_zh.xml"
    local en_file="src/main/resources/strings/strings_en.xml"
    
    echo "=== 中英文字符串资源文件位置比对 ==="
    echo ""
    
    # 检查文件是否存在
    if [[ ! -f "$zh_file" ]]; then
        echo -e "${RED}错误: 中文文件不存在: $zh_file${NC}"
        return 1
    fi
    
    if [[ ! -f "$en_file" ]]; then
        echo -e "${RED}错误: 英文文件不存在: $en_file${NC}"
        return 1
    fi
    
    # 创建临时文件存储key和行号的映射
    local zh_temp=$(mktemp)
    local en_temp=$(mktemp)
    
    echo "1. 提取中文文件中的key和行号..."
    grep -n 'name="[^"]*"' "$zh_file" | sed 's/^\([0-9]*\):.*name="\([^"]*\)".*/\1:\2/' > "$zh_temp"
    
    echo "2. 提取英文文件中的key和行号..."
    grep -n 'name="[^"]*"' "$en_file" | sed 's/^\([0-9]*\):.*name="\([^"]*\)".*/\1:\2/' > "$en_temp"
    
    echo "3. 统计信息..."
    local zh_count=$(wc -l < "$zh_temp")
    local en_count=$(wc -l < "$en_temp")
    echo "中文文件字符串数量: $zh_count"
    echo "英文文件字符串数量: $en_count"
    echo ""
    
    # 找出共同的key
    local common_keys=$(comm -12 <(cut -d: -f2 "$zh_temp" | sort) <(cut -d: -f2 "$en_temp" | sort))
    local common_count=$(echo "$common_keys" | wc -l)
    echo "4. 共同key数量: $common_count"
    echo ""
    
    # 检查行号差异
    local mismatched_count=0
    local max_diff=0
    local total_diff=0
    
    echo "5. 检查行号一致性..."
    echo "$common_keys" | while read key; do
        local zh_line=$(grep ":$key$" "$zh_temp" | cut -d: -f1)
        local en_line=$(grep ":$key$" "$en_temp" | cut -d: -f1)
        
        if [[ -n "$zh_line" && -n "$en_line" ]]; then
            local diff=$((zh_line - en_line))
            local abs_diff=${diff#-}  # 取绝对值
            
            if [[ $abs_diff -gt 0 ]]; then
                mismatched_count=$((mismatched_count + 1))
                total_diff=$((total_diff + abs_diff))
                
                if [[ $abs_diff -gt $max_diff ]]; then
                    max_diff=$abs_diff
                fi
                
                if [[ "$VERBOSE" == true ]]; then
                    echo -e "${YELLOW}行号不匹配: $key${NC}"
                    echo "  中文文件第${zh_line}行"
                    echo "  英文文件第${en_line}行"
                    echo "  差异: $diff 行"
                    echo ""
                fi
            fi
        fi
    done
    
    # 由于while循环在子shell中执行，我们需要用其他方式统计
    local actual_mismatched=$(echo "$common_keys" | while read key; do
        local zh_line=$(grep ":$key$" "$zh_temp" | cut -d: -f1)
        local en_line=$(grep ":$key$" "$en_temp" | cut -d: -f1)
        
        if [[ -n "$zh_line" && -n "$en_line" ]]; then
            local diff=$((zh_line - en_line))
            local abs_diff=${diff#-}
            
            if [[ $abs_diff -gt 0 ]]; then
                echo "1"
            fi
        fi
    done | wc -l)
    
    local actual_total_diff=$(echo "$common_keys" | while read key; do
        local zh_line=$(grep ":$key$" "$zh_temp" | cut -d: -f1)
        local en_line=$(grep ":$key$" "$en_temp" | cut -d: -f1)
        
        if [[ -n "$zh_line" && -n "$en_line" ]]; then
            local diff=$((zh_line - en_line))
            local abs_diff=${diff#-}
            echo "$abs_diff"
        fi
    done | awk '{sum+=$1} END {print sum}')
    
    local actual_max_diff=$(echo "$common_keys" | while read key; do
        local zh_line=$(grep ":$key$" "$zh_temp" | cut -d: -f1)
        local en_line=$(grep ":$key$" "$en_temp" | cut -d: -f1)
        
        if [[ -n "$zh_line" && -n "$en_line" ]]; then
            local diff=$((zh_line - en_line))
            local abs_diff=${diff#-}
            echo "$abs_diff"
        fi
    done | sort -n | tail -1)
    
    echo "6. 位置比对结果:"
    if [[ $actual_mismatched -eq 0 ]]; then
        echo -e "${GREEN}✅ 所有共同key的行号都一致！${NC}"
    else
        echo -e "${YELLOW}⚠️  发现 $actual_mismatched 个key的行号不一致${NC}"
        echo "  最大行号差异: $actual_max_diff 行"
        echo "  平均行号差异: $(echo "scale=1; $actual_total_diff / $actual_mismatched" | bc 2>/dev/null || echo "N/A") 行"
        
        if [[ "$VERBOSE" == false ]]; then
            echo ""
            echo "使用 -v 选项查看详细的不匹配信息"
        fi
    fi
    
    # 清理临时文件
    rm "$zh_temp" "$en_temp"
    
    echo ""
    echo "=== 位置比对完成 ==="
    echo ""
}
    local zh_file="src/main/resources/strings/strings_zh.xml"
    local en_file="src/main/resources/strings/strings_en.xml"
    
    echo "=== 中英文字符串资源文件比对 ==="
    echo ""
    
    # 检查文件是否存在
    if [[ ! -f "$zh_file" ]]; then
        echo -e "${RED}错误: 中文文件不存在: $zh_file${NC}"
        return 1
    fi
    
    if [[ ! -f "$en_file" ]]; then
        echo -e "${RED}错误: 英文文件不存在: $en_file${NC}"
        return 1
    fi
    
    # 提取所有字符串名称
    echo "1. 提取中文文件中的字符串名称..."
    local zh_names=$(grep -o 'name="[^"]*"' "$zh_file" | sed 's/name="//g' | sed 's/"//g' | sort)
    
    echo "2. 提取英文文件中的字符串名称..."
    local en_names=$(grep -o 'name="[^"]*"' "$en_file" | sed 's/name="//g' | sed 's/"//g' | sort)
    
    echo "3. 统计信息..."
    local zh_count=$(echo "$zh_names" | wc -l)
    local en_count=$(echo "$en_names" | wc -l)
    
    echo "中文文件字符串数量: $zh_count"
    echo "英文文件字符串数量: $en_count"
    echo ""
    
    # 找出中文有但英文没有的字符串
    echo "4. 中文有但英文没有的字符串:"
    local missing_in_en=$(comm -23 <(echo "$zh_names") <(echo "$en_names"))
    if [[ -z "$missing_in_en" ]]; then
        echo -e "${GREEN}✅ 没有缺失的英文翻译${NC}"
    else
        echo "$missing_in_en" | nl
    fi
    echo ""
    
    # 找出英文有但中文没有的字符串
    echo "5. 英文有但中文没有的字符串:"
    local missing_in_zh=$(comm -13 <(echo "$zh_names") <(echo "$en_names"))
    if [[ -z "$missing_in_zh" ]]; then
        echo -e "${GREEN}✅ 没有缺失的中文翻译${NC}"
    else
        echo "$missing_in_zh" | nl
    fi
    echo ""
    
    # 显示具体的缺失内容
    if [[ ! -z "$missing_in_en" ]]; then
        echo "6. 缺失的英文翻译详情:"
        echo "$missing_in_en" | while read name; do
            local zh_line=$(grep "name=\"$name\"" "$zh_file")
            echo "字符串名称: $name"
            echo "中文内容: $zh_line"
            echo "---"
        done
    fi
    
    if [[ ! -z "$missing_in_zh" ]]; then
        echo "7. 缺失的中文翻译详情:"
        echo "$missing_in_zh" | while read name; do
            local en_line=$(grep "name=\"$name\"" "$en_file")
            echo "字符串名称: $name"
            echo "英文内容: $en_line"
            echo "---"
        done
    fi
    
    echo "=== 比对完成 ==="
    echo ""
}

# 主函数
main() {
    # 初始化文件数组
    FILES=()
    
    # 解析参数
    parse_args "$@"
    
    # 检查是否指定了模式
    if [[ "$CLEANUP_MODE" == false && "$COMPARE_MODE" == false ]]; then
        echo -e "${RED}错误: 请指定操作模式 (-c, -m, 或 -a)${NC}"
        show_help
        exit 1
    fi
    
    # 执行清理模式
    if [[ "$CLEANUP_MODE" == true ]]; then
        echo "字符串资源文件清理工具 v2.0"
        echo ""
        cleanup_mode
    fi
    
    # 执行比对模式
    if [[ "$COMPARE_MODE" == true ]]; then
        compare_mode
    fi
    
    echo "所有操作完成！"
}

# 运行主函数
main "$@"
