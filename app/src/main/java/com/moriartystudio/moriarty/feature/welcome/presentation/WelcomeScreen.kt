package com.moriartystudio.moriarty.feature.welcome.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.R
import com.moriartystudio.moriarty.core.designsystem.MoriartyLogo
import com.moriartystudio.moriarty.ui.theme.*

private val WelcomeTop = Color(0xFFF7F4FF)
private val WelcomeBottom = Color(0xFFEDE8FF)
private val Blob = Color(0xFFDCD3FB)

/** First screen on a fresh install: brand, one line of promise, then straight to signing in. */
@Composable
fun WelcomeScreen(onGoogle: () -> Unit, onEmail: () -> Unit, onGuest: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(WelcomeTop, WelcomeBottom)))
            .drawBehind {
                // Soft corner blobs, top-left and bottom-right.
                drawCircle(Blob.copy(alpha = 0.7f), 130.dp.toPx(), Offset(-10.dp.toPx(), -20.dp.toPx()))
                drawCircle(Blob.copy(alpha = 0.8f), 120.dp.toPx(), Offset(size.width + 10.dp.toPx(), size.height + 10.dp.toPx()))
            },
    ) {
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            MoriartyLogo(markSize = 78.dp)
            Spacer(Modifier.height(22.dp))
            Text("Continue your", style = Headline, color = Night, textAlign = TextAlign.Center)
            Text(
                "puzzle journey.",
                style = Headline,
                color = Night,
                modifier = Modifier.drawBehind {
                    // Highlighter stroke under the second line.
                    drawRoundRect(
                        GoldLight, Offset(-4.dp.toPx(), size.height * 0.6f), Size(size.width + 8.dp.toPx(), size.height * 0.3f), CornerRadius(8.dp.toPx()),
                    )
                },
            )
            Image(
                painterResource(R.drawable.detective_set),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 6.dp),
            )
            AuthButton(onGoogle) {
                Image(painterResource(R.drawable.ic_google_g), null, Modifier.size(24.dp))
                Spacer(Modifier.width(14.dp))
                Text("Continue with Google", style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(Modifier.weight(1f), color = Blob)
                Text("or", style = MaterialTheme.typography.titleMedium, color = Indigo, modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(Modifier.weight(1f), color = Blob)
            }
            AuthButton(onEmail, outlined = true) {
                Icon(Icons.Outlined.MailOutline, null, tint = Indigo, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(14.dp))
                Text("Continue with Email", style = MaterialTheme.typography.titleMedium, color = Ink)
            }
            // No accounts exist yet, so guest play is the way into the app for now.
            Text(
                "Play as guest",
                style = MaterialTheme.typography.titleMedium,
                color = Indigo,
                modifier = Modifier.padding(top = 6.dp).clip(RoundedCornerShape(14.dp)).clickable(onClick = onGuest).padding(horizontal = 20.dp, vertical = 12.dp),
            )
            Text(
                buildAnnotatedString {
                    append("By continuing, you agree to our\n")
                    val link = SpanStyle(color = Indigo, fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline)
                    withStyle(link) { append("Terms of Service") }
                    append(" and ")
                    withStyle(link) { append("Privacy Policy.") }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = InkMuted,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

private val Headline @Composable get() = MaterialTheme.typography.displaySmall.copy(fontSize = 34.sp, lineHeight = 40.sp, textAlign = TextAlign.Center)

@Composable
private fun AuthButton(onClick: () -> Unit, outlined: Boolean = false, content: @Composable RowScope.() -> Unit) {
    val shape = RoundedCornerShape(50)
    Surface(
        onClick = onClick,
        shape = shape,
        color = if (outlined) Color.White.copy(alpha = 0.45f) else Color.White,
        border = if (outlined) BorderStroke(1.5.dp, Blob) else null,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .then(if (outlined) Modifier else Modifier.shadow(10.dp, shape, ambientColor = Lavender, spotColor = Lavender)),
    ) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, content = content)
    }
}
