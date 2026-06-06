package com.example.caretaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.caretaker.data.PreferencesManager
import com.example.caretaker.ui.login.LoginScreen
import com.example.caretaker.ui.register.RegisterScreen
import com.example.caretaker.ui.dashboard.DashboardScreen
import androidx.compose.ui.graphics.Color

enum class Screen {
    Login, Register, Dashboard
}

// Color Theme
val InkColor = Color(0xFF080B12)
val PanelColor = Color(0xFF111827)
val MintColor = Color(0xFF63E6BE)
val BorderColor = Color(0xFF2E333F)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = this
            val prefManager = remember { PreferencesManager(context) }
            var currentScreen by remember { 
                mutableStateOf(if (prefManager.isLoggedIn) Screen.Dashboard else Screen.Login) 
            }

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    background = InkColor,
                    surface = PanelColor,
                    primary = MintColor
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = InkColor
                ) {
                    when (currentScreen) {
                        Screen.Login -> LoginScreen(
                            prefManager = prefManager,
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            onLoginSuccess = { currentScreen = Screen.Dashboard }
                        )
                        Screen.Register -> RegisterScreen(
                            prefManager = prefManager,
                            onNavigateToLogin = { currentScreen = Screen.Login },
                            onRegisterSuccess = { currentScreen = Screen.Login }
                        )
                        Screen.Dashboard -> DashboardScreen(
                            prefManager = prefManager,
                            onLogout = {
                                prefManager.clear()
                                currentScreen = Screen.Login
                            }
                        )
                    }
                }
            }
        }
    }
}
