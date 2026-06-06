package com.example.caretaker.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caretaker.InkColor
import com.example.caretaker.MintColor
import com.example.caretaker.PanelColor
import com.example.caretaker.data.CaretakerLoginRequest
import com.example.caretaker.data.NetworkClient
import com.example.caretaker.data.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    prefManager: PreferencesManager,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // API settings dialog
    var showSettings by remember { mutableStateOf(false) }
    var apiInputUrl by remember { mutableStateOf(prefManager.apiUrl) }

    Scaffold(
        containerColor = InkColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkColor),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Caretaker", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Portal", fontWeight = FontWeight.Bold, color = MintColor, modifier = Modifier.padding(start = 4.dp))
                    }
                },
                actions = {
                    IconButton(onClick = {
                        apiInputUrl = prefManager.apiUrl
                        showSettings = true
                    }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.LightGray)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(InkColor)
                .padding(24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "WELCOME BACK",
                        color = MintColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Log In",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFEF4444),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Username Input
                    Text(
                        text = "Username",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        placeholder = { Text("Enter username", color = Color.Gray) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.25f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Input
                    Text(
                        text = "Password",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                        placeholder = { Text("••••••", color = Color.Gray) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.10f),
                            focusedContainerColor = Color.Black.copy(alpha = 0.25f),
                            unfocusedContainerColor = Color.Black.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (username.trim().isEmpty() || password.trim().isEmpty()) {
                                errorMessage = "Please fill in all fields."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                try {
                                    val api = NetworkClient.createService(prefManager.apiUrl)
                                    val res = api.login(CaretakerLoginRequest(username.trim(), password.trim()))
                                    prefManager.username = res.username
                                    prefManager.caregiverPhone = res.phone
                                    prefManager.isLoggedIn = true
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "Authentication failed."
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        enabled = !isLoading && username.trim().isNotEmpty() && password.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = MintColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = if (isLoading) "Logging In..." else "Log In",
                            color = InkColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Don't have an account? Sign Up", color = Color.LightGray, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showSettings) {
        Dialog(onDismissRequest = { showSettings = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                colors = CardDefaults.cardColors(containerColor = PanelColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Server Settings", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                    OutlinedTextField(
                        value = apiInputUrl,
                        onValueChange = { apiInputUrl = it },
                        textStyle = TextStyle(color = Color.White, fontSize = 13.sp),
                        label = { Text("API Server URL") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MintColor,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                            focusedLabelColor = MintColor,
                            unfocusedLabelColor = Color.Gray
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                prefManager.apiUrl = apiInputUrl.trim()
                                showSettings = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MintColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save", color = InkColor, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showSettings = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                        ) {
                            Text("Cancel", color = Color.LightGray)
                        }
                    }
                }
            }
        }
    }
}
