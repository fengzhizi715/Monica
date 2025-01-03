package cn.netdiscovery.monica.ui.showimage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.state.ApplicationState
import cn.netdiscovery.monica.utils.extension.to2fStr

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.showimage.ShowImgView
 * @author: Tony Shen
 * @date: 2024/4/26 22:18
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun showImage(
    state: ApplicationState
) {
    var angle   by remember { mutableStateOf(0f) }  // 旋转角度
    var scale   by remember { mutableStateOf(1f) }  // 缩放
    var offsetX by remember { mutableStateOf(0f) }  // x偏移
    var offsetY by remember { mutableStateOf(0f) }  // y偏移
    var matrix  by remember { mutableStateOf(Matrix()) }  // 矩阵

    val image = state.currentImage!!.toComposeImageBitmap()

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

        Row (modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)){

            Column(
                Modifier.padding(end = 10.dp),
                verticalArrangement = Arrangement.Center
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

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = scale.to2fStr(),
                    color = Color.Unspecified,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                verticalSlider(
                    value = scale,
                    onValueChange = {
                        scale = it
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .background(Color(0xffdedede)),
                    valueRange = 0.1f..5f
                )
            }
        }
    }
}

@Composable
fun verticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    /*@IntRange(from = 0)*/
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors()
){
    Slider(
        colors = colors,
        interactionSource = interactionSource,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        valueRange = valueRange,
        enabled = enabled,
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .then(modifier)
    )
}