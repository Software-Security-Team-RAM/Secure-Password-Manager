/**
* Created largely using Gemini Script
* Implements Master Password Validation
*/

package ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import encryption.CryptoManager
import javax.crypto.SecretKey
import java.io.File
import java.security.MessageDigest // Needed for hashing

@Composable
fun LoginScreen(onLoginSuccess: (SecretKey) -> Unit) {
    var masterPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)
    val descriptionTextColor = Color(0xFF666666)

    // Helper function to create a verification hash of the key
    // We do NOT store the password. We store a hash of the KEY.
    fun hashKey(key: SecretKey): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(key.encoded)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(darkColor), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Lock, "Lock Icon", tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Unlock Password Vault", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Enter your master password to access your saved passwords", fontSize = 16.sp, color = descriptionTextColor, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Master Password", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp))
            OutlinedTextField(
                value = masterPassword,
                onValueChange = {
                    masterPassword = it
                    errorMessage = "" // Clear error when typing
                },
                placeholder = { Text("Enter master password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = lightGreyBackground,
                    focusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else Color.Transparent,
                    unfocusedBorderColor = if (errorMessage.isNotEmpty()) Color.Red else Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (masterPassword.isNotEmpty()) {
                    try {
                        val saltFile = File("safebyte.salt")
                        val checkFile = File("safebyte.check")

                        if (!saltFile.exists()) {
                            // This should never happen if routing is correct, but handle gracefully
                            errorMessage = "Master password not found. Please restart the application."
                            return@Button
                        }

                        // --- LOGIN MODE ---

                        // 1. Load Salt
                        val salt = saltFile.readBytes()

                        // 2. Derive Key
                        val key = CryptoManager.deriveKey(masterPassword.toCharArray(), salt)

                        // 3. VERIFY KEY
                        if (checkFile.exists()) {
                            val savedHash = checkFile.readText()
                            val currentHash = hashKey(key)

                            if (savedHash == currentHash) {
                                // MATCH! Let them in.
                                onLoginSuccess(key)
                            } else {
                                // NO MATCH! Block them.
                                errorMessage = "Incorrect Password. Please try again."
                            }
                        } else {
                            // Edge case: Salt exists but check file missing?
                            errorMessage = "Verification file missing. Please restart the application."
                        }

                    } catch (e: Exception) {
                        errorMessage = "Error: ${e.message}"
                        e.printStackTrace()
                    }
                } else {
                    errorMessage = "Please enter your master password."
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Unlock Vault", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}