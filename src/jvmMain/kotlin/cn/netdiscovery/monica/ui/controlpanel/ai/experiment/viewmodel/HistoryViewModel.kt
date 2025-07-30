package cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel

import cn.netdiscovery.monica.config.MODULE_OPENCV
import cn.netdiscovery.monica.history.EditHistoryCenter
import cn.netdiscovery.monica.history.HistoryEntry
import cn.netdiscovery.monica.history.modules.opencv.CVParams
import cn.netdiscovery.monica.utils.logger
import org.slf4j.Logger

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.HistoryViewModel
 * @author: Tony Shen
 * @date: 2025/7/30 09:52
 * @version: V1.0 <描述当前版本功能>
 */
class HistoryViewModel {
    private val logger: Logger = logger<HistoryViewModel>()
    private val manager = EditHistoryCenter.getManager<CVParams>(MODULE_OPENCV)

    fun getOperationLog():List<HistoryEntry>{

        return manager.getOperationLog().asReversed()
    }
}