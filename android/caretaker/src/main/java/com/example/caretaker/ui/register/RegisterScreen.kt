package com.example.caretaker.ui.register

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caretaker.InkColor
import com.example.caretaker.MintColor
import com.example.caretaker.PanelColor
import com.example.caretaker.data.CaretakerRegisterRequest
import com.example.caretaker.data.NetworkClient
import com.example.caretaker.data.PreferencesManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    prefManager: PreferencesManager,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
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
                    text = "GET CONNECTED",
                    color = MintColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Sign Up",
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

                if (statusMessage.isNotEmpty()) {
                    Text(
                        text = statusMessage,
                        color = MintColor,
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
                    placeholder = { Text("Choose a username", color = Color.Gray) },
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
                    placeholder = { Text("Choose a password (min 6 chars)", color = Color.Gray) },
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

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Input
                Text(
                    text = "Caregiver Phone Number",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                    placeholder = { Text("Must match Patient caregiver_phone", color = Color.Gray) },
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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (username.trim().isEmpty() || password.trim().isEmpty() || phone.trim().isEmpty()) {
                            errorMessage = "Please fill in all fields."
                            return@Button
                        }
                        if (password.trim().length < 6) {
                            errorMessage = "Password must be at least 6 characters."
                            return@Button
                        }
                        isLoading = true
                        errorMessage = ""
                        statusMessage = ""
                        coroutineScope.launch {
                            try {
                                val api = NetworkClient.createService(prefManager.apiUrl)
                                api.register(CaretakerRegisterRequest(username.trim(), password.trim(), phone.trim()))
                                statusMessage = "Registration successful! Redirecting..."
                                kotlinx.coroutines.delay(1500)
                                onRegisterSuccess()
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Registration failed."
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = !isLoading && username.trim().isNotEmpty() && password.trim().isNotEmpty() && phone.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = MintColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isLoading) "Registering..." else "Register",
                        color = InkColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Already have an account? Log In", color = Color.LightGray, fontSize = 13.sp)
                }
            }
        }
    }
}
