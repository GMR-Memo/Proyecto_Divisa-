package com.example.proyectodivisaff

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectodivisaff.data.RetrofitCliente
import com.example.proyectodivisaff.data.RetrofitService
import com.example.proyectodivisaff.ui.theme.ProyectoDivisaffTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            ProyectoDivisaffTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    MainScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var amount by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val retrofitService = remember {
        RetrofitCliente.instance.create(RetrofitService::class.java)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Conversor de Divisas", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Cantidad en USD") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (amountValue != null) {
                    isLoading = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = retrofitService.getMoney(
                                apiKey = "fb2e294992766ce64e11aa61",
                                base = "USD"
                            )
                            val rate = response.conversionRates["MXN"] ?: 0.0
                            val conversionResult = amountValue * rate

                            withContext(Dispatchers.Main) {
                                result = "Resultado: %.2f MXN".format(conversionResult)
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                isLoading = false
                            }
                        } }
                } else {
                    Toast.makeText(context, "Por favor, ingresa un valor válido", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Convertir a MXN")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = result, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProyectoDivisaffTheme {
        MainScreen()
    }
}
