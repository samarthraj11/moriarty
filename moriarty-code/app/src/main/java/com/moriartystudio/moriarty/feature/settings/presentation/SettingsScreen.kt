package com.moriartystudio.moriarty.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moriartystudio.moriarty.core.designsystem.IconCircle
import com.moriartystudio.moriarty.core.designsystem.TopBar
import com.moriartystudio.moriarty.ui.theme.*
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.collectAsState(); viewModel.collectSideEffect { when (it) { SettingsSideEffect.GoBack, SettingsSideEffect.SignOut -> onBack() } }
    Scaffold(containerColor = Ivory, topBar = { TopBar("Settings", leading = { IconCircle(Icons.AutoMirrored.Outlined.ArrowBack, viewModel::back, "Back") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            SettingsGroup("Accessibility") { SettingToggle("Larger puzzle text", "Make puzzle text bigger and easier to read.", state.largerText, viewModel::largerText); SettingToggle("High contrast", "Increase contrast for clearer visuals.", state.highContrast, viewModel::contrast); SettingToggle("Reduce motion", "Minimise animations and motion effects.", state.reduceMotion, viewModel::motion); SettingToggle("Describe visual puzzles", "Play audio descriptions for visual puzzles.", state.audioDescriptions, viewModel::descriptions); SettingToggle("Colour-independent clues", "Show shapes or patterns beyond colour alone.", state.colourIndependent, viewModel::colourClues) }
            SettingsGroup("Sound") { SettingToggle("Sound effects", "Play sounds for interactions and feedback.", state.soundEnabled, viewModel::sound); SettingToggle("Haptic feedback", "Vibrate for key interactions.", state.hapticsEnabled, viewModel::haptics) }
            SettingsGroup("Gameplay") { SettingToggle("Confirm before using a hint", "Show a confirmation before spending a hint.", state.confirmHint, viewModel::confirmHint); SettingToggle("Show timer while solving", "Display how long you’ve been solving a puzzle.", state.timerVisible, viewModel::timer) }
            SettingsGroup("Account") { SettingLink("Continue with Google", "Sign in to save your progress across devices."); SettingLink("Restore progress", "Recover your saved progress."); SettingLink("Delete account", "Permanently delete your account and progress.") }
            TextButton(onClick = viewModel::signOut, modifier = Modifier.fillMaxWidth()) { Text("Sign out", color = ErrorCoral, style = MaterialTheme.typography.titleMedium) }
            Text("Moriarty 0.1.0", style = MaterialTheme.typography.bodyMedium, color = InkMuted, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { Text(title, style = MaterialTheme.typography.titleMedium, color = InkMuted); Column(Modifier.fillMaxWidth().background(Paper, RoundedCornerShape(22.dp)).padding(horizontal = 16.dp), content = content) } }
@Composable private fun SettingToggle(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) { Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkMuted) }; Switch(checked, onChecked, colors = SwitchDefaults.colors(checkedTrackColor = Emerald, checkedThumbColor = Color.White)) } }
@Composable private fun SettingLink(title: String, subtitle: String) { Row(Modifier.fillMaxWidth().clickable { }.padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleMedium); Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = InkMuted) }; Icon(Icons.Outlined.ChevronRight, null, tint = InkMuted) } }
