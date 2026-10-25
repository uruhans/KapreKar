package com.urh.kaprekar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.urh.kaprekar.ui.theme.KapreKarTheme
import kotlin.random.Random

private val mathSymbols = listOf(
    "α", "β", "γ", "δ", "π", "λ", "μ", "σ", "Σ", "Δ", "Ω", "θ", "φ",
    "∞", "∑", "∫", "√", "∂", "≈", "≠", "≤", "≥", "±", "×", "÷", "%", "∏"
)

private data class BackgroundGlyph(
    val text: String,
    val xFraction: Float,
    val yFraction: Float,
    val sizeSp: Float,
    val rotation: Float,
    val alpha: Float,
    val bold: Boolean
)

/**
 * Draws random digits and mathematical symbols scattered over the whole area.
 * Glyphs are placed on a jittered grid so they spread evenly without clumping.
 */
@Composable
fun MathBackground(
    modifier: Modifier = Modifier,
    color: Color = KapreKarTheme.colorScheme.onSurface,
    columns: Int = 6,
    rows: Int = 11,
    seed: Int = 6174
) {
    val textMeasurer = rememberTextMeasurer()

    val glyphs = remember(columns, rows, seed) {
        val random = Random(seed)
        buildList {
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    val text = if (random.nextFloat() < 0.5f) {
                        random.nextInt(0, 10).toString()
                    } else {
                        mathSymbols.random(random)
                    }
                    add(
                        BackgroundGlyph(
                            text = text,
                            xFraction = (column + 0.15f + random.nextFloat() * 0.7f) / columns,
                            yFraction = (row + 0.15f + random.nextFloat() * 0.7f) / rows,
                            sizeSp = 16f + random.nextFloat() * 30f,
                            rotation = -30f + random.nextFloat() * 60f,
                            alpha = 0.05f + random.nextFloat() * 0.12f,
                            //alpha = 0.12f + random.nextFloat() * 0.18f,
                            bold = random.nextBoolean()
                        )
                    )
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        glyphs.forEach { glyph ->
            val layout = textMeasurer.measure(
                text = glyph.text,
                style = TextStyle(
                    color = color.copy(alpha = glyph.alpha),
                    fontSize = glyph.sizeSp.sp,
                    fontWeight = if (glyph.bold) FontWeight.Bold else FontWeight.Light
                )
            )
            val center = Offset(size.width * glyph.xFraction, size.height * glyph.yFraction)
            rotate(degrees = glyph.rotation, pivot = center) {
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        center.x - layout.size.width / 2f,
                        center.y - layout.size.height / 2f
                    )
                )
            }
        }
    }
}
