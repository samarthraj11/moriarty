package com.moriartystudio.moriarty.core.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.ui.theme.*

@Composable
fun MoriartyPrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, trailing: @Composable (() -> Unit)? = null) {
    Button(onClick = onClick, enabled = enabled, modifier = modifier.fillMaxWidth().height(58.dp), shape = RoundedCornerShape(22.dp), colors = ButtonDefaults.buttonColors(containerColor = Emerald, contentColor = Color.White, disabledContainerColor = Color(0xFFD9DDDA), disabledContentColor = Color(0xFF8D9892))) {
        Text(text, style = MaterialTheme.typography.titleMedium)
        if (trailing != null) { Spacer(Modifier.size(10.dp)); trailing() }
    }
}

@Composable
fun MoriartySecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick = onClick, modifier = modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Emerald), colors = ButtonDefaults.outlinedButtonColors(contentColor = Emerald)) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun SectionTitle(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) Text(action, style = MaterialTheme.typography.labelLarge, color = Emerald, modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onAction).padding(8.dp))
    }
}

@Composable
fun DifficultyPill(text: String, color: Color = Mint, contentColor: Color = Emerald) {
    Text(text, color = contentColor, style = MaterialTheme.typography.labelMedium, modifier = Modifier.background(color, RoundedCornerShape(50)).padding(horizontal = 14.dp, vertical = 7.dp))
}

@Composable
fun TopBar(title: String, subtitle: String? = null, leading: (@Composable () -> Unit)? = null, trailing: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        if (leading != null) { leading(); Spacer(Modifier.size(12.dp)) }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineMedium)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
        }
        trailing?.invoke()
    }
}

@Composable
fun MoriartyBottomBar(selected: RootDestination, onSelect: (RootDestination) -> Unit) {
    val items = listOf(
        Triple(RootDestination.Levels, "Levels", Icons.Outlined.Home),
        Triple(RootDestination.Daily, "Daily", Icons.Outlined.CalendarToday),
        Triple(RootDestination.Leaderboard, "Leaderboard", Icons.Outlined.EmojiEvents),
        Triple(RootDestination.Profile, "Profile", Icons.Outlined.PersonOutline),
    )
    Column(Modifier.background(Paper).navigationBarsPadding()) {
        HorizontalDivider(color = Line)
        NavigationBar(containerColor = Paper, tonalElevation = 0.dp) {
            items.forEach { (destination, label, icon) ->
                NavigationBarItem(selected = selected == destination, onClick = { onSelect(destination) }, icon = { Icon(icon, null) }, label = { Text(label, style = MaterialTheme.typography.labelMedium) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Emerald, selectedTextColor = Emerald, indicatorColor = Mint, unselectedIconColor = InkMuted, unselectedTextColor = InkMuted))
            }
        }
    }
}

@Composable
fun IconCircle(icon: ImageVector, onClick: () -> Unit, contentDescription: String) {
    Surface(modifier = Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onClick), shape = CircleShape, color = Paper, border = BorderStroke(1.dp, Line)) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription, tint = Ink, modifier = Modifier.size(21.dp)) }
    }
}

@Composable
fun Metric(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Ink)
        Text(label, style = MaterialTheme.typography.bodyMedium, color = InkMuted)
    }
}

@Composable
fun ScreenBackground(content: @Composable () -> Unit) = Surface(color = Ivory, contentColor = Ink, content = content)
