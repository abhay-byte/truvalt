package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

private enum class AuthIntent {
    LOGIN,
    REGISTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSetupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToVault: () -> Unit,
    viewModel: ServerSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var serverUrl by remember { mutableStateOf("") }
    var useCloudMode by remember { mutableStateOf(false) }
    var currentIntent by remember { mutableStateOf(AuthIntent.REGISTER) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            when (currentIntent) {
                AuthIntent.LOGIN -> onNavigateToLogin()
                AuthIntent.REGISTER -> onNavigateToRegister()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = AuthBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Server Settings", 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold,
                        color = AuthOnSurface
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AuthBackground
                )
            )
        },
        bottomBar = {
            // High-fidelity Glassmorphism Bottom Nav
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(32.dp, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), spotColor = Color(0x0F33313A)),
                color = AuthBackground.copy(alpha = 0.9f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Row(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AuthTabItem(
                        icon = Icons.Default.PersonAdd,
                        label = "Sign Up",
                        isSelected = currentIntent == AuthIntent.REGISTER,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            currentIntent = AuthIntent.REGISTER
                            viewModel.saveServerConfig(serverUrl, false, useFirebaseCloud = useCloudMode)
                        }
                    )
                    AuthTabItem(
                        icon = Icons.Default.Login,
                        label = "Log In",
                        isSelected = currentIntent == AuthIntent.LOGIN,
                        modifier = Modifier.weight(1f),
                        onClick = { 
                            currentIntent = AuthIntent.LOGIN
                            viewModel.saveServerConfig(serverUrl, false, useFirebaseCloud = useCloudMode)
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Header Section
            Text(
                text = "Connect to your vault",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthOnSurface,
                letterSpacing = (-1).sp,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Enter your self-hosted server URL or use cloud mode.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AuthOnSurfaceVariant,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Server URL Input with specific focus behavior
            AuthTextField(
                value = serverUrl,
                onValueChange = { serverUrl = it },
                label = "Server URL",
                placeholder = "https://vault.example.com",
                modifier = Modifier.fillMaxWidth().alpha(if (useCloudMode) 0.5f else 1f)
            )
            Text(
                text = "Leave blank to use Truvalt Cloud",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AuthOnSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // "OR" Divider
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = AuthSurfaceContainerHighest)
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = AuthOutlineVariant,
                    letterSpacing = 2.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = AuthSurfaceContainerHighest)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Use Cloud Mode Toggle Card
            Surface(
                onClick = { useCloudMode = !useCloudMode },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                color = if (useCloudMode) AuthSurfaceContainerHighest else AuthSurfaceContainerLow,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Use cloud mode",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AuthOnSurface
                        )
                        Text(
                            text = "Recommended for most users",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AuthPrimary
                        )
                    }
                    Checkbox(
                        checked = useCloudMode,
                        onCheckedChange = { useCloudMode = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AuthPrimary,
                            uncheckedColor = AuthOutlineVariant
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Graphic Detail Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(192.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(AuthSurfaceContainerLow)
            ) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDgQEJobdZ7Bi7w9hjSRwZrW7B2J1ppYvdBIdbIaQzH6Yftt8gxN-cEUpv9z_EEpNxgJKReAB8VCk5FpPHl1ys2EQSaoqESgKKWXokvwM90_S2HdNNB-CA8TdlyuLginYgJyjKzmvmpkwwpSr_11LKPmjzAWIjpupfH0GxSFRxs0WlwCPZ4SZlwulJH4Sdpb6F89yrizBpMtDj4RobUb0Bsyj04Ll2hwbIr7RPCUyKacfprSfzTdWjeJyAlKMOF-ARPyWo8idH3OxE",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().alpha(0.4f),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, AuthSurfaceContainerLow)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = AuthPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MILITARY GRADE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = AuthPrimary,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your vault keys never leave your device.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuthOnSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AuthTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) AuthPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else AuthOnSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else AuthOnSurfaceVariant,
                letterSpacing = 1.sp
            )
        }
    }
}
