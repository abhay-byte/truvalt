package com.ivarna.truvalt.presentation.ui.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ivarna.truvalt.R
import com.ivarna.truvalt.core.utils.PasswordGenerator
import com.ivarna.truvalt.core.utils.PasswordStrength
import com.ivarna.truvalt.core.utils.PasswordStrengthMeter
import com.ivarna.truvalt.presentation.ui.vault.VaultHomePalette
import com.ivarna.truvalt.presentation.ui.vault.rememberVaultPalette
import com.ivarna.truvalt.presentation.ui.shared.TruvaltTopAppBar
import kotlinx.coroutines.launch

private data class GeneratorMode(
    val id: Int,
    val label: String
)

private data class ToggleOption(
    val label: String,
    val leading: String,
    val checked: Boolean,
    val onToggle: (Boolean) -> Unit
)

@Composable
fun GeneratorScreen(
    onNavigateBack: () -> Unit
) {
    val passwordGenerator = remember { PasswordGenerator() }
    val strengthMeter = remember { PasswordStrengthMeter() }
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val palette = rememberVaultPalette()
    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }

    var selectedMode by remember { mutableIntStateOf(0) }
    var generatedPassword by remember { mutableStateOf("") }

    var passwordLength by remember { mutableIntStateOf(16) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var excludeAmbiguous by remember { mutableStateOf(false) }

    var passphraseWordCount by remember { mutableIntStateOf(4) }
    var passphraseCount by remember { mutableIntStateOf(1) }
    var generatedPassphrases by remember { mutableStateOf(emptyList<String>()) }

    fun regeneratePassword() {
        generatedPassword = passwordGenerator.generate(
            length = passwordLength,
            useUppercase = useUppercase,
            useLowercase = useLowercase,
            useDigits = useDigits,
            useSymbols = useSymbols,
            excludeAmbiguous = excludeAmbiguous
        )
    }

    fun regeneratePassphrases() {
        generatedPassphrases = List(passphraseCount) {
            passwordGenerator.generatePassphrase(wordCount = passphraseWordCount)
        }
        generatedPassword = generatedPassphrases.firstOrNull().orEmpty()
    }

    if (generatedPassword.isEmpty()) {
        if (selectedMode == 0) regeneratePassword() else regeneratePassphrases()
    }

    val displayedValue = if (selectedMode == 0) {
        generatedPassword
    } else {
        generatedPassphrases.joinToString("\n")
    }
    val strength = remember(generatedPassword, selectedMode) {
        strengthMeter.calculate(generatedPassword)
    }
    val modes = listOf(
        GeneratorMode(0, "Password"),
        GeneratorMode(1, "Passphrase")
    )

    val toggleOptions = listOf(
        ToggleOption("Uppercase", "A-Z", useUppercase) {
            useUppercase = it
            regeneratePassword()
        },
        ToggleOption("Lowercase", "a-z", useLowercase) {
            useLowercase = it
            regeneratePassword()
        },
        ToggleOption("Numbers", "0-9", useDigits) {
            useDigits = it
            regeneratePassword()
        },
        ToggleOption("Symbols", "!@#", useSymbols) {
            useSymbols = it
            regeneratePassword()
        }
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(palette.background, palette.backgroundAccent)
                    )
                )
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .size(360.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 40.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(palette.heroGlow, Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                TruvaltTopAppBar(
                    title = "Generator",
                    palette = palette,
                    photoUrl = firebaseUser?.photoUrl?.toString(),
                    profileFallback = firebaseUser?.displayName
                        ?.firstOrNull()
                        ?.uppercase()
                        ?: firebaseUser?.email?.firstOrNull()?.uppercase()
                        ?: "T"
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    item {
                        ModeSelector(
                        palette = palette,
                        modes = modes,
                        selectedMode = selectedMode,
                        onSelect = { mode ->
                            selectedMode = mode
                            if (selectedMode == 0) regeneratePassword() else regeneratePassphrases()
                        }
                    )
                }

                item {
                    GeneratedValueCard(
                        palette = palette,
                        title = if (selectedMode == 0) "Generated Secure Key" else "Generated Passphrase",
                        value = displayedValue,
                        strength = strength,
                        onRefresh = {
                            if (selectedMode == 0) regeneratePassword() else regeneratePassphrases()
                        }
                    )
                }

                if (selectedMode == 0) {
                    item {
                        LengthCard(
                            palette = palette,
                            label = "Character Length",
                            value = passwordLength,
                            valueRange = 8f..64f,
                            steps = 55,
                            onValueChange = {
                                passwordLength = it.toInt()
                                regeneratePassword()
                            }
                        )
                    }

                    items(toggleOptions) { option ->
                        ToggleCard(
                            palette = palette,
                            label = option.label,
                            leading = option.leading,
                            checked = option.checked,
                            onCheckedChange = option.onToggle
                        )
                    }

                    item {
                        AdvancedOptionCard(
                            palette = palette,
                            label = "Avoid ambiguous",
                            subtitle = "Removes 0, O, 1, l, and I from generated passwords",
                            checked = excludeAmbiguous,
                            onCheckedChange = {
                                excludeAmbiguous = it
                                regeneratePassword()
                            }
                        )
                    }
                } else {
                    item {
                        LengthCard(
                            palette = palette,
                            label = "Words Per Phrase",
                            value = passphraseWordCount,
                            valueRange = 3f..8f,
                            steps = 4,
                            onValueChange = {
                                passphraseWordCount = it.toInt()
                                regeneratePassphrases()
                            }
                        )
                    }

                    item {
                        LengthCard(
                            palette = palette,
                            label = "Phrase Count",
                            value = passphraseCount,
                            valueRange = 1f..5f,
                            steps = 3,
                            onValueChange = {
                                passphraseCount = it.toInt()
                                regeneratePassphrases()
                            }
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(displayedValue))
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    if (selectedMode == 0) "Password copied" else "Passphrase copied"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .navigationBarsPadding(),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(palette.chipSelectedSurface, palette.fabGradientEnd)
                                    ),
                                    RoundedCornerShape(22.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = palette.chipSelectedText
                                )
                                Text(
                                    text = "Copy & Use",
                                    color = palette.chipSelectedText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                        }
                    }
                }
            }
        }
    }
}
}
}

