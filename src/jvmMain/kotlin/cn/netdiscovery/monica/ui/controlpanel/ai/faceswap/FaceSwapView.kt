package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.composeClick
import org.koin.compose.koinInject

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwapView
 * @author: Tony Shen
 * @date: 2024/8/25 13:02
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun faceSwap(state: ApplicationState) {

    val viewModel: FaceSwapModel = koinInject()

    Column (modifier = Modifier.fillMaxSize()){
        Row (
            modifier = Modifier.fillMaxSize().weight(9.5f).padding(top= 20.dp, start = 20.dp, end = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp).weight(1.0f),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
//            previewViewModel.chooseImage(state)
                },
                enabled = state.rawImage == null
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier,
                        text = "source",
                        color = MaterialTheme.colors.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Image(
                        painter = state.currentImage!!.toPainter(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier)
                }
            }

            Card(
                modifier = Modifier.padding(16.dp).weight(1.0f),
                shape = RoundedCornerShape(8.dp),
                elevation = 4.dp,
                onClick = {
                },
                enabled = state.rawImage == null
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "请点击选择图像",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row(modifier = Modifier.fillMaxSize().weight(0.5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.width(100.dp),
                onClick = composeClick {
                },
                enabled = true
            ) {
                Text(text = "确定",
                    color = if (true) Color.Unspecified else Color.LightGray)
            }
        }
    }
}