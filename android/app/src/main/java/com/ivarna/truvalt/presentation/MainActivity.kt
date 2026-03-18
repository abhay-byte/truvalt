package com.ivarna.truvalt.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.presentation.navigation.TruvaltNavHost
import com.ivarna.truvalt.presentation.theme.TruvaltTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    @Inject
    lateinit var appLockManager: AppLockManager
    
    @Inject
    lateinit var preferences: TruvaltPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TruvaltTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TruvaltNavHost()
                }
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            val timeout = preferences.autoLockTimeout.first()
            if (timeout == 0L) {
                appLockManager.lock()
            } else if (timeout > 0) {
                appLockManager.startAutoLockCountdown()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        appLockManager.cancelAutoLockCountdown()
    }
}
