package cn.netdiscovery.monica.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ShowImgView
 * @author: Tony Shen
 * @date: 2024/4/26 22:18
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun ShowImageView(
    image: ImageBitmap
) {
    var angle by remember { mutableStateOf(0f) }//旋转角度
    var scale by remember { mutableStateOf(1f) }//缩放
    var offsetX by remember { mutableStateOf(0f) }//x偏移
    var offsetY by remember { mutableStateOf(0f) }//y偏移
    var matrix by remember { mutableStateOf(Matrix()) }//矩阵

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = angle
                    translationX = offsetX
                    translationY = offsetY
                }
                .pointerInput(Unit) {
                    detectTransformGestures { centroid, pan, zoom, rotation ->
                        angle += rotation
                        scale *= zoom

                        matrix.translate(pan.x, pan.y)
                        matrix.rotateZ(rotation)
                        matrix.scale(zoom, zoom)

                        matrix = Matrix(matrix.values)

                        offsetX = matrix.values[Matrix.TranslateX]
                        offsetY = matrix.values[Matrix.TranslateY]
                    }
                }
        )

        AnimatedVisibility(
            visible = offsetX!=0f && offsetY!=0f,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            OutlinedButton(
                onClick = {
                    angle = 0f
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                    matrix = Matrix()
                },
            ) {
                Text("恢复")
            }
        }
    }
}