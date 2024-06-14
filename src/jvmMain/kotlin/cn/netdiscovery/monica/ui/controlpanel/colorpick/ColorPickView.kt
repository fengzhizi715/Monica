package cn.netdiscovery.monica.ui.controlpanel.colorpick

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.colorpick.ColorPickView
 * @author: Tony Shen
 * @date: 2024/6/13 16:29
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun rememberColorParser(): ColorNameParser {
    return remember {
        ColorNameParser()
    }
}

@Composable
fun colorPick(state: ApplicationState) {

    val colorNameParser = rememberColorParser()

    var currentColor by remember { mutableStateOf(Color.Unspecified) }
    var colorName by remember { mutableStateOf("") }

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
            thumbnailSize = 150.dp
        ) { colorData: ColorData ->
            currentColor = colorData.color
            colorName = colorData.name

            println("colorData = ${colorData.hexText}")
        }
    }
}