package com.mercury.configurator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class ProtocolType { MERCURY, SPODES }
enum class ConnectionType { USB_RS485, TCP_IP }
enum class PasswordEncoding { HEX, ASCII }

data class VectorData(
    val angleA: Float, val valA: Float,
    val angleB: Float, val valB: Float,
    val angleC: Float, val valC: Float
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainConfiguratorScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainConfiguratorScreen() {
    var selectedProtocol by remember { mutableStateOf(ProtocolType.MERCURY) }
    var connectionType by remember { mutableStateOf(ConnectionType.USB_RS485) }
    var passwordEncoding by remember { mutableStateOf(PasswordEncoding.HEX) }

    var netAddress by remember { mutableStateOf("0") }
    var passwordText by remember { mutableStateOf("010101010101") }
    var ipAddress by remember { mutableStateOf("192.168.1.100") }
    var tcpPort by remember { mutableStateOf("4001") }

    var statusText by remember { mutableStateOf("Готов к работе") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mercury & SPODES Configurator") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Параметры соединения", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedProtocol == ProtocolType.MERCURY,
                            onClick = { selectedProtocol = ProtocolType.MERCURY },
                            label = { Text("Меркурий") }
                        )
                        FilterChip(
                            selected = selectedProtocol == ProtocolType.SPODES,
                            onClick = { selectedProtocol = ProtocolType.SPODES },
                            label = { Text("СПОДЕС") }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = connectionType == ConnectionType.USB_RS485,
                            onClick = { connectionType = ConnectionType.USB_RS485 },
                            label = { Text("USB / RS-485 / УСО-2") }
                        )
                        FilterChip(
                            selected = connectionType == ConnectionType.TCP_IP,
                            onClick = { connectionType = ConnectionType.TCP_IP },
                            label = { Text("TCP / IP") }
                        )
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (connectionType == ConnectionType.TCP_IP) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = ipAddress, onValueChange = { ipAddress = it }, label = { Text("IP адрес") }, modifier = Modifier.weight(2f))
                            OutlinedTextField(value = tcpPort, onValueChange = { tcpPort = it }, label = { Text("Порт") }, modifier = Modifier.weight(1f))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(value = netAddress, onValueChange = { netAddress = it }, label = { Text("Сетевой адрес") }, modifier = Modifier.weight(1f))
                        Row(Modifier.weight(1.5f), verticalAlignment = Alignment.CenterVertically) {
                            Text("Пароль:")
                            RadioButton(selected = passwordEncoding == PasswordEncoding.HEX, onClick = { passwordEncoding = PasswordEncoding.HEX })
                            Text("HEX")
                            RadioButton(selected = passwordEncoding == PasswordEncoding.ASCII, onClick = { passwordEncoding = PasswordEncoding.ASCII })
                            Text("ASCII")
                        }
                    }

                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        label = { Text("Пароль доступа (${passwordEncoding.name})") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Button(
                onClick = { statusText = "Опрос прибора учета..." },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Опросить прибор учета")
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Векторная диаграмма (U / I)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    VectorDiagramView(
                        voltageVectors = VectorData(0f, 220f, 240f, 220f, 120f, 220f),
                        currentVectors = VectorData(30f, 5f, 270f, 5f, 150f, 5f)
                    )
                }
            }

            Text(text = "Статус: $statusText", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun VectorDiagramView(
    voltageVectors: VectorData,
    currentVectors: VectorData,
    modifier: Modifier = Modifier.size(240.dp)
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 20f

        drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = radius, style = Stroke(2f))
        drawCircle(color = Color.Gray.copy(alpha = 0.2f), radius = radius * 0.66f, style = Stroke(1f))
        drawCircle(color = Color.Gray.copy(alpha = 0.1f), radius = radius * 0.33f, style = Stroke(1f))

        drawLine(Color.Gray.copy(alpha = 0.3f), Offset(center.x - radius, center.y), Offset(center.x + radius, center.y))
        drawLine(Color.Gray.copy(alpha = 0.3f), Offset(center.x, center.y - radius), Offset(center.x, center.y + radius))

        fun drawVector(angleDeg: Float, magnitudeNormalized: Float, color: Color, strokeWidth: Float) {
            val angleRad = Math.toRadians(angleDeg.toDouble())
            val endX = center.x + (radius * magnitudeNormalized * cos(angleRad)).toFloat()
            val endY = center.y - (radius * magnitudeNormalized * sin(angleRad)).toFloat()
            drawLine(color = color, start = center, end = Offset(endX, endY), strokeWidth = strokeWidth)
        }

        drawVector(voltageVectors.angleA, 0.9f, Color.Red, 6f)
        drawVector(voltageVectors.angleB, 0.9f, Color(0xFF00C853), 6f)
        drawVector(voltageVectors.angleC, 0.9f, Color.Blue, 6f)

        drawVector(currentVectors.angleA, 0.6f, Color(0xFFFF8A80), 4f)
        drawVector(currentVectors.angleB, 0.6f, Color(0xFFB9F6CA), 4f)
        drawVector(currentVectors.angleC, 0.6f, Color(0xFF82B1FF), 4f)
    }
}
