package com.ivarna.truvalt.presentation.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivarna.truvalt.core.utils.PasswordGenerator

@Composable
fun PasswordGeneratorDialog(
    onDismiss: () -> Unit,
    onPasswordSelected: (String) -> Unit
) {
    val passwordGenerator = remember { PasswordGenerator() }
    var generatedPassword by remember { mutableStateOf(passwordGenerator.generate()) }
    var passwordLength by remember { mutableIntStateOf(16) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useDigits by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Generate Password") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = generatedPassword,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = {
                            generatedPassword = passwordGenerator.generate(
                                length = passwordLength,
                                useUppercase = useUppercase,
                                useLowercase = useLowercase,
                                useDigits = useDigits,
                                useSymbols = useSymbols
                            )
                        }) {
                            Icon(Icons.Default.Refresh, "Regenerate")
                        }
                    }
                )

                Text("Length: $passwordLength", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = passwordLength.toFloat(),
                    onValueChange = {
                        passwordLength = it.toInt()
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols
                        )
                    },
                    valueRange = 8f..32f,
                    steps = 23
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = useUppercase, onCheckedChange = {
                        useUppercase = it
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols
                        )
                    })
                    Text("A-Z", modifier = Modifier.padding(start = 4.dp))
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = useLowercase, onCheckedChange = {
                        useLowercase = it
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols
                        )
                    })
                    Text("a-z", modifier = Modifier.padding(start = 4.dp))
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = useDigits, onCheckedChange = {
                        useDigits = it
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols
                        )
                    })
                    Text("0-9", modifier = Modifier.padding(start = 4.dp))
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Checkbox(checked = useSymbols, onCheckedChange = {
                        useSymbols = it
                        generatedPassword = passwordGenerator.generate(
                            length = passwordLength,
                            useUppercase = useUppercase,
                            useLowercase = useLowercase,
                            useDigits = useDigits,
                            useSymbols = useSymbols
                        )
                    })
                    Text("!@#$%", modifier = Modifier.padding(start = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onPasswordSelected(generatedPassword) }) {
                Text("Use Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
