package com.example.myaccidentapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myaccidentapplication.data.repository.AccidentRepository
import com.example.myaccidentapplication.ui.theme.DeepBlue
import com.example.myaccidentapplication.ui.theme.Teal
import com.example.myaccidentapplication.ui.theme.Amber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    repository: AccidentRepository   // Inject repository here
) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    fun validateInputs(): Boolean {
        return when {
            email.isBlank() -> { errorMessage = "Email cannot be empty"; false }
            password.isBlank() -> { errorMessage = "Password cannot be empty"; false }
            !email.contains("@") || !email.contains(".") -> { errorMessage = "Enter a valid email"; false }
            else -> true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(DeepBlue, Teal.copy(alpha = 0.3f)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Accident Monitor",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sign in to continue",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(48.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = null },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (validateInputs()) {
                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    val result = repository.login(email, password)  // ✅ returns Result<User>
                                    isLoading = false

                                    result.onSuccess { user ->
                                        CoroutineScope(Dispatchers.Main).launch {
                                            Toast.makeText(context, "Welcome ${user.name}", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        }
                                    }.onFailure { error ->
                                        CoroutineScope(Dispatchers.Main).launch {
                                            errorMessage = error.message ?: "Login failed"
                                        }
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Don't have an account? Register")
                    }
                }
            }
        }
    }
}