package UserInterface

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
import javax.crypto.SecretKey
import java.io.File
import java.security.MessageDigest

// --- Main Composable for the Master Password Screen ---

@Composable
fun CreateMasterPasswordScreen(onPasswordCreated: (SecretKey) -> Unit) {
    var masterPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)
    val descriptionTextColor = Color(0xFF666666)

    // Helper function to create a verification hash of the key
    fun hashKey(key: SecretKey): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(key.encoded)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 1. Lock Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(darkColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Title
        Text(
            text = "Create Master Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Description
        Text(
            text = "This password will encrypt and protect all your saved passwords. Make sure it's strong and memorable.",
            fontSize = 16.sp,
            color = descriptionTextColor,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Master Password Input
        PasswordInputField(
            label = "Master Password",
            value = masterPassword,
            onValueChange = { 
                masterPassword = it
                errorMessage = "" // Clear error when typing
            },
            placeholder = "Enter master password",
            backgroundColor = lightGreyBackground,
            showError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Confirm Master Password Input
        PasswordInputField(
            label = "Confirm Master Password",
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                errorMessage = "" // Clear error when typing
            },
            placeholder = "Re-enter master password",
            backgroundColor = lightGreyBackground,
            showError = errorMessage.isNotEmpty()
        )

        // Error message display
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 6. Create Master Password Button
        Button(
            onClick = {
                // Validation
                if (masterPassword.isEmpty()) {
                    errorMessage = "Please enter a password."
                    return@Button
                }
                
                if (masterPassword.length < 8) {
                    errorMessage = "Password must be at least 8 characters long."
                    return@Button
                }
                
                if (masterPassword != confirmPassword) {
                    errorMessage = "Passwords do not match!"
                    return@Button
                }

                // Create master password
                try {
                    val saltFile = File("safebyte.salt")
                    val checkFile = File("safebyte.check")

                    // 1. Generate & Save Salt
                    val salt = CryptoManager.generateSalt()
                    saltFile.writeBytes(salt)

                    // 2. Derive Key
                    val key = CryptoManager.deriveKey(masterPassword.toCharArray(), salt)

                    // 3. Create Verification File
                    val keyHash = hashKey(key)
                    checkFile.writeText(keyHash)

                    println("Setup: Created new master password with verification hash.")
                    
                    // Clear password from memory
                    masterPassword = ""
                    confirmPassword = ""
                    
                    // Navigate to home screen
                    onPasswordCreated(key)
                } catch (e: Exception) {
                    errorMessage = "Error creating master password: ${e.message}"
                    e.printStackTrace()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Create Master Password",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Helper Composable for Text Field Styling ---

@Composable
fun PasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    backgroundColor: Color,
    showError: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = if (showError) Color.Red else Color.Transparent,
                unfocusedBorderColor = if (showError) Color.Red else Color.Transparent,
                backgroundColor = backgroundColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}
