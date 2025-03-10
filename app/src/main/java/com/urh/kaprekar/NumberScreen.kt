package com.urh.kaprekar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urh.kaprekar.ui.theme.KapreKarTheme

@Composable
fun NumberScreen(
    state: NumberState,
    focusRequesters: List<FocusRequester>,
    onAction: (NumberAction) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {

    val wobbleDegrees: Float = 15f
    val wobbleDurationMillis: Int = 400
    val totalDurationMillis: Long = 2200

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.kaprekar_headline),
            textAlign = TextAlign.Center,
            color = KapreKarTheme.colorScheme.onSurface,
            fontSize = 34.sp,
            fontWeight = FontWeight.Normal,
        )

        Text(
            text = stringResource(R.string.kaprekar_number),
            textAlign = TextAlign.Center,
            color = KapreKarTheme.colorScheme.onSurface,
            fontSize = 36.sp,
            fontWeight = FontWeight.Light,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.End) {
            AnimatedContent(
                targetState = state.code.none { it == null },
                label = ""
            ) { show ->
                if (show) {
                    val rotation = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        val numberOfWobbles = totalDurationMillis / wobbleDurationMillis
                        for (i in 0 until numberOfWobbles) {
                            rotation.animateTo(
                                targetValue = -wobbleDegrees,
                                animationSpec = tween(durationMillis = wobbleDurationMillis / 2, easing = LinearEasing)
                            )
                            rotation.animateTo(
                                targetValue = wobbleDegrees,
                                animationSpec = tween(durationMillis = wobbleDurationMillis / 2, easing = LinearEasing)
                            )
                        }
                        rotation.snapTo(0f)
                    }
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = null,
                        tint = KapreKarTheme.colorScheme.surface,
                        modifier = Modifier
                            .size(58.dp)
                            .padding(bottom = 8.dp, end = 22.dp)
                            .rotate(rotation.value)
                            .clickable { onDelete() }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(22.dp, Alignment.CenterHorizontally)
            ) {
                state.code.forEachIndexed { index, number ->
                    DigitInputField(
                        number = number,
                        focusRequester = focusRequesters[index],
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                onAction(NumberAction.OnChangeFieldFocused(index))
                            }
                        },
                        onNumberChanged = { newNumber ->
                            onAction(NumberAction.OnEnterNumber(newNumber, index))
                        },
                        onKeyboardBack = {
                            onAction(NumberAction.OnKeyboardBack)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                    )
                }
            }
        }

        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = stringResource(R.string.kaprekar_meaning),
            textAlign = TextAlign.Start,
            color = KapreKarTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                ),
                lineHeight = 16.sp,
                letterSpacing = 0.sp
            )
        )
    }
}