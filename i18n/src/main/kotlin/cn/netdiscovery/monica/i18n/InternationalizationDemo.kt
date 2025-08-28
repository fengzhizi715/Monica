package cn.netdiscovery.monica.i18n

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * 国际化功能演示程序
 */
class InternationalizationDemo : JFrame(), ActionListener {
    
    private val languageLabel = JLabel()
    private val stringLabel = JLabel()
    private val languageButton = JButton()
    private val demoButton = JButton("演示字符串")
    
    init {
        setupUI()
        updateDisplay()
    }
    
    private fun setupUI() {
        title = "Monica 国际化演示"
        defaultCloseOperation = EXIT_ON_CLOSE
        layout = BorderLayout()
        setSize(500, 300)
        setLocationRelativeTo(null)
        
        // 顶部面板 - 语言信息
        val topPanel = JPanel(FlowLayout(FlowLayout.LEFT))
        topPanel.add(JLabel("当前语言: "))
        topPanel.add(languageLabel)
        topPanel.add(languageButton)
        add(topPanel, BorderLayout.NORTH)
        
        // 中间面板 - 字符串演示
        val centerPanel = JPanel(BorderLayout())
        centerPanel.border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        
        val demoPanel = JPanel(GridLayout(0, 1, 10, 10))
        demoPanel.add(JLabel("应用名称: ${LocalizationManager.getString("app_name")}"))
        demoPanel.add(JLabel("应用描述: ${LocalizationManager.getString("app_description")}"))
        demoPanel.add(JLabel("通用设置: ${LocalizationManager.getString("general_settings")}"))
        demoPanel.add(JLabel("基础功能: ${LocalizationManager.getString("basic_functions")}"))
        demoPanel.add(JLabel("AI 实验室: ${LocalizationManager.getString("ai_laboratory")}"))
        demoPanel.add(JLabel("图像模糊: ${LocalizationManager.getString("image_blur")}"))
        demoPanel.add(JLabel("图像马赛克: ${LocalizationManager.getString("image_mosaic")}"))
        demoPanel.add(JLabel("形状绘制: ${LocalizationManager.getString("shape_drawing")}"))
        
        centerPanel.add(demoPanel, BorderLayout.CENTER)
        add(centerPanel, BorderLayout.CENTER)
        
        // 底部面板 - 控制按钮
        val bottomPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        bottomPanel.add(demoButton)
        add(bottomPanel, BorderLayout.SOUTH)
        
        // 设置事件监听器
        languageButton.addActionListener(this)
        demoButton.addActionListener(this)
    }
    
    private fun updateDisplay() {
        val currentLang = LocalizationManager.currentLanguage
        languageLabel.text = "${currentLang.flag} ${currentLang.displayName}"
        languageButton.text = "切换到${if (currentLang == Language.CHINESE) "英文" else "中文"}"
    }
    
    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            languageButton -> {
                val currentLang = LocalizationManager.currentLanguage
                val newLang = if (currentLang == Language.CHINESE) Language.ENGLISH else Language.CHINESE
                LocalizationManager.setLanguage(newLang)
                updateDisplay()
                
                // 重新构建UI以显示新语言的字符串
                SwingUtilities.invokeLater {
                    dispose()
                    InternationalizationDemo().isVisible = true
                }
            }
            demoButton -> {
                showStringDemo()
            }
        }
    }
    
    private fun showStringDemo() {
        val strings = getCurrentStringResource()
        val demoText = """
            当前语言: ${LocalizationManager.currentLanguage.displayName}
            
            字符串资源信息:
            ${strings.getXmlResourceInfo()}
            
            包含的键数量: ${strings.getAllKeys().size}
            
            示例字符串:
            - 应用名称: ${strings.get("app_name")}
            - 通用设置: ${strings.get("general_settings")}
            - 基础功能: ${strings.get("basic_functions")}
            - 带参数字符串: ${strings.get("color_parameters_updated", "亮度+10")}
        """.trimIndent()
        
        JOptionPane.showMessageDialog(
            this,
            demoText,
            "字符串资源演示",
            JOptionPane.INFORMATION_MESSAGE
        )
    }
    
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                InternationalizationDemo().isVisible = true
            }
        }
    }
}
