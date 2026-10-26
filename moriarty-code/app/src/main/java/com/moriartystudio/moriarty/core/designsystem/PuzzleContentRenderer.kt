package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.model.PuzzleContent
import com.moriartystudio.moriarty.core.model.PuzzleAnswerVisual
import com.moriartystudio.moriarty.core.model.ShapeKind
import com.moriartystudio.moriarty.core.model.VisualToken
import com.moriartystudio.moriarty.ui.theme.Amber
import com.moriartystudio.moriarty.ui.theme.Emerald
import com.moriartystudio.moriarty.ui.theme.EmeraldDark
import com.moriartystudio.moriarty.ui.theme.Ink
import com.moriartystudio.moriarty.ui.theme.InkMuted
import com.moriartystudio.moriarty.ui.theme.Ivory
import com.moriartystudio.moriarty.ui.theme.Line
import com.moriartystudio.moriarty.ui.theme.Mint
import com.moriartystudio.moriarty.ui.theme.Paper
import com.moriartystudio.moriarty.ui.theme.Peach
import com.moriartystudio.moriarty.ui.theme.SoftAmber
import com.moriartystudio.moriarty.ui.theme.SoftPeach

@Composable
fun PuzzleContentRenderer(
    content: PuzzleContent,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (compact) 22.dp else 30.dp),
        color = Paper,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF4DD))
                .padding(if (compact) 14.dp else 20.dp),
            contentAlignment = Alignment.Center,
        ) {
            when (content) {
                is PuzzleContent.ShapeSequence -> ShapeSequence(content, compact)
                is PuzzleContent.NumberSequence -> NumberSequence(content, compact)
                is PuzzleContent.Matrix -> ShapeMatrix(content, compact)
                is PuzzleContent.RotationSequence -> RotationSequence(content, compact)
                is PuzzleContent.ClueList -> ClueList(content.clues, compact)
                is PuzzleContent.Equations -> EquationList(content.rows, compact)
                is PuzzleContent.OddOneOut -> OddOneOut(content, compact)
                is PuzzleContent.RouteGrid -> RouteGrid(content)
                is PuzzleContent.Statements -> ClueList(content.statements, compact, showLetters = true)
            }
        }
    }
}

@Composable
fun PuzzleAnswerVisualRenderer(
    visual: PuzzleAnswerVisual,
    modifier: Modifier = Modifier,
) {
    when (visual) {
        is PuzzleAnswerVisual.Token -> ShapeGlyph(visual.token, modifier)
        is PuzzleAnswerVisual.TokenGroup -> Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            visual.tokens.forEach { token -> ShapeGlyph(token, Modifier.size(30.dp)) }
        }
    }
}

@Composable
private fun ShapeSequence(content: PuzzleContent.ShapeSequence, compact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content.tokens.forEach { token -> TokenTile(token, if (compact) 44 else 54) }
    }
}

@Composable
private fun NumberSequence(content: PuzzleContent.NumberSequence, compact: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 9.dp, Alignment.CenterHorizontally),
    ) {
        content.values.forEachIndexed { index, value ->
            Box(
                modifier = Modifier
                    .size(if (compact) 43.dp else 54.dp)
                    .background(if (value == "?") Mint else Ivory, RoundedCornerShape(14.dp))
                    .border(1.dp, if (value == "?") Emerald else Line, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(value, style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            if (index < content.values.lastIndex) Text("·", color = InkMuted, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

@Composable
private fun ShapeMatrix(content: PuzzleContent.Matrix, compact: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)) {
        content.tokens.chunked(content.columns).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 7.dp)) {
                row.forEach { token -> TokenTile(token, if (compact) 42 else 56) }
            }
        }
    }
}

@Composable
private fun RotationSequence(content: PuzzleContent.RotationSequence, compact: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 12.dp)) {
        content.rotations.forEach { rotation ->
            TokenTile(rotation?.let { VisualToken(ShapeKind.Arrow, it) }, if (compact) 48 else 62)
        }
    }
}

@Composable
private fun ClueList(clues: List<String>, compact: Boolean, showLetters: Boolean = false) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 7.dp else 10.dp),
    ) {
        clues.forEachIndexed { index, clue ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Ivory, RoundedCornerShape(14.dp))
                    .padding(horizontal = 13.dp, vertical = if (compact) 9.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(28.dp).background(Mint, CircleShape), contentAlignment = Alignment.Center) {
                    Text(if (showLetters) ('A' + index).toString() else "${index + 1}", color = EmeraldDark, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text(clue, style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge, color = Ink)
            }
        }
    }
}

