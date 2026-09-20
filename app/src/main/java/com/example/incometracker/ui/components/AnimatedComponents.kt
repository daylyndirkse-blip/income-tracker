package com.example.incometracker.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                }
            }
    ) {
        content()
    }
}

@Composable
fun FadeInSlideUp(
    visible: Boolean = true,
    delay: Int = 0,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 400, delayMillis = delay)
        ) + slideInVertically(
            initialOffsetY = { it / 3 },
            animationSpec = tween(durationMillis = 400, delayMillis = delay, easing = FastOutSlowInEasing)
        ),
        exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
            targetOffsetY = { -it / 3 },
            animationSpec = tween(200)
        ),
        content = content
    )
}

@Composable
fun ScaleInAnimation(
    visible: Boolean = true,
    delay: Int = 0,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium,
                visibilityThreshold = 0.001f
            ).run { delay(delay) }
        ) + fadeIn(tween(300, delayMillis = delay)),
        exit = scaleOut(targetScale = 0.8f) + fadeOut(tween(200)),
        content = content
    )
}

@Composable
fun PulseAnimation(
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    
    content(scale)
}

@Composable
fun ShakeAnimation(
    shake: Boolean,
    onShakeComplete: () -> Unit = {},
    content: @Composable (offsetX: Float) -> Unit
) {
    var currentShake by remember { mutableStateOf(shake) }
    
    val offsetX by animateFloatAsState(
        targetValue = if (currentShake) 10f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(50),
            repeatMode = RepeatMode.Reverse
        ),
        finishedListener = {
            currentShake = false
            onShakeComplete()
        },
        label = "shake"
    )
    
    LaunchedEffect(shake) {
        if (shake) currentShake = true
    }
    
    content(offsetX)
}

private fun <T> AnimationSpec<T>.delay(delayMillis: Int): AnimationSpec<T> {
    return tween<T>(delayMillis = delayMillis).then(this)
}

private fun <T> AnimationSpec<T>.then(next: AnimationSpec<T>): AnimationSpec<T> {
    return this
}
