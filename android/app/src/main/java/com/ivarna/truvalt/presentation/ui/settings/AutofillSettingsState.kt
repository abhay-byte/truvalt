package com.ivarna.truvalt.presentation.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun rememberTruvaltAutofillEnabled(context: Context = LocalContext.current): State<Boolean> {
    val lifecycleOwner = LocalLifecycleOwner.current
    val enabled = remember { mutableStateOf(isTruvaltAutofillEnabled(context)) }

    fun refresh() {
        enabled.value = isTruvaltAutofillEnabled(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        refresh()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return enabled
}

fun isTruvaltAutofillEnabled(context: Context): Boolean {
    val enabledService = Settings.Secure.getString(
        context.contentResolver,
        "autofill_service"
    ) ?: return false

    val component = ComponentName.unflattenFromString(enabledService) ?: return false
    return component.packageName == context.packageName
}

fun openTruvaltAutofillSettings(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE).apply {
        data = android.net.Uri.parse("package:${context.packageName}")
    }
    context.startActivity(intent)
}
