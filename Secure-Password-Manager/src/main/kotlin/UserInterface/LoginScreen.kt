package UserInterface

// LoginScreen.kt

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

@Composable
fun LoginScreen() {
    var masterPassword by remember { mutableStateOf("") }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)
    val descriptionTextColor = Color(0xFF666666)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp)) // More space at the top

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
            text = "Unlock Password Vault",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Description
        Text(
            text = "Enter your master password to access your saved passwords",
            fontSize = 16.sp,
            color = descriptionTextColor,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. Master Password Input Field
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Master Password",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
            )
            OutlinedTextField(
                value = masterPassword,
                onValueChange = { masterPassword = it },
                placeholder = { Text("Enter master password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    backgroundColor = lightGreyBackground
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(48.dp)) // More space above the button

        // 5. Unlock Vault Button
        Button(
            onClick = {
                // TODO: Implement your master password verification logic here
                if (masterPassword.isNotEmpty()) {
                    println("Attempting to unlock with: $masterPassword")
                    // If password is correct, navigate to main app content
                } else {
                    println("Please enter your master password.")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Unlock Vault",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes content up for consistent layout
    }
}