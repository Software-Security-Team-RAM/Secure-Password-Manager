// Main.kt

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState


import UserInterface.CreateMasterPasswordScreen
import UserInterface.LoginScreen
import androidx.compose.runtime.Composable

// If your project structure is simpler, it might just be:
// import UserInterface.CreateMasterPasswordScreen

@Composable
fun App() {
    MaterialTheme {
        // Initially launch the Master Password creation screen
        CreateMasterPasswordScreen()
        LoginScreen()

        // When you implement navigation, you would switch between screens here:
        // when (currentScreen) {
        //     Screen.CREATE_MASTER -> CreateMasterPasswordScreen()
        //     Screen.LOGIN -> LoginScreen()
        // }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Secure Password Manager",
        state = rememberWindowState(
            width = 700.dp,
            height = 600.dp
        )
    ) {
        App()
    }
}