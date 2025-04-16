package cn.netdiscovery.monica.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import cn.netdiscovery.monica.ui.controlpanel.ai.AIViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.BinaryImageViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ContourAnalysisViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.EdgeDetectionViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ImageEnhanceViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.ImageDenoisingViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MatchTemplateViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.viewmodel.MorphologicalOperationsViewModel
import cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapViewModel
import cn.netdiscovery.monica.ui.controlpanel.cartoon.CartoonViewModel
import cn.netdiscovery.monica.ui.controlpanel.colorcorrection.ColorCorrectionViewModel
import cn.netdiscovery.monica.ui.controlpanel.cropimage.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.doodle.DoodleViewModel
import cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
import cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifViewModel
import cn.netdiscovery.monica.ui.controlpanel.shapedrawing.ShapeDrawingViewModel
import cn.netdiscovery.monica.ui.main.MainViewModel
import cn.netdiscovery.monica.ui.preview.PreviewViewModel


/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.di.appModule
 * @author: Tony Shen
 * @date: 2024/5/7 20:28
 * @version: V1.0 <描述当前版本功能>
 */
val viewModelModule = module {

    singleOf(::MainViewModel)
    singleOf(::PreviewViewModel)
    singleOf(::DoodleViewModel)
    singleOf(::ShapeDrawingViewModel)
    singleOf(::CropViewModel)
    singleOf(::ColorCorrectionViewModel)
    singleOf(::FilterViewModel)
    singleOf(::AIViewModel)
    singleOf(::FaceSwapViewModel)
    singleOf(::BinaryImageViewModel)
    singleOf(::EdgeDetectionViewModel)
    singleOf(::ContourAnalysisViewModel)
    singleOf(::ImageEnhanceViewModel)
    singleOf(::ImageDenoisingViewModel)
    singleOf(::MorphologicalOperationsViewModel)
    singleOf(::MatchTemplateViewModel)
    singleOf(::GenerateGifViewModel)
    singleOf(::CartoonViewModel)
}