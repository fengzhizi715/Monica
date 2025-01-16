package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.model.MatchTemplateSettings
import cn.netdiscovery.monica.utils.logger
import com.safframework.rxcache.utils.GsonUtils
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MatchTemplateViewModel
 * @author: Tony Shen
 * @date: 2025/1/1 15:32
 * @version: V1.0 <描述当前版本功能>
 */
class MatchTemplateViewModel {

    private val logger: Logger = logger<MatchTemplateViewModel>()

    fun matchTemplate(state: ApplicationState, matchTemplateSettings: MatchTemplateSettings) {
        logger.info("matchTemplateSettings = ${GsonUtils.toJson(matchTemplateSettings)}")


    }
}