package cn.netdiscovery.monica.ui.controlpanel.filter

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.imageprocess.BufferedImages
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.ui.widget.rightSideMenuBar
import cn.netdiscovery.monica.ui.widget.toolTipButton
import cn.netdiscovery.monica.utils.chooseImage
import org.koin.compose.koinInject
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.filter.FilterView
 * @author: Tony Shen
 * @date: 2025/3/6 15:34
 * @version: V1.0 <描述当前版本功能>
 */
private val logger: Logger = LoggerFactory.getLogger(object : Any() {}.javaClass.enclosingClass)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun filter(state: ApplicationState) {

    val viewModel: FilterViewModel = koinInject()

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row (
                modifier = Modifier.fillMaxSize().padding(bottom = 200.dp, end = 400.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.padding(10.dp).weight(1.0f),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 4.dp,
                    onClick = {
                        chooseImage(state) { file ->
                            state.rawImage = BufferedImages.load(file)
                            state.currentImage = state.rawImage
                            state.rawImageFile = file
                        }
                    },
                    enabled = state.currentImage == null
                ) {
                    if (state.currentImage == null) {
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
                                    painter = state.currentImage!!.toPainter(),
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

        rightSideMenuBar(modifier = Modifier.width(400.dp).height(600.dp).align(Alignment.CenterEnd),
            backgroundColor = Color.White, percent = 5) {

        }
    }
}