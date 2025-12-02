package UserInterface

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.crypto.SecretKey
import androidx.compose.material.ExperimentalMaterialApi // <--- ADDED THIS IMPORT

// Data Class
data class PasswordEntry(
    val id: String,
    val website: String,
    val username: String,
    val passwordEncrypted: String
)

@Composable
fun HomeScreen(masterKey: SecretKey) {
    // Connect to the Database Repository
    val repository = remember { PasswordRepository() }

    // Load Real Data
    var passwordEntries by remember { mutableStateOf(repository.getAllPasswords(masterKey)) }
    var showAddDialog by remember { mutableStateOf(false) }

    fun refreshList() {
        passwordEntries = repository.getAllPasswords(masterKey)
    }

    val onDeletePassword: (PasswordEntry) -> Unit = { entry ->
        repository.deletePassword(entry.id)
        refreshList()
    }

    val lightBlueBackground = Color(0xFFF7F8FC)
    val darkColor = Color(0xFF333333)

    Box(modifier = Modifier.fillMaxSize().background(lightBlueBackground)) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top Bar
            TopBarSection(count = passwordEntries.size)

            Spacer(modifier = Modifier.height(16.dp))

            // Search & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, "Search") },
                    modifier = Modifier.weight(1f).background(Color.White, RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, "Add", tint = Color.White)
                    Text(" Add", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password List
            if (passwordEntries.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No passwords saved yet.", color = Color.Gray)
                }
            } else {
                PasswordList(passwordEntries, onDeletePassword)
            }
        }

        if (showAddDialog) {
            AddPasswordDialog(
                onDismiss = { showAddDialog = false },
                onSave = { website, username, password ->
                    repository.addPassword(website, username, password, masterKey)
                    refreshList()
                    showAddDialog = false
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
fun PasswordList(entries: List<PasswordEntry>, onDelete: (PasswordEntry) -> Unit) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(entries) { entry ->
            PasswordCard(entry, onDelete)
        }
    }
}

@Composable
fun PasswordCard(entry: PasswordEntry, onDelete: (PasswordEntry) -> Unit) {
    var showPassword by remember { mutableStateOf(false) }
    Card(shape = RoundedCornerShape(12.dp), elevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(entry.website, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(entry.username, color = Color.Gray, fontSize = 14.sp)
                }
                IconButton(onClick = { onDelete(entry) }) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFE57373))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(if (showPassword) entry.passwordEncrypted else "••••••••••")
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Toggle")
                }
            }
        }
    }
}

// --- ADDED THE OptIn ANNOTATION HERE ---
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddPasswordDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var website by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Password") },
        text = {
            Column {
                OutlinedTextField(value = website, onValueChange = { website = it }, label = { Text("Website") })
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())
            }
        },
        confirmButton = {
            Button(onClick = { onSave(website, username, password) }) { Text("Save") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}