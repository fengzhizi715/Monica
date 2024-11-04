package cn.netdiscovery.monica.ui.widget

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.netdiscovery.monica.config.height
import cn.netdiscovery.monica.config.loadingWidth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.ThreeBallLoading
 * @author: Tony Shen
 * @date: 2024/4/28 17:45
 * @version: V1.0 <描述当前版本功能>
 */
@Composable
fun showLoading() {
    ThreeBallLoading(Modifier.width(loadingWidth).height(height))
}

@Composable
fun ThreeBallLoading(modifier: Modifier) {
    val width = remember { mutableStateOf(800f) }
    val height = remember { mutableStateOf(800f) }
    val centerX = width.value / 2
    val centerY = height.value / 2
    val anglist = Array(3) {
        120f * it
    }
    val ballradius = 20f
    val colorList = listOf(
        Color(0xffFF1D1D),
        Color(0xff0055FF),
        Color(0xff43B988),
    )
    val transition = rememberInfiniteTransition()
    val radiusDiff = transition.animateFloat(
        ballradius / 2, ballradius * 4, animationSpec = InfiniteRepeatableSpec(
            tween(durationMillis = 500, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        )
    )
    val diff = remember { mutableStateOf(0f) }
    LaunchedEffect(true) {
        flow {
            while (true) {
                emit(1)
                delay(1000)
            }
        }.collect {
            diff.value += 90f
        }
    }
    val angleDiff = animateFloatAsState(
        diff.value, TweenSpec(
            durationMillis = 500, easing = LinearEasing
        )
    )
    Canvas(
        modifier = modifier.padding(10.dp)
    ) {
        width.value = size.width
        height.value = size.height

        for (index in anglist.indices) {
            drawCircle(
                colorList[index], radius = 20f, center = Offset(
                    pointX(radiusDiff.value, centerX, anglist[index] + angleDiff.value),
                    pointY(radiusDiff.value, centerY, anglist[index] + angleDiff.value)
                )
            )
        }

    }
}

private fun pointX(radius: Float, centerX: Float, angle: Float): Float {
    val res = Math.toRadians(angle.toDouble())
    return centerX - cos(res).toFloat() * (radius)
}

private fun pointY(radius: Float, centerY: Float, angle: Float): Float {
    val res = Math.toRadians(angle.toDouble())
    return centerY - sin(res).toFloat() * (radius)
}