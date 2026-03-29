package com.ivarna.truvalt.presentation.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.ivarna.truvalt.R
import coil.compose.AsyncImage
import java.security.MessageDigest
import java.util.UUID

// Common Theme Colors from HTML
val AuthPrimary = Color(0xFF5850BD)
val AuthBackground = Color(0xFFFCF8FE)
val AuthOnSurface = Color(0xFF33313A)
val AuthOnSurfaceVariant = Color(0xFF605E68)
val AuthSurfaceContainerHighest = Color(0xFFE5E1ED)
val AuthOutlineVariant = Color(0xFFB4B0BC)
val AuthSurfaceContainerLow = Color(0xFFF6F2FA)
val AuthSurfaceContainerLowest = Color(0xFFFFFFFF)
val AuthOnPrimary = Color(0xFFFCF7FF)
val AuthError = Color(0xFFA8364B)
val AuthSuccess = Color(0xFF34A853)

/** Walks up the context wrapper chain to find the host [Activity], or null. */
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

/** Google Credential Manager launcher for authentication. */
suspend fun launchGoogleSignIn(
    context: Activity,
    onToken: (String) -> Unit,
    onError: (String) -> Unit
) {
    val credentialManager = CredentialManager.create(context)
    val webClientId = context.getString(R.string.default_web_client_id)

    // Nonce to prevent replay attacks
    val rawNonce = UUID.randomUUID().toString()
    val nonce = MessageDigest.getInstance("SHA-256")
        .digest(rawNonce.toByteArray())
        .joinToString("") { "%02x".format(it) }

    val signInOption = GetSignInWithGoogleOption.Builder(webClientId)
        .setNonce(nonce)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCred.idToken)
        } else {
            onError("Unexpected credential type: ${credential.type}")
        }
    } catch (_: GetCredentialCancellationException) {
        // Silent — user cancelled
    } catch (e: GetCredentialException) {
        android.util.Log.e("GoogleSignIn", "${e::class.simpleName}: ${e.message}")
        onError("Google Sign-In failed: ${e.message ?: e::class.simpleName}")
    } catch (e: Exception) {
        android.util.Log.e("GoogleSignIn", "Unexpected: ${e.message}")
        onError("Google Sign-In error: ${e.message}")
    }
}

@Composable
fun RedesignedGoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.08f)),
        colors = ButtonDefaults.buttonColors(
            containerColor = AuthSurfaceContainerLowest,
            contentColor = AuthOnSurface,
            disabledContainerColor = AuthSurfaceContainerLowest.copy(alpha = 0.6f),
            disabledContentColor = AuthOnSurfaceVariant.copy(alpha = 0.4f)
        ),
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AuthOutlineVariant.copy(alpha = 0.3f)),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AuthPrimary)
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Official Google "G" Logo via AsyncImage for high-fidelity
                AsyncImage(
                    model = "https://www.gstatic.com/images/branding/product/2x/googleg_96dp.png",
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "Sign in with Google",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AuthOnSurface,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = AuthOutlineVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AuthOnSurface,
                unfocusedTextColor = AuthOnSurface,
                focusedContainerColor = AuthSurfaceContainerHighest.copy(alpha = 0.3f),
                unfocusedContainerColor = AuthSurfaceContainerHighest.copy(alpha = 0.3f),
                disabledContainerColor = AuthSurfaceContainerHighest.copy(alpha = 0.2f),
                unfocusedBorderColor = AuthOutlineVariant,
                focusedBorderColor = AuthPrimary,
                errorBorderColor = AuthError,
                cursorColor = AuthPrimary,
                focusedLabelColor = AuthPrimary,
                unfocusedLabelColor = AuthOnSurfaceVariant,
                errorLabelColor = AuthError
            ),
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = AuthOnSurfaceVariant
                        )
                    }
                }
            } else null,
            isError = error != null,
            singleLine = true,
            supportingText = error?.let {
                {
                    Text(
                        text = it,
                        color = AuthError,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        )
    }
}

@Composable
fun PasswordStrengthMeter(
    passwordLength: Int,
    modifier: Modifier = Modifier
) {
    val strength = when {
        passwordLength == 0 -> 0
        passwordLength < 8 -> 1
        passwordLength < 12 -> 2
        passwordLength < 16 -> 3
        else -> 4
    }
    
    val strengthText = when (strength) {
        0 -> "Empty"
        1 -> "Weak"
        2 -> "Fair"
        3 -> "Good"
        else -> "Strong"
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(4) { index ->
                val active = index < strength
                val color = if (active) {
                    when (strength) {
                        1 -> AuthError
                        2 -> Color(0xFFFBBC05) // Yellow
                        3 -> Color(0xFF34A853).copy(alpha = 0.7f)
                        else -> Color(0xFF34A853)
                    }
                } else AuthSurfaceContainerHighest
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(color)
                        .animateContentSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Strength: $strengthText",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AuthOutlineVariant,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SecurityStatusCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = AuthSurfaceContainerLow),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AuthPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AuthPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = AuthPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AuthOnSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun BrandIconHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .shadow(elevation = 24.dp, shape = RoundedCornerShape(24.dp), spotColor = AuthPrimary.copy(alpha = 0.2f)),
            colors = CardDefaults.cardColors(containerColor = AuthPrimary),
            shape = RoundedCornerShape(24.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Proper App Logo
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.ivarna.truvalt.R.drawable.truvalt_icon),
                    contentDescription = "Truvalt Logo",
                    modifier = Modifier.size(42.dp)
                )
            }
        }
    }
}