@Composable
private fun EquationList(rows: List<String>, compact: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        rows.forEachIndexed { index, row ->
            Text(
                row,
                modifier = Modifier
                    .background(if (index == rows.lastIndex) Mint else Ivory, RoundedCornerShape(14.dp))
                    .padding(horizontal = 22.dp, vertical = if (compact) 8.dp else 11.dp),
                style = if (compact) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun OddOneOut(content: PuzzleContent.OddOneOut, compact: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        content.groups.chunked(2).forEachIndexed { rowIndex, groups ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                groups.forEachIndexed { columnIndex, group ->
                    val label = ('A'.code + rowIndex * 2 + columnIndex).toChar().toString()
                    Column(
                        modifier = Modifier
                            .background(Ivory, RoundedCornerShape(16.dp))
                            .padding(if (compact) 8.dp else 11.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(label, color = InkMuted, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            group.forEach { ShapeGlyph(it, Modifier.size(if (compact) 27.dp else 34.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TokenTile(token: VisualToken?, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(if (token == null) Mint else Ivory, RoundedCornerShape(16.dp))
            .border(1.dp, if (token == null) Emerald else Line, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center,
    ) {
        if (token == null) Text("?", style = MaterialTheme.typography.titleLarge, color = Emerald, fontWeight = FontWeight.Bold)
        else ShapeGlyph(token, Modifier.size((size * .58f).dp))
    }
}

@Composable
private fun ShapeGlyph(token: VisualToken, modifier: Modifier) {
    val colors = listOf(Emerald, Peach, Amber, Color(0xFF78BDA5))
    val color = colors[token.accentIndex.mod(colors.size)]
    Canvas(modifier) {
        rotate(token.rotationDegrees) {
            val center = Offset(size.width / 2f, size.height / 2f)
            when (token.shape) {
                ShapeKind.Circle -> drawCircle(color, size.minDimension * .38f, center)
                ShapeKind.Square -> drawRoundRect(
                    color,
                    Offset(size.width * .17f, size.height * .17f),
                    Size(size.width * .66f, size.height * .66f),
                    CornerRadius(size.minDimension * .12f),
                )
                ShapeKind.Triangle -> drawPath(Path().apply {
                    moveTo(center.x, size.height * .10f)
                    lineTo(size.width * .90f, size.height * .84f)
                    lineTo(size.width * .10f, size.height * .84f)
                    close()
                }, color)
                ShapeKind.Diamond -> drawPath(Path().apply {
                    moveTo(center.x, size.height * .07f)
                    lineTo(size.width * .93f, center.y)
                    lineTo(center.x, size.height * .93f)
                    lineTo(size.width * .07f, center.y)
                    close()
                }, color)
                ShapeKind.Arrow -> {
                    drawLine(color, Offset(center.x, size.height * .86f), Offset(center.x, size.height * .17f), size.width * .16f, StrokeCap.Round)
                    drawLine(color, Offset(center.x, size.height * .17f), Offset(size.width * .24f, size.height * .43f), size.width * .14f, StrokeCap.Round)
                    drawLine(color, Offset(center.x, size.height * .17f), Offset(size.width * .76f, size.height * .43f), size.width * .14f, StrokeCap.Round)
                }
            }
        }
    }
}

@Composable
private fun RouteGrid(content: PuzzleContent.RouteGrid) {
    Canvas(Modifier.fillMaxSize().padding(8.dp)) {
        val cell = minOf(size.width / content.columns, size.height / content.rows)
        val gridWidth = cell * content.columns
        val gridHeight = cell * content.rows
        val origin = Offset((size.width - gridWidth) / 2f, (size.height - gridHeight) / 2f)

        repeat(content.rows) { row ->
            repeat(content.columns) { column ->
                val index = row * content.columns + column
                val topLeft = origin + Offset(column * cell, row * cell)
                drawRoundRect(
                    color = when (index) {
                        in content.blockedCells -> Ink.copy(alpha = .13f)
                        content.startCell -> Mint
                        content.endCell -> SoftPeach
                        else -> Ivory
                    },
                    topLeft = topLeft + Offset(4f, 4f),
                    size = Size(cell - 8f, cell - 8f),
                    cornerRadius = CornerRadius(cell * .14f),
                )
                drawRoundRect(
                    color = Line,
                    topLeft = topLeft + Offset(4f, 4f),
                    size = Size(cell - 8f, cell - 8f),
                    cornerRadius = CornerRadius(cell * .14f),
                    style = Stroke(2f),
                )
                val center = topLeft + Offset(cell / 2f, cell / 2f)
                if (index == content.startCell) drawCircle(Emerald, cell * .18f, center)
                if (index == content.endCell) {
                    drawPath(Path().apply {
                        moveTo(center.x, center.y - cell * .20f)
                        lineTo(center.x + cell * .20f, center.y)
                        lineTo(center.x, center.y + cell * .20f)
                        lineTo(center.x - cell * .20f, center.y)
                        close()
                    }, Peach)
                }
            }
        }
    }
}
