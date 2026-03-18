package com.ivarna.truvalt.presentation.ui.generator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.ivarna.truvalt.core.utils.PasswordGenerator
import com.ivarna.truvalt.core.utils.PasswordStrength
import com.ivarna.truvalt.core.utils.PasswordStrengthMeter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    onNavigateBack: () -> Unit
) {
    val passwordGenerator = remember { PasswordGenerator() }
    val strengthMeter = remember { PasswordStrengthMeter() }
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }

    var generatedPassword by remember { mutableStateOf(passwordGenerator.generate()) }
    var passwordLength by remember { mutableIntStateOf(16) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var excludeAmbiguous by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf(0) }

    val strength = remember(generatedPassword) { strengthMeter.calculate(generatedPassword) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Password Generator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = selectedMode == 0,
                    onClick = { 
                        selectedMode = 0
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols,
                            excludeAmbiguous = excludeAmbiguous
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Password")
                }
                SegmentedButton(
                    selected = selectedMode == 1,
                    onClick = { 
                        selectedMode = 1
                        generatedPassword = passwordGenerator.generatePassphrase(wordCount = 4)
                    },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Passphrase")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = generatedPassword,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { when (strength) {
                            PasswordStrength.VERY_WEAK -> 0.2f
                            PasswordStrength.WEAK -> 0.4f
                            PasswordStrength.MEDIUM -> 0.6f
                            PasswordStrength.STRONG -> 0.8f
                            PasswordStrength.VERY_STRONG -> 1.0f
                        }},
                        modifier = Modifier.fillMaxWidth(),
                        color = androidx.compose.ui.graphics.Color(strength.color),
                    )
                    
                    Text(
                        text = strength.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = androidx.compose.ui.graphics.Color(strength.color)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { 
                            generatedPassword = if (selectedMode == 0) {
                                passwordGenerator.generate(
                                    length = passwordLength,
                                    useUppercase = useUppercase,
                                    useLowercase = useLowercase,
                                    useDigits = useDigits,
                                    useSymbols = useSymbols,
                                    excludeAmbiguous = excludeAmbiguous
                                )
                            } else {
                                passwordGenerator.generatePassphrase(wordCount = 4)
                            }
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Regenerate")
                        }
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(generatedPassword))
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            }

            if (selectedMode == 0) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Length: $passwordLength",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Slider(
                            value = passwordLength.toFloat(),
                            onValueChange = { 
                                passwordLength = it.toInt()
                                generatedPassword = passwordGenerator.generate(
                                    length = passwordLength,
                                    useUppercase = useUppercase,
                                    useLowercase = useLowercase,
                                    useDigits = useDigits,
                                    useSymbols = useSymbols,
                                    excludeAmbiguous = excludeAmbiguous
                                )
                            },
                            valueRange = 8f..128f,
                            steps = 119
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CheckboxOption("Uppercase (A-Z)", useUppercase) { useUppercase = it
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols,
                                excludeAmbiguous = excludeAmbiguous
                            )
                        }
                        CheckboxOption("Lowercase (a-z)", useLowercase) { useLowercase = it
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols,
                                excludeAmbiguous = excludeAmbiguous
                            )
                        }
                        CheckboxOption("Digits (0-9)", useDigits) { useDigits = it
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols,
                                excludeAmbiguous = excludeAmbiguous
                            )
                        }
                        CheckboxOption("Symbols (!@#\$%)", useSymbols) { useSymbols = it
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols,
                                excludeAmbiguous = excludeAmbiguous
                            )
                        }

                        CheckboxOption("Exclude ambiguous (0O1lI)", excludeAmbiguous) { excludeAmbiguous = it
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols,
                                excludeAmbiguous = excludeAmbiguous
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    clipboardManager.setText(AnnotatedString(generatedPassword))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Text("Copy to Clipboard", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun CheckboxOption(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
