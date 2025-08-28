package cn.netdiscovery.monica.i18n

import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.GridLayout
import javax.swing.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * 语言选择器对话框
 */
class LanguageSelectorDialog(
    private val onLanguageSelected: (Language) -> Unit
) : JDialog(), ActionListener {
    
    private val currentLanguage = LocalizationManager.currentLanguage
    private var selectedLanguage = currentLanguage
    
    init {
        setupUI()
    }
    
    private fun setupUI() {
        title = "选择语言 / Select Language"
        layout = BorderLayout()
        isModal = true
        setSize(300, 200)
        setLocationRelativeTo(null)
        
        // 语言选择面板
        val languagePanel = JPanel(GridLayout(Language.values().size, 1, 5, 5))
        val buttonGroup = ButtonGroup()
        
        Language.values().forEach { language ->
            val radioButton = JRadioButton("${language.flag} ${language.displayName}")
            radioButton.isSelected = language == currentLanguage
            radioButton.addActionListener { selectedLanguage = language }
            buttonGroup.add(radioButton)
            languagePanel.add(radioButton)
        }
        
        // 按钮面板
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val confirmButton = JButton("确定")
        val cancelButton = JButton("取消")
        
        confirmButton.addActionListener(this)
        cancelButton.addActionListener(this)
        
        add(languagePanel, BorderLayout.CENTER)
        add(buttonPanel, BorderLayout.SOUTH)
    }
    
    override fun actionPerformed(e: ActionEvent) {
        when (e.actionCommand) {
            "确定" -> {
                onLanguageSelected(selectedLanguage)
                dispose()
            }
            "取消" -> dispose()
        }
    }
}

/**
 * 语言切换按钮
 */
class LanguageSwitchButton : JButton() {
    
    init {
        updateText()
        addActionListener {
            val currentLang = LocalizationManager.currentLanguage
            val newLang = if (currentLang == Language.CHINESE) Language.ENGLISH else Language.CHINESE
            LocalizationManager.setLanguage(newLang)
            updateText()
        }
    }
    
    private fun updateText() {
        val currentLang = LocalizationManager.currentLanguage
        text = "${currentLang.flag} ${currentLang.displayName}"
    }
}

/**
 * 语言指示器（只显示当前语言）
 */
class LanguageIndicator : JLabel() {
    
    init {
        updateText()
    }
    
    private fun updateText() {
        val currentLang = LocalizationManager.currentLanguage
        text = "${currentLang.flag} ${currentLang.displayName}"
    }
}
