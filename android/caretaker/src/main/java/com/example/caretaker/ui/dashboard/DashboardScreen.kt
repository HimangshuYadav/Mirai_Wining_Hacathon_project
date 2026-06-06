package com.example.caretaker.ui.dashboard

import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.caretaker.InkColor
import com.example.caretaker.MintColor
import com.example.caretaker.PanelColor
import com.example.caretaker.data.NetworkClient
import com.example.caretaker.data.PreferencesManager
import com.example.caretaker.data.SosAlert
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    prefManager: PreferencesManager,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var alertsList by remember { mutableStateOf<List<SosAlert>>(emptyList()) }
    var activeAlert by remember { mutableStateOf<SosAlert?>(null) }
    var wsConnectionStatus by remember { mutableStateOf("Connecting...") }
    var wsConnected by remember { mutableStateOf(false) }

    // Fetch historical alerts once on startup
    LaunchedEffect(Unit) {
        try {
            val api = NetworkClient.createService(prefManager.apiUrl)
            val res = api.getSosAlerts(prefManager.caregiverPhone)
            alertsList = res.alerts
        } catch (e: Exception) {
            Log.e("Dashboard", "Failed to fetch alerts", e)
        }
    }

    // Helper to play default system alarm/notification ringtone
    fun playEmergencySound() {
        try {
            val alertUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE) 
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context, alertUri)
            ringtone.play()
            // Stop playing after 4 seconds to avoid blocking user indefinitely
            coroutineScope.launch {
                delay(4000)
                if (ringtone.isPlaying) {
                    ringtone.stop()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Connect to WebSocket route for real-time notifications
    LaunchedEffect(Unit) {
        val client = NetworkClient.httpClient
        val wsUrl = prefManager.apiUrl
            .replace("http://", "ws://")
            .replace("https://", "wss://")
        val formattedBase = if (wsUrl.endsWith("/")) wsUrl else "$wsUrl/"
        val fullWsUrl = "${formattedBase}sos/ws/${prefManager.caregiverPhone}"

        Log.d("WebSocket", "Connecting to $fullWsUrl")
        val request = Request.Builder().url(fullWsUrl).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                Log.d("WebSocket", "Connected")
                wsConnectionStatus = "Live Connected"
                wsConnected = true
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                try {
                    val alert = Gson().fromJson(text, SosAlert::class.java)
                    // Prepend to top of dashboard list
                    alertsList = listOf(alert) + alertsList
                    // Set active popup alert
                    activeAlert = alert
                    // Play warning sound
                    playEmergencySound()
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error parsing websocket message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                wsConnectionStatus = "Reconnecting..."
                wsConnected = false
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Connection failure", t)
                wsConnectionStatus = "Disconnected. Retrying..."
                wsConnected = false
            }
        }

        var webSocket = client.newWebSocket(request, listener)

        // Keep-alive/reconnect polling loop
        while (true) {
            delay(10000)
            if (!wsConnected) {
                Log.d("WebSocket", "Attempting websocket reconnection...")
                webSocket.cancel()
                webSocket = client.newWebSocket(request, listener)
            }
        }
    }

    Scaffold(
        containerColor = InkColor,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = InkColor),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Caretaker", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Text("Dashboard", fontWeight = FontWeight.Bold, color = MintColor, fontSize = 18.sp, modifier = Modifier.padding(start = 4.dp))
                        }
                        // Status connection banner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (wsConnected) MintColor else Color(0xFFEF4444))
                            )
                            Text(
                                text = wsConnectionStatus,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Log Out", tint = Color.LightGray)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(InkColor)
                .padding(16.dp)
        ) {
            // Profile Info Header Card
            Card(
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                colors = CardDefaults.cardColors(containerColor = PanelColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MintColor.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = prefManager.username.take(1).uppercase(),
                            color = MintColor,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Logged in as ${prefManager.username}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "Emergency Phone: ${prefManager.caregiverPhone}", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Text(
                text = "SOS EMERGENCY ALERTS",
                color = MintColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (alertsList.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        text = "No SOS alerts received yet.\nReal-time alerts are live connected.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(alertsList) { alert ->
                        AlertItemCard(alert = alert, onOpenMaps = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(alert.location_link))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
                    }
                }
            }
        }
    }

    // Full Screen Emergency overlay banner dialog on receiving real-time SOS
    activeAlert?.let { alert ->
        Dialog(onDismissRequest = { activeAlert = null }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(2.dp, Color(0xFFEF4444)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F0D0D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                            .border(2.dp, Color(0xFFEF4444), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CRITICAL EMERGENCY ALERT",
                        color = Color(0xFFEF4444),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${alert.person_name} has triggered an SOS!",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Latitude: ${alert.latitude}\nLongitude: ${alert.longitude}\nTime: ${alert.timestamp}",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(alert.location_link))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Open Google Maps Location", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { activeAlert = null }) {
                        Text("Acknowledge & Close", color = Color.LightGray, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(
    alert: SosAlert,
    onOpenMaps: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(containerColor = PanelColor.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF4444))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = alert.person_name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = alert.timestamp.take(16).replace("T", " "),
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "GPS Coordinates: ${alert.latitude}, ${alert.longitude}",
                color = Color.LightGray,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenMaps,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("View Location in Maps", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
