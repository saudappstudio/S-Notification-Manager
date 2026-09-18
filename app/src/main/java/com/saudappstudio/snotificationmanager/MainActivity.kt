package com.saudappstudio.snotificationmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.presentation.navigation.MainNavGraph
import com.saudappstudio.snotificationmanager.ui.theme.SNotificationManagerTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.saudappstudio.snotificationmanager.core.security.BiometricAuthManager
import com.saudappstudio.snotificationmanager.core.ui.ToastManager
import javax.inject.Inject

/**
 * Single Activity entry point hosting Compose navigation and biometric authentication.
 */

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var preferencesManager: PreferencesManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        Logger.i("POST_NOTIFICATIONS permission granted: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        setContent {
            val userPrefs by preferencesManager.userPreferencesFlow.collectAsState(
                initial = com.saudappstudio.snotificationmanager.core.datastore.UserPreferences()
            )

            val isDarkTheme = when (userPrefs.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            var isUnlocked by remember { mutableStateOf(!userPrefs.requireBiometricOnAppOpen) }

            LaunchedEffect(userPrefs.requireBiometricOnAppOpen) {
                if (userPrefs.requireBiometricOnAppOpen && !isUnlocked) {
                    val biometric = BiometricAuthManager(this@MainActivity)
                    if (biometric.canAuthenticate()) {
                        biometric.authenticate(
                            activity = this@MainActivity,
                            onAuthenticated = { isUnlocked = true },
                            onError = { err -> ToastManager.show(this@MainActivity, err) }
                        )
                    } else {
                        isUnlocked = true
                    }
                }
            }

            SNotificationManagerTheme(darkTheme = isDarkTheme) {
                if (userPrefs.requireBiometricOnAppOpen && !isUnlocked) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "App Locked",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Authenticate to open Saud Notification Manager",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    val biometric = BiometricAuthManager(this@MainActivity)
                                    if (biometric.canAuthenticate()) {
                                        biometric.authenticate(
                                            activity = this@MainActivity,
                                            onAuthenticated = { isUnlocked = true },
                                            onError = { err -> ToastManager.show(this@MainActivity, err) }
                                        )
                                    } else {
                                        isUnlocked = true
                                    }
                                },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Unlock App")
                            }
                        }
                    }
                } else {
                    MainNavGraph()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
