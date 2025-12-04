/**
* Created largly using Gemini Script
*/

package ui


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

// Enum for managing the tabs
private enum class AddPasswordTab {
    DETAILS,
    GENERATOR
}

@Composable
fun AddPasswordScreen() {
    // Placeholder actions
    val onDismiss = { println("Placeholder: Dialog dismissed") }
    val onSave: (String, String, String, String) -> Unit = { website, user, pass, notes ->
        println("Placeholder: SAVE attempt - Website: $website")
    }

    // State to track which tab is active
    var currentTab by remember { mutableStateOf(AddPasswordTab.DETAILS) }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)

    // Fill the entire screen with white background and center the card
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Main dialog surface/card
        Card(
            modifier = Modifier
                .width(500.dp) // Fixed width for the dialog
                .heightIn(min = 600.dp), // Minimum height
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color.White,
            elevation = 8.dp
        ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add New Password",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Save a new password to your vault",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(onClick = onDismiss)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tabs Segmented Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(lightGreyBackground)
            ) {
                TabSegment(
                    label = "Details",
                    isSelected = currentTab == AddPasswordTab.DETAILS,
                    onClick = { currentTab = AddPasswordTab.DETAILS }
                )
                TabSegment(
                    label = "Generator",
                    isSelected = currentTab == AddPasswordTab.GENERATOR,
                    onClick = { currentTab = AddPasswordTab.GENERATOR }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tab Content
            when (currentTab) {
                AddPasswordTab.DETAILS -> DetailsTabContent(onSave = onSave)
                AddPasswordTab.GENERATOR -> GeneratorTabContent()
            }
        }
        }
    }
}

// --- Helper Composable for Tab Segment ---

@Composable
private fun RowScope.TabSegment(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.White else lightGreyBackground)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) darkColor else Color.Gray,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

// --- Details Tab Content ---

@Composable
private fun DetailsTabContent(
    onSave: (website: String, username: String, password: String, notes: String) -> Unit
) {
    var website by remember { mutableStateOf("Test") }
    var username by remember { mutableStateOf("test") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val darkColor = Color(0xFF333333)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Website/Service
        LabeledTextField(label = "Website/Service", value = website, onValueChange = { website = it })
        Spacer(modifier = Modifier.height(16.dp))

        // Username/Email
        LabeledTextField(label = "Username/Email", value = username, onValueChange = { username = it })
        Spacer(modifier = Modifier.height(16.dp))

        // Password
        LabeledPasswordField(label = "Password", value = password, onValueChange = { password = it })
        Spacer(modifier = Modifier.height(16.dp))

        // Notes (Optional)
        LabeledTextField(label = "Notes (Optional)", value = notes, onValueChange = { notes = it }, placeholder = "Additional information")
        Spacer(modifier = Modifier.weight(1f)) // Push buttons to the bottom

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = { println("Placeholder: Cancel clicked") }) {
                Text("Cancel", color = Color.Gray, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { onSave(website, username, password, notes) },
                colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(100.dp).height(48.dp)
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}

// --- Generator Tab Content ---

@Composable
private fun GeneratorTabContent() {
    var generatedPassword by remember { mutableStateOf("Click generate to create password") }
    var passwordLength by remember { mutableStateOf(16f) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)

    val generateAction = {
        generatedPassword = generatePassword(passwordLength.toInt(), includeUppercase, includeLowercase, includeNumbers, includeSymbols)
        println("Placeholder: Generated new password.")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Generated Password Display
        Text(
            text = "Generated Password",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(lightGreyBackground, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(generatedPassword, fontSize = 16.sp, color = Color.Black)
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = Color.Gray,
                modifier = Modifier.clickable { println("Placeholder: Copied password to clipboard.") }
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Length Slider
        Text(
            text = "Length: ${passwordLength.toInt()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        Slider(
            value = passwordLength,
            onValueChange = { passwordLength = it },
            valueRange = 8f..32f,
            steps = 23,
            colors = SliderDefaults.colors(thumbColor = darkColor, activeTrackColor = darkColor)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Checkboxes
        SettingsCheckbox(label = "Uppercase (A-Z)", isChecked = includeUppercase, onCheckedChange = { includeUppercase = it })
        SettingsCheckbox(label = "Lowercase (a-z)", isChecked = includeLowercase, onCheckedChange = { includeLowercase = it })
        SettingsCheckbox(label = "Numbers (0-9)", isChecked = includeNumbers, onCheckedChange = { includeNumbers = it })
        SettingsCheckbox(label = "Symbols (!@#$...) S", isChecked = includeSymbols, onCheckedChange = { includeSymbols = it })

        Spacer(modifier = Modifier.weight(1f))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = generateAction,
                colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Generate", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Password", color = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { println("Placeholder: Used generated password.") },
                colors = ButtonDefaults.buttonColors(backgroundColor = lightGreyBackground),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(120.dp).height(48.dp),
                elevation = ButtonDefaults.elevation(0.dp)
            ) {
                Text("Use Password", color = darkColor)
            }
        }
    }
}

// --- Generic Reusable Composables (Same as before) ---

@Composable
private fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                backgroundColor = Color(0xFFF0F0F0)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
private fun LabeledPasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                backgroundColor = Color(0xFFF0F0F0)
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )
    }
}

@Composable
private fun SettingsCheckbox(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF333333))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, fontSize = 16.sp, color = Color.Black)
    }
}


// --- Simple Password Generation Logic (Placeholder) ---

private fun generatePassword(
    length: Int,
    uppercase: Boolean,
    lowercase: Boolean,
    numbers: Boolean,
    symbols: Boolean
): String {
    val charPool = mutableListOf<Char>()
    if (uppercase) charPool.addAll('A'..'Z')
    if (lowercase) charPool.addAll('a'..'z')
    if (numbers) charPool.addAll('0'..'9')
    if (symbols) charPool.addAll("!@#\$%^&*_+-=".toList())

    if (charPool.isEmpty()) return "Error: Select character types."

    return (1..length)
        .map { charPool[Random.nextInt(charPool.size)] }
        .joinToString("")
}
