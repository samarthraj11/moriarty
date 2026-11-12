package com.moriartystudio.moriarty.feature.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moriartystudio.moriarty.app.navigation.RootDestination
import com.moriartystudio.moriarty.core.designsystem.MoriartyBottomBar
import com.moriartystudio.moriarty.core.designsystem.MoriartyPage
import com.moriartystudio.moriarty.core.designsystem.MoriartyPrimaryButton
import com.moriartystudio.moriarty.core.designsystem.rememberAppVersion
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onSettings: () -> Unit, onRoot: (RootDestination) -> Unit) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) { viewModel.refresh() }
    viewModel.collectSideEffect {
        when (it) {
            ProfileSideEffect.OpenSettings -> onSettings()
            ProfileSideEffect.LoggedOut -> Toast.makeText(context, "Logged out", Toast.LENGTH_SHORT).show()
            ProfileSideEffect.ProgressReset -> Toast.makeText(context, "Progress reset", Toast.LENGTH_SHORT).show()
        }
    }
    val profile = state.profile ?: return
    MoriartyPage(title = "Profile", subtitle = "You and your case file", bottomBar = { MoriartyBottomBar(RootDestination.Profile, onRoot) }) {
        ProfileContent(
            name = profile.name,
            solved = profile.solved,
            version = rememberAppVersion(),
            onSettings = viewModel::settings,
            onBackUp = {},
            onReset = viewModel::askReset,
            onLogout = viewModel::askLogout,
        )
    }
    if (state.confirmReset) {
        AlertDialog(
            onDismissRequest = viewModel::dismissReset,
            title = { Text("Reset progress?") },
            text = { Text("This erases every solve, point and streak on this phone. It can't be undone.") },
            confirmButton = { TextButton(viewModel::reset) { Text("Reset", color = ErrorCoral) } },
            dismissButton = { TextButton(viewModel::dismissReset) { Text("Cancel") } },
        )
    }
    if (state.confirmLogout) {
        AlertDialog(
            onDismissRequest = viewModel::dismissLogout,
            title = { Text("Log out?") },
            text = { Text("Your progress stays on this phone.") },
            confirmButton = { TextButton(viewModel::logout) { Text("Log out", color = ErrorCoral) } },
            dismissButton = { TextButton(viewModel::dismissLogout) { Text("Cancel") } },
        )
    }
}

@Composable
internal fun ProfileContent(
    name: String,
    solved: Int,
    version: String,
    onSettings: () -> Unit,
    onBackUp: () -> Unit,
    onReset: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Avatar()
        Spacer(Modifier.height(14.dp))
        Text(name, style = MaterialTheme.typography.headlineMedium, color = Ink)
        Text("Guest · progress lives on this phone", style = MaterialTheme.typography.bodyMedium, color = InkMuted)
        Spacer(Modifier.height(22.dp))
        BackupCard(solved, onBackUp)
        Spacer(Modifier.height(26.dp))
        Column(Modifier.fillMaxWidth()) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp), color = Ink, modifier = Modifier.padding(bottom = 6.dp))
            SettingRow(Icons.Outlined.VolumeUp, "Sound and haptics", onSettings)
            SettingRow(Icons.Outlined.Accessibility, "Accessibility", onSettings)
            SettingRow(Icons.Outlined.SportsEsports, "Gameplay", onSettings)
            SettingRow(Icons.Outlined.RestartAlt, "Reset progress", onReset, color = ErrorCoral, chevron = false, divider = false)
        }
        Spacer(Modifier.height(28.dp))
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, ErrorCoral),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorCoral),
        ) {
            Icon(Icons.AutoMirrored.Outlined.Logout, null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text("Log out", style = MaterialTheme.typography.titleMedium)
        }
        Spacer(Modifier.height(18.dp))
        Text("Moriarty · Version $version", style = MaterialTheme.typography.bodyMedium, color = InkMuted, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
    }
}

/** The mark initial on indigo, wrapped in a thin gold ring like the Progress stamp. */
@Composable
private fun Avatar() {
    Box(Modifier.size(92.dp).border(2.5.dp, Gold, CircleShape).padding(6.dp).background(Indigo, CircleShape), contentAlignment = Alignment.Center) {
        Text("M", style = MaterialTheme.typography.displaySmall, color = Color.White)
    }
}

@Composable
private fun BackupCard(solved: Int, onBackUp: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Paper, RoundedCornerShape(22.dp)).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(38.dp).background(CategoryDeduction.copy(alpha = 0.16f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CloudOff, null, tint = CategoryDeduction, modifier = Modifier.size(21.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Not backed up", style = MaterialTheme.typography.titleLarge, color = Ink)
                Text(
                    if (solved > 0) "Lose the phone, lose $solved ${if (solved == 1) "solve" else "solves"}." else "Back up so your progress follows you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = InkMuted,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        MoriartyPrimaryButton("Continue with Google", onBackUp)
    }
}

@Composable
private fun SettingRow(icon: ImageVector, label: String, onClick: () -> Unit, color: Color = Ink, chevron: Boolean = true, divider: Boolean = true) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth().padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (color == Ink) Indigo else color, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(14.dp))
            Text(label, style = MaterialTheme.typography.titleMedium, color = color, modifier = Modifier.weight(1f))
            if (chevron) Icon(Icons.Outlined.ChevronRight, null, tint = InkMuted)
        }
        if (divider) HorizontalDivider(color = Line)
    }
}
