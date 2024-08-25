package cn.netdiscovery.monica.ui.controlpanel.ai.faceswap

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
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.netdiscovery.monica.state.ApplicationState

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.controlpanel.ai.faceswap.FaceSwap
 * @author: Tony Shen
 * @date: 2024/8/25 13:02
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun faceSwap(state: ApplicationState) {

    Row (
        modifier = Modifier.fillMaxSize().padding(top = 50.dp, bottom = 80.dp, start = 20.dp, end = 20.dp),
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
//            Text(
//                modifier = Modifier.padding(vertical = 1.dp),
//                text = "source",
//                color = MaterialTheme.colors.primary,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )

            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
//            previewViewModel.chooseImage(state)
            },
            enabled = state.rawImage == null
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "请点击选择图像或拖拽图像至此",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}