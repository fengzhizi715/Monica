package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorData
import cn.netdiscovery.monica.ui.controlpanel.colorpick.model.ColorNameParser
import cn.netdiscovery.monica.ui.controlpanel.colorpick.widget.ColorDisplay
import cn.netdiscovery.monica.ui.controlpanel.colorpick.widget.ImageColorDetector

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

    val image = state.currentImage!!.toComposeImageBitmap()

    Box(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        ImageColorDetector(
            modifier = Modifier.fillMaxWidth().aspectRatio(16 / 9f),
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