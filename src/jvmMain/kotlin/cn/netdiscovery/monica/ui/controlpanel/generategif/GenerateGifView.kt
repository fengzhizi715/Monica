package cn.netdiscovery.monica.ui.controlpanel.generategif

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.controlpanel.ai.experiment.CVState
import cn.netdiscovery.monica.utils.chooseImage
import java.awt.image.BufferedImage

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.generategif.GenerateGifView
 * @author: Tony Shen
 * @date:  2025/2/23 16:16
 * @version: V1.0 <描述当前版本功能>
 */
private val list = mutableListOf<BufferedImage>()

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun generateGif(state: ApplicationState) {

    Column {

        Row {
            Card(
                modifier = Modifier.padding(10.dp).width(300.dp).height(300.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                },
                enabled = list.size == 0
            ) {
                if (list.size == 0) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = "请点击选择图像",
                        textAlign = TextAlign.Center
                    )
                } else {
                    Box {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = list[0].toPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.padding(10.dp).width(300.dp).height(300.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                },
                enabled = list.size > 1
            ) {
                if (list.size == 1) {
                    Text(
                        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                        text = "请添加图片",
                        textAlign = TextAlign.Center
                    )
                } else if (list.size == 0) {

                } else if (list.size > 1) {
                    Box {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = list[1].toPainter(),
                                contentDescription = null,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
        }
    }
}