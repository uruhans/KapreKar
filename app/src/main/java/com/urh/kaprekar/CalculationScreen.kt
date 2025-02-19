package com.urh.kaprekar

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.urh.kaprekar.ui.theme.KapreKarTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculationScreen(
    id: Int,
    viewModel: CalculationViewModel = koinViewModel()
) {
    val systemTopPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val kapreKarNumberList = viewModel.findKapreKarData(id)
    Log.d("URH", "URH $kapreKarNumberList")

    CalculationView(
        id = id,
        kapreKarNumberList = kapreKarNumberList,
        systemTopPadding = systemTopPadding
    )
}

@Composable
private fun CalculationView(
    id: Int,
    kapreKarNumberList: List<CalculationState>,
    systemTopPadding: Dp = 0.dp,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = kapreKarNumberList.isNotEmpty()
        ) {
            Column(
                modifier = Modifier
                    .padding(top = systemTopPadding),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.input_number, id.toString()),
                        style = typography.bodyLarge,
                        fontSize = 28.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.padding(16.dp))
                HorizontalLine()
                Spacer(modifier = Modifier.padding(16.dp))

                if (kapreKarNumberList.size > 1) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "Iterations: ${kapreKarNumberList.size}"
                        )
                    }
                    Spacer(modifier = Modifier.padding(8.dp))
                    LazyVerticalGrid(
                        modifier = Modifier.padding(start = 64.dp),
                        columns = GridCells.Adaptive(130.dp),

                        contentPadding = PaddingValues(
                            top = 16.dp,
                            end = 12.dp,
                            bottom = 16.dp
                        ),
                        content = {
                            items(kapreKarNumberList.size) { index ->
                                ResultCard(
                                    calculationState = kapreKarNumberList[index],
                                    isLastResult = index == kapreKarNumberList.size - 1
                                )
                            }
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                       ResultCard(calculationState = kapreKarNumberList[0], isLastResult = true)
                    }
                }
            }
        }
    }
}

@Composable
fun HorizontalLine(
    color: Color = Color.Gray,
    width: Dp = Dp.Unspecified
) {
    HorizontalDivider(
        modifier = Modifier.width(width),
        thickness = 2.dp,
        color = color
    )
}

@Composable
fun ResultCard(
    calculationState: CalculationState,
    isLastResult: Boolean = false
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Card(
            modifier = Modifier
                .shadow(elevation = 4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = KapreKarTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            )

            ) {
            Column(
                modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = calculationState.numberDescending.toString(),
                    color = KapreKarTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = "- " + calculationState.numberAscending.toString(),
                    color = KapreKarTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End
                )
                HorizontalLine(color = Color.LightGray)
                Text(
                    text = calculationState.result.toString(),
                    fontSize = if (isLastResult) 24.sp else 20.sp,
                    color = if (isLastResult) KapreKarTheme.colorScheme.primary else KapreKarTheme.colorScheme.onSecondary,
                    textAlign = TextAlign.End
                )
                HorizontalLine(color = Color.LightGray)
                Spacer(modifier = Modifier.padding(top = 4.dp))
            }
        }
        if (!isLastResult) {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .size(32.dp),
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                tint = KapreKarTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}

@Composable
@PreviewLightDark
fun CalculationViewPreview() {
    KapreKarTheme {
        val id = 1234
        val kapreKarNumberList =
            listOf(CalculationState(1234, 4321, 3087), CalculationState(2358, 8532, 6174))
        CalculationView(id = id, kapreKarNumberList = kapreKarNumberList)
    }
}


@Composable
@PreviewLightDark
fun ResultCardPreview() {
    KapreKarTheme {
        ResultCard(calculationState = CalculationState(1234, 4321, 3087))

    }
}