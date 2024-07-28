package cn.netdiscovery.monica.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import cn.netdiscovery.monica.ui.controlpanel.ai.AIViewMode
import cn.netdiscovery.monica.ui.controlpanel.crop.CropViewModel
import cn.netdiscovery.monica.ui.controlpanel.doodle.DoodleViewModel
import cn.netdiscovery.monica.ui.controlpanel.enhance.ImageEnhanceViewModel
import cn.netdiscovery.monica.ui.controlpanel.filter.FilterViewModel
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
    singleOf(::CropViewModel)
    singleOf(::FilterViewModel)
    singleOf(::AIViewMode)
    singleOf(::ImageEnhanceViewModel)
}