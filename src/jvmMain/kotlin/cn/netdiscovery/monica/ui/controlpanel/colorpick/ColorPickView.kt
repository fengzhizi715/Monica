package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorNameParser
import cn.netdiscovery.monica.ui.controlpanel.colorpick.widget.ColorDisplay
import cn.netdiscovery.monica.ui.controlpanel.colorpick.widget.ImageColorDetector
import cn.netdiscovery.monica.ui.widget.image.ImageSizeCalculator

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorPickView
 * @author: Tony Shen
 * @date: 2024/6/13 16:29
 * @version: V1.0 <描述当前版本功能>
 */

val colorNameParser = ColorNameParser()

val defaultThumbnailSize = 150.dp

typealias OnColorChange = (ColorData) -> Unit

@Composable
fun colorPick(state: ApplicationState) {

    var colorData by remember { mutableStateOf(ColorData(color = Color.Unspecified, name = ""))  }

    // 安全获取图片，避免空指针异常
    val image = state.currentImage?.toComposeImageBitmap()

    // 如果图片为空，显示提示信息
    if (image == null) {
    Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "请先加载图片",
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.background,
                        MaterialTheme.colors.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 使用统一的图片尺寸计算
        val (width, height) = ImageSizeCalculator.calculateImageSize(state)
        
        ImageColorDetector(
            modifier = Modifier
                .width(width)
                .height(height),
            contentScale = ContentScale.Fit,
            colorNameParser = colorNameParser,
            imageBitmap = image,
            thumbnailSize = defaultThumbnailSize
        ) {
            colorData = it
        }

        if (colorData.color != Color.Unspecified) {
            ColorDisplay(
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 20.dp),
                colorData = colorData
            )
        }
    }
}