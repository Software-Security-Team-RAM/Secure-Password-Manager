import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.runtime.*
import UserInterface.HomeScreen
import UserInterface.LoginScreen
// CryptoManager is not needed in Main, so I removed it to keep it clean

@Composable
fun App() {
    MaterialTheme {
        // State to track which screen is active
        var currentScreen by remember { mutableStateOf("login") }

        // We hold the Master Key here in memory while the app is open
        var masterKey by remember { mutableStateOf<javax.crypto.SecretKey?>(null) }

        when (currentScreen) {
            "login" -> {
                LoginScreen(
                    onLoginSuccess = { key ->
                        masterKey = key
                        currentScreen = "home"
                        println("Navigation: Switching to Home Screen")
                    }
                )
            }
            "home" -> {
                if (masterKey != null) {
                    HomeScreen(masterKey!!)
                }
            }
        } // <--- THIS WAS MISSING
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
