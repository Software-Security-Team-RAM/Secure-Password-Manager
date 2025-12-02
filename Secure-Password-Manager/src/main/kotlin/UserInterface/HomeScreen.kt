package UserInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.crypto.SecretKey
import androidx.compose.material.ExperimentalMaterialApi
import kotlin.random.Random

// Data Class
data class PasswordEntry(
    val id: String,
    val website: String,
    val username: String,
    val passwordEncrypted: String,
    val notes: String
)

@Composable
fun HomeScreen(masterKey: SecretKey) {
    val repository = remember { PasswordRepository() }
    var passwordEntries by remember { mutableStateOf(repository.getAllPasswords(masterKey)) }

    // State for Add/Edit Dialog
    var showDialog by remember { mutableStateOf(false) }
    var passwordToEdit by remember { mutableStateOf<PasswordEntry?>(null) } // Null means "Add Mode"

    // Search Query State
    var searchQuery by remember { mutableStateOf("") }

    fun refreshList() {
        val all = repository.getAllPasswords(masterKey)
        if (searchQuery.isEmpty()) {
            passwordEntries = all
        } else {
            passwordEntries = all.filter {
                it.website.contains(searchQuery, ignoreCase = true) ||
                        it.username.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Refresh when search query changes
    LaunchedEffect(searchQuery) {
        refreshList()
    }

    val onDeletePassword: (PasswordEntry) -> Unit = { entry ->
        repository.deletePassword(entry.id)
        refreshList()
    }

    // Edit Handler: Opens the dialog in "Edit Mode"
    val onEditPassword: (PasswordEntry) -> Unit = { entry ->
        passwordToEdit = entry
        showDialog = true
    }

    val lightBlueBackground = Color(0xFFF7F8FC)
    val darkColor = Color(0xFF333333)

    Box(modifier = Modifier.fillMaxSize().background(lightBlueBackground)) {
        Column(modifier = Modifier.padding(16.dp)) {

            TopBarSection(count = passwordEntries.size)

            Spacer(modifier = Modifier.height(16.dp))

            // Search & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search websites...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = {
                        passwordToEdit = null // Reset to Add Mode
                        showDialog = true
                    },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, "Add", tint = Color.White)
                    Text(" Add", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (passwordEntries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No passwords found.", color = Color.Gray)
                }
            } else {
                PasswordList(passwordEntries, onDeletePassword, onEditPassword)
            }
        }

        // The Dialog Logic
        if (showDialog) {
            AddPasswordDialog(
                initialEntry = passwordToEdit, // Pass the existing data if editing
                onDismiss = { showDialog = false },
                onSave = { website, username, password, notes ->
                    if (passwordToEdit != null) {
                        // EDIT MODE: Update existing entry
                        repository.updatePassword(passwordToEdit!!.id, website, username, password, notes, masterKey)
                    } else {
                        // ADD MODE: Create new entry
                        repository.addPassword(website, username, password, notes, masterKey)
                    }
                    refreshList()
                    showDialog = false
                }
            )
        }
    }
}

// --- Helper Components ---

@Composable
fun TopBarSection(count: Int) {
    Card(shape = RoundedCornerShape(12.dp), elevation = 2.dp) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF333333)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Lock, "Vault", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Password Vault", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$count passwords saved", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PasswordList(entries: List<PasswordEntry>, onDelete: (PasswordEntry) -> Unit, onEdit: (PasswordEntry) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(entries) { entry ->
            PasswordCard(entry, onDelete, onEdit)
        }
    }
}

