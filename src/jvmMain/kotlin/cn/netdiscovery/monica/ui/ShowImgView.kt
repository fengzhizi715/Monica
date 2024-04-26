package cn.netdiscovery.monica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ShowImgView
 * @author: Tony Shen
 * @date: 2024/4/26 22:18
 * @version: V1.0 <描述当前版本功能>
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ShowImgView(
    img: ImageBitmap
) {

    var scaleNumber by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var isShowScaleTip by remember { mutableStateOf(false) }

    LaunchedEffect(isShowScaleTip) {
        delay(2000)
        isShowScaleTip = false
    }

    Box(
        Modifier
            .fillMaxSize()
            .onPointerEvent(PointerEventType.Scroll) {
                scaleNumber = (scaleNumber - it.changes.first().scrollDelta.y).coerceIn(1f, 20f)
                isShowScaleTip = true
            }
            .onPointerEvent(PointerEventType.Move) {
                if (it.changes.first().pressed) {
                    offset -= (it.changes.first().previousPosition - it.changes.first().position)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = img,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scaleNumber
                    scaleY = scaleNumber

                    translationX = offset.x
                    translationY = offset.y
                }
            ,
        )

        AnimatedVisibility(
            visible = isShowScaleTip
        ) {
            Text(
                "${scaleNumber}X",
                modifier = Modifier.background(MaterialTheme.colors.surface),
                color = MaterialTheme.colors.onSurface,
                fontSize = 48.sp
            )
        }

        AnimatedVisibility(
            visible = offset != Offset.Zero || scaleNumber != 1f,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            OutlinedButton(
                onClick = {
                    offset = Offset.Zero
                    scaleNumber = 1f
                },
            ) {
                Text("恢复")
            }
        }
    }
}