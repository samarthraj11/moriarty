package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.moriartystudio.moriarty.R
import com.moriartystudio.moriarty.ui.theme.BitterFamily

/** The circular jigsaw icon with its M tablet. */
@Composable
fun MoriartyMark(modifier: Modifier = Modifier, contentDescription: String? = null) {
    Image(painterResource(R.drawable.ic_moriarty_mark), contentDescription, modifier)
}

/** The full logo: the icon above the Moriarty wordmark (set in Bitter, the app's headline face). */
@Composable
fun MoriartyLogo(modifier: Modifier = Modifier, markSize: Dp = 72.dp) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        MoriartyMark(Modifier.size(markSize))
        Spacer(Modifier.height(markSize / 6))
        Text(
            "Moriarty",
            color = Color(0xFF292044),
            fontFamily = BitterFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = (markSize.value * 0.36f).sp,
            letterSpacing = (-0.5).sp,
        )
    }
}
