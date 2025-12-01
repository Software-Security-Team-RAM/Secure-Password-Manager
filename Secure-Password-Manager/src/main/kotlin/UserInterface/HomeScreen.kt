// HomeScreen.kt
package UserInterface

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Data Class (Same as before) ---
data class PasswordEntry(
    val id: String,
    val website: String,
    val username: String,
    val passwordEncrypted: String
)

// --- Home Screen Composable (Simplified - No parameters) ---

@Composable
fun HomeScreen() {
    // 1. Mock Data State (Temporary, will be replaced by SQLite data)
    var mockPasswordEntries by remember {
        mutableStateOf(
            listOf(
                PasswordEntry("1", "Example.com", "user@example.com", "securepassword123"),
                PasswordEntry("2", "Google", "myemail@gmail.com", "anothersecurepass"),
                PasswordEntry("3", "GitHub", "dev_user", "mycode_pass"),
                PasswordEntry("4", "Amazon", "shopaholic@mail.com", "buyallthethings"),
                PasswordEntry("5", "Twitter", "x_user", "12345password"),
            )
        )
    }

    // 2. Mock Action Handlers (Same placeholder logic)
    val onAddPassword = {
        val newId = (mockPasswordEntries.size + 1).toString()
        mockPasswordEntries = mockPasswordEntries + PasswordEntry(
            id = newId,
            website = "New Mock Site $newId",
            username = "mockuser$newId",
            passwordEncrypted = "mockpass$newId"
        )
        println("Placeholder: Navigate to Add Password Screen or show dialog.")
    }
    val onSearchQueryChange: (String) -> Unit = { query ->
        println("Placeholder: Filtering list based on query: $query")
    }
    val onEditPassword: (PasswordEntry) -> Unit = { entry ->
        println("Placeholder: Edit password for ${entry.website}")
    }
    val onDeletePassword: (PasswordEntry) -> Unit = { entry ->
        mockPasswordEntries = mockPasswordEntries.filter { it.id != entry.id }
        println("Placeholder: Deleted password for ${entry.website}")
    }
    val onLockVault = {
        println("Placeholder: Lock Vault (Navigate to Login Screen)")
    }

    // --- UI Implementation ---
    val lightBlueBackground = Color(0xFFF7F8FC)
    val cardColor = Color.White
    val darkColor = Color(0xFF333333)
    val borderColor = Color(0xFFE0E0E0)

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBlueBackground)
            .padding(16.dp)
    ) {
        // ... (Top Bar and Search Bar sections remain the same) ...

        // 1. Top Bar: Password Vault Info & Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = cardColor,
            elevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(darkColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password Vault",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Password Vault",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Text(
                            text = "${mockPasswordEntries.size} passwords saved",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { println("Placeholder: Auto-lock button clicked") },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        elevation = ButtonDefaults.elevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
                            .height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Auto-lock time",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto-lock: 5:00", color = Color.Black, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onLockVault,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                        elevation = ButtonDefaults.elevation(0.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(8.dp))
                            .height(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Lock Vault",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock Vault", color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Search Bar and Add Password Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearchQueryChange(it)
                },
                placeholder = { Text("Search websites or usernames...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    backgroundColor = cardColor,
                    textColor = Color.Black
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(12.dp))
            Button(
                onClick = onAddPassword,
                modifier = Modifier
                    .height(56.dp)
                    .width(140.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = darkColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Password", tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Password", color = Color.White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Password List / No Passwords Found
        if (mockPasswordEntries.isEmpty()) {
            NoPasswordsFoundContent(onAddFirstPassword = onAddPassword)
        } else {
            // --- UPDATED to use LazyVerticalGrid ---
            PasswordGrid(
                passwordEntries = mockPasswordEntries,
                onEditPassword = onEditPassword,
                onDeletePassword = onDeletePassword
            )
        }
    }
}

// --- NEW/MODIFIED Helper Composables ---

@Composable
fun PasswordGrid(
    passwordEntries: List<PasswordEntry>,
    onEditPassword: (PasswordEntry) -> Unit,
    onDeletePassword: (PasswordEntry) -> Unit
) {
    // Create a grid layout using LazyColumn with rows
    // Group entries into rows of 3 items each
    val rows = passwordEntries.chunked(3)
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(rows) { rowEntries ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowEntries.forEach { entry ->
                    PasswordCard(
                        entry = entry,
                        onEdit = { onEditPassword(entry) },
                        onDelete = { onDeletePassword(entry) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Add spacers to fill remaining slots in the row (for 3-column layout)
                repeat(3 - rowEntries.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// --- Existing Helper Composables (Code is unchanged, but included for completeness) ---

@Composable
fun NoPasswordsFoundContent(onAddFirstPassword: () -> Unit) {
    // ... (No change)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "No Passwords Found",
            tint = Color.Gray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No passwords found",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start by adding your first password",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAddFirstPassword,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF333333)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Your First Password", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Your First Password", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun PasswordCard(
    entry: PasswordEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ... (No change)
    var showPassword by remember { mutableStateOf(false) }
    val darkColor = Color(0xFF333333)
    val lightGreyBackground = Color(0xFFF0F0F0)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White,
        elevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = entry.website,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Text(
                text = entry.username,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Password Display Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(lightGreyBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (showPassword) entry.passwordEncrypted else "••••••••••",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Password Visibility",
                            tint = darkColor
                        )
                    }
                    IconButton(onClick = { println("Placeholder: Copy clicked for ${entry.website}") }) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Password",
                            tint = darkColor
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Edit & Delete)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = lightGreyBackground),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.elevation(0.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Black, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit", color = Color.Black, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE57373))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}