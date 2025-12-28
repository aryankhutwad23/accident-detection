package com.example.myaccidentapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myaccidentapplication.ui.theme.Teal

@Composable
fun EmergencyContactFormScreen(
    onSaveContacts: (String, String) -> Unit,
    onBack: () -> Unit
) {
    var contact1 by remember { mutableStateOf("") }
    var contact2 by remember { mutableStateOf("") }
    var contact1Error by remember { mutableStateOf<String?>(null) }
    var contact2Error by remember { mutableStateOf<String?>(null) }

    // Improved validation: Allows only 10 digits (modify if needed)
    fun validatePhone(input: String): Boolean {
        val digits = input.filter { it.isDigit() }
        return digits.length == 10
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Emergency Contacts",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add two trusted contacts who will be notified in case of an emergency.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            PhoneField(
                label = "Emergency Contact 1",
                phone = contact1,
                onChange = {
                    contact1 = it.filter(Char::isDigit)
                    contact1Error = null
                },
                error = contact1Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            PhoneField(
                label = "Emergency Contact 2",
                phone = contact2,
                onChange = {
                    contact2 = it.filter(Char::isDigit)
                    contact2Error = null
                },
                error = contact2Error
            )
        }

        Column {
            Button(
                onClick = {
                    val c1Valid = validatePhone(contact1)
                    val c2Valid = validatePhone(contact2)

                    contact1Error = when {
                        contact1.isEmpty() -> "Phone number is required"
                        !c1Valid -> "Must be a valid 10-digit number"
                        else -> null
                    }

                    contact2Error = when {
                        contact2.isEmpty() -> "Phone number is required"
                        !c2Valid -> "Must be a valid 10-digit number"
                        contact1 == contact2 -> "Contacts cannot be the same number"
                        else -> null
                    }

                    if (contact1Error == null && contact2Error == null) {
                        onSaveContacts(contact1.trim(), contact2.trim())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                Text("Save & Continue")
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun PhoneField(
    label: String,
    phone: String,
    onChange: (String) -> Unit,
    error: String?
) {
    OutlinedTextField(
        value = phone,
        onValueChange = onChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
    )
}