@Composable
fun PasswordCard(entry: PasswordEntry, onDelete: (PasswordEntry) -> Unit, onEdit: (PasswordEntry) -> Unit) {
    var showPassword by remember { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current

    Card(shape = RoundedCornerShape(12.dp), elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(entry.website, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(entry.username, color = Color.Gray, fontSize = 14.sp)
                }
                Row {
                    // Edit Button
                    IconButton(onClick = { onEdit(entry) }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Color.Black)
                    }
                    // Delete Button
                    IconButton(onClick = { onDelete(entry) }) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFE57373))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Password Row
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (showPassword) entry.passwordEncrypted else "••••••••••")

                Row {
                    // Reveal Button
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle")
                    }
                    // Copy Button
                    IconButton(onClick = {
                        clipboardManager.setText(AnnotatedString(entry.passwordEncrypted))
                    }) {
                        Icon(Icons.Default.ContentCopy, "Copy", tint = Color(0xFF333333))
                    }
                }
            }

            // Notes Display (If present)
            if (entry.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Notes: ${entry.notes}", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

// --- ADD/EDIT DIALOG WITH GENERATOR ---

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddPasswordDialog(
    initialEntry: PasswordEntry? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit // Added String param for Notes
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Details, 1 = Generator
    val tabs = listOf("Details", "Generator")

    // Form State (Pre-filled if editing)
    var website by remember { mutableStateOf(initialEntry?.website ?: "") }
    var username by remember { mutableStateOf(initialEntry?.username ?: "") }
    var password by remember { mutableStateOf(initialEntry?.passwordEncrypted ?: "") }
    var notes by remember { mutableStateOf(initialEntry?.notes ?: "") } // NEW: Notes State

    // Generator State
    var genLength by remember { mutableStateOf(16f) }
    var useUpper by remember { mutableStateOf(true) }
    var useLower by remember { mutableStateOf(true) }
    var useNums by remember { mutableStateOf(true) }
    var useSyms by remember { mutableStateOf(true) }
    var generatedPass by remember { mutableStateOf("") }

    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialEntry == null) "Add New Password" else "Edit Password") },
        text = {
            Column(modifier = Modifier.width(400.dp)) {
                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTab,
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF333333)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    // --- DETAILS TAB ---
                    Column {
                        OutlinedTextField(
                            value = website,
                            onValueChange = { website = it },
                            label = { Text("Website/Service") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Username/Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = PasswordVisualTransformation(),
                            trailingIcon = {
                                if (password.isNotEmpty()) {
                                    IconButton(onClick = { password = "" }) {
                                        Icon(Icons.Default.Clear, "Clear")
                                    }
                                }
                            }
                        )
                        // NEW: Notes Field
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                } else {
                    // --- GENERATOR TAB ---
                    Column {
                        // Generated Display Area
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (generatedPass.isEmpty()) "Click Generate" else generatedPass,
                                fontSize = 16.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                maxLines = 1
                            )
                            IconButton(onClick = {
                                if(generatedPass.isNotEmpty()) clipboardManager.setText(AnnotatedString(generatedPass))
                            }) {
                                Icon(Icons.Default.ContentCopy, "Copy")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Controls
                        Text("Length: ${genLength.toInt()}")
                        Slider(
                            value = genLength,
                            onValueChange = { genLength = it },
                            valueRange = 8f..32f,
                            steps = 24
                        )

                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                LabelledCheckbox("Uppercase (A-Z)", useUpper) { useUpper = it }
                                LabelledCheckbox("Numbers (0-9)", useNums) { useNums = it }
                            }
                            Column {
                                LabelledCheckbox("Lowercase (a-z)", useLower) { useLower = it }
                                LabelledCheckbox("Symbols (!@#$)", useSyms) { useSyms = it }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Generator Buttons
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(
                                onClick = {
                                    generatedPass = generateRandomPassword(genLength.toInt(), useUpper, useLower, useNums, useSyms)
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333))
                            ) {
                                Icon(Icons.Default.Refresh, "Generate", tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generate", color = Color.White)
                            }

                            // "Use Password" Button - Moves result to Details tab
                            Button(
                                onClick = {
                                    if (generatedPass.isNotEmpty()) {
                                        password = generatedPass
                                        selectedTab = 0 // Switch back to details
                                    }
                                },
                                enabled = generatedPass.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFF0F0F0))
                            ) {
                                Text("Use Password")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 0) {
                Button(onClick = { onSave(website, username, password, notes) }) { Text("Save") }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LabelledCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onCheckedChange(!checked) }) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(label, fontSize = 14.sp)
    }
}

// Logic Function for Random Generation
fun generateRandomPassword(length: Int, upper: Boolean, lower: Boolean, nums: Boolean, syms: Boolean): String {
    val upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowerChars = "abcdefghijklmnopqrstuvwxyz"
    val numChars = "0123456789"
    val symChars = "!@#$%^&*()_+-=[]{}|;:,.<>?"

    var pool = ""
    if (upper) pool += upperChars
    if (lower) pool += lowerChars
    if (nums) pool += numChars
    if (syms) pool += symChars

    if (pool.isEmpty()) return ""

    return (1..length)
        .map { pool[Random.nextInt(pool.length)] }
        .joinToString("")
}