@Composable
private fun ModeSelector(
    palette: VaultHomePalette,
    modes: List<GeneratorMode>,
    selectedMode: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        modes.forEach { mode ->
            val isSelected = mode.id == selectedMode
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(mode.id) },
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) palette.chipSelectedSurface else palette.cardSurface
            ) {
                Text(
                    text = mode.label,
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = if (isSelected) palette.chipSelectedText else palette.body,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun GeneratedValueCard(
    palette: VaultHomePalette,
    title: String,
    value: String,
    strength: PasswordStrength,
    onRefresh: () -> Unit
) {
    val progress = when (strength) {
        PasswordStrength.VERY_WEAK -> 0.25f
        PasswordStrength.WEAK -> 0.5f
        PasswordStrength.MEDIUM -> 0.7f
        PasswordStrength.STRONG -> 0.9f
        PasswordStrength.VERY_STRONG -> 1f
    }
    val strengthLabel = when (strength) {
        PasswordStrength.VERY_WEAK -> "FRAGILE"
        PasswordStrength.WEAK -> "WEAK"
        PasswordStrength.MEDIUM -> "SOLID"
        PasswordStrength.STRONG -> "STRONG"
        PasswordStrength.VERY_STRONG -> "ELITE"
    }
    val entropyLabel = when (strength) {
        PasswordStrength.VERY_WEAK -> "Entropy: Low"
        PasswordStrength.WEAK -> "Entropy: Fair"
        PasswordStrength.MEDIUM -> "Entropy: Strong"
        PasswordStrength.STRONG -> "Entropy: High"
        PasswordStrength.VERY_STRONG -> "Entropy: Maximum"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    color = palette.muted,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 4.sp
                )
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(palette.mutedSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Regenerate",
                        tint = palette.brand
                    )
                }
            }

            Text(
                text = if (value.isBlank()) "Generating..." else value + "_",
                color = palette.title,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 50.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = entropyLabel.uppercase(),
                        color = palette.healthAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Strength: $strengthLabel",
                        color = palette.healthAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(4) { index ->
                        val active = progress >= ((index + 1) / 4f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (active) palette.healthAccent else palette.healthTrack)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LengthCard(
    palette: VaultHomePalette,
    label: String,
    value: Int,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = palette.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value.toString(),
                    color = palette.brand,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Slider(
                value = value.toFloat(),
                onValueChange = onValueChange,
                valueRange = valueRange,
                steps = steps,
                colors = SliderDefaults.colors(
                    thumbColor = palette.chipSelectedSurface,
                    activeTrackColor = palette.brand,
                    inactiveTrackColor = palette.healthTrack
                )
            )
        }
    }
}

@Composable
private fun ToggleCard(
    palette: VaultHomePalette,
    label: String,
    leading: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(26.dp),
        color = palette.cardSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 58.dp, height = 44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.mutedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = leading,
                        color = palette.brand,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = label,
                    color = palette.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = palette.background,
                    checkedTrackColor = palette.chipSelectedSurface,
                    uncheckedThumbColor = palette.muted,
                    uncheckedTrackColor = palette.healthTrack,
                    uncheckedBorderColor = Color.Transparent,
                    checkedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun AdvancedOptionCard(
    palette: VaultHomePalette,
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = palette.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = palette.background,
                        checkedTrackColor = palette.chipSelectedSurface,
                        uncheckedThumbColor = palette.muted,
                        uncheckedTrackColor = palette.healthTrack,
                        uncheckedBorderColor = Color.Transparent,
                        checkedBorderColor = Color.Transparent
                    )
                )
            }
            HorizontalDivider(color = palette.cardBorder)
            Text(
                text = subtitle,
                color = palette.muted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
