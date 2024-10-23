package cn.netdiscovery.monica.ui.widget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 *
 * @FileName:
 *          cn.netdiscovery.monica.ui.widget.Toasts
 * @author: Tony Shen
 * @date: 2024/5/28 15:13
 * @version: V1.0 <描述当前版本功能>
 */

@Composable
fun TopToast(
    modifier: Modifier = Modifier,
    message: String = "An unexpected error occurred. Please try again later",
    height: Dp = 100.dp,
    width: Dp = 400.dp,
    onDismissCallback: @Composable () -> Unit = {},
) {
    var hasTransitionStarted by remember { mutableStateOf(false) }
    var clipShape by remember { mutableStateOf(CircleShape) }
    var slideDownAnimation by remember { mutableStateOf(true) }
    var animationStarted by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var dismissCallback by remember { mutableStateOf(false) }

    val boxWidth by animateDpAsState(
        targetValue = if (hasTransitionStarted) width else 30.dp,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "Box width",
    )

    val boxHeight by animateDpAsState(
        targetValue = if (hasTransitionStarted) height else 30.dp,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "Box height",
    )

    val slideY by animateDpAsState(
        targetValue = if (slideDownAnimation) (-100).dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "Slide parameter in DP",
    )

    if (!animationStarted) {
        LaunchedEffect(Unit) {
            slideDownAnimation = false

            // Delay for 0.2 seconds before transitioning to rectangle
            delay(200)
            hasTransitionStarted = true
            clipShape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 12.dp)
            showMessage = true

            // Delay for 2.5 seconds before reverting to circle
            delay(2500)
            hasTransitionStarted = false
            showMessage = false

            // Delay for 0.2 seconds before sliding up
            delay(200)
            clipShape = CircleShape
            slideDownAnimation = true
            animationStarted = true
            dismissCallback = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
    ) {
        Box(
            modifier = modifier
                .size(boxWidth, boxHeight)
                .offset(y = slideY)
                .clip(clipShape)
                .background(MaterialTheme.colors.primary)
                .align(alignment = Alignment.TopCenter),
            contentAlignment = Alignment.Center,
        ) {
            if (showMessage) {
                Text(
                    text = message,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp),
                )
            }

            if (dismissCallback) onDismissCallback()
        }
    }
}

@Composable
fun CenterToast(
    modifier: Modifier = Modifier,
    message: String = "An unexpected error occurred. Please try again later",
    height: Dp = 100.dp,
    width: Dp = 400.dp,
    onDismissCallback: @Composable () -> Unit = {},
) {
    var hasTransitionStarted by remember { mutableStateOf(false) }
    var clipShape by remember { mutableStateOf(CircleShape) }
    var slideDownAnimation by remember { mutableStateOf(true) }
    var animationStarted by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var dismissCallback by remember { mutableStateOf(false) }

    val boxWidth by animateDpAsState(
        targetValue = if (hasTransitionStarted) width else 30.dp,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "Box width",
    )

    val boxHeight by animateDpAsState(
        targetValue = if (hasTransitionStarted) height else 30.dp,
        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing),
        label = "Box height",
    )

    val slideY by animateDpAsState(
        targetValue = if (slideDownAnimation) (-100).dp else 0.dp,
        animationSpec = tween(durationMillis = 100),
        label = "Slide parameter in DP",
    )

    if (!animationStarted) {
        LaunchedEffect(Unit) {
            slideDownAnimation = false

            // Delay for 0.2 seconds before transitioning to rectangle
            delay(200)
            hasTransitionStarted = true
            clipShape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 12.dp)
            showMessage = true

            // Delay for 2.5 seconds before reverting to circle
            delay(2500)
            hasTransitionStarted = false
            showMessage = false

            // Delay for 0.2 seconds before sliding up
            delay(200)
            clipShape = CircleShape
            slideDownAnimation = true
            animationStarted = true
            dismissCallback = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
    ) {
        Box(
            modifier = modifier
                .size(boxWidth, boxHeight)
                .offset(y = slideY)
                .clip(clipShape)
                .background(MaterialTheme.colors.primary)
                .align(alignment = Alignment.Center),
            contentAlignment = Alignment.Center,
        ) {
            if (showMessage) {
                Text(
                    text = message,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp),
                )
            }

            if (dismissCallback) onDismissCallback()
        }
    }
}

@Composable
fun BottomToast(
    modifier: Modifier = Modifier,
    message: String = "An unexpected error occurred. Please try again later",
    height: Dp = 100.dp,
    width: Dp = 400.dp,
    onDismissCallback: @Composable () -> Unit = {},
) {
    var isTransitionStarted by remember { mutableStateOf(false) }
    var clipShape by remember { mutableStateOf(CircleShape) }
    var slideAnimation by remember { mutableStateOf(true) }
    var animationStarted by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf(false) }
    var dismissCallback by remember { mutableStateOf(false) }

    val boxWidth by animateDpAsState(
        targetValue = if (isTransitionStarted) width else 30.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "Box width",
    )

    val boxHeight by animateDpAsState(
        targetValue = if (isTransitionStarted) height else 30.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "Box Height",
    )

    val slideY by animateDpAsState(
        targetValue = if (slideAnimation) 100.dp else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "Slide parameter in DP",
    )

    if (!animationStarted) {
        LaunchedEffect(Unit) {
            slideAnimation = false

            // Delay for 0.33 seconds before transitioning to rectangle
            delay(330)
            isTransitionStarted = true
            clipShape = RoundedCornerShape(12.dp, 12.dp, 12.dp, 12.dp)
            showMessage = true

            // Delay for 2 seconds before transitioning back to circle
            delay(2000)
            isTransitionStarted = false
            showMessage = false

            // Delay for 0.33 seconds before sliding down
            delay(330)
            clipShape = CircleShape
            slideAnimation = true
            animationStarted = true
            dismissCallback = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp),
    ) {
        Box(
            modifier = modifier
                .size(boxWidth, boxHeight)
                .offset(y = slideY)
                .clip(clipShape)
                .background(MaterialTheme.colors.primary)
                .align(alignment = Alignment.BottomCenter),
            contentAlignment = Alignment.Center,
        ) {
            if (showMessage) {
                Text(
                    text = message,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(16.dp),
                )
            }

            if (dismissCallback) onDismissCallback()
        }
    }
}