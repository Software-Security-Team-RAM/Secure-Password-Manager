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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

// --- Main Composable for the Master Password Screen ---

@Composable
fun CreateMasterPasswordScreen() {
    var masterPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)
    val descriptionTextColor = Color(0xFF666666)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp), // Increased padding for better desktop feel
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // 1. Lock Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(darkColor), // Dark grey background for the icon
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
            onValueChange = { masterPassword = it },
            placeholder = "Enter master password",
            backgroundColor = lightGreyBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Confirm Master Password Input
        PasswordInputField(
            label = "Confirm Master Password",
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Re-enter master password",
            backgroundColor = lightGreyBackground
        )

        Spacer(modifier = Modifier.weight(1f)) // Pushes the button to the bottom

        // 6. Create Master Password Button
        Button(
            onClick = {
                // TODO: Implement your master password creation and validation logic here
                if (masterPassword == confirmPassword && masterPassword.isNotEmpty()) {
                    println("Master Password Created: $masterPassword")
                } else if (masterPassword != confirmPassword) {
                    println("Passwords do not match!")
                } else {
                    println("Please enter a password.")
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
    backgroundColor: Color
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
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                backgroundColor = backgroundColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

// --- Main entry point for Compose Desktop (if needed for testing) ---

// If your project uses the App() Composable from the template, you would use this structure:
/*
@Composable
fun App() {
    MaterialTheme {
        CreateMasterPasswordScreen()
    }
}
*/

// If you need a standalone runnable file for testing in a desktop environment:
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Secure Password Manager",
        state = androidx.compose.ui.window.rememberWindowState(
            width = 512.dp, // Appropriate size for a modal screen
            height = 768.dp
        )
    ) {
        MaterialTheme {
            CreateMasterPasswordScreen()
        }
    }
}
