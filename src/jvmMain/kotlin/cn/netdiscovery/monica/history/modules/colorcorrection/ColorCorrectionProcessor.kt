package cn.netdiscovery.monica.history.modules.colorcorrection

import cn.netdiscovery.monica.domain.ColorCorrectionSettings
import cn.netdiscovery.monica.edit.history.core.ParameterizedProcessor
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.history.modules.colorcorrection.ColorCorrectionProcessor
 * @author: Tony Shen
 * @date: 2025/7/28 19:31
 * @version: V1.0 <描述当前版本功能>
 */
class ColorCorrectionProcessor(private val originalImage: BufferedImage): ParameterizedProcessor<ColorCorrectionSettings, BufferedImage> {

    private var currentParams: ColorCorrectionSettings = ColorCorrectionSettings()

    override fun applyParams(params: ColorCorrectionSettings) {
        currentParams = params
    }

    override fun getCurrentParams(): ColorCorrectionSettings = currentParams

    override fun process(): BufferedImage {
        TODO("Not yet implemented")
    }
}