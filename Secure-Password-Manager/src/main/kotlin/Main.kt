import androidx.compose.material.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.runtime.*
import UserInterface.HomeScreen
import UserInterface.LoginScreen
import UserInterface.CreateMasterPasswordScreen
import java.io.File

@Composable
fun App() {
    MaterialTheme {
        // Check if this is the first launch (master password hasn't been created yet)
        val saltFile = File("safebyte.salt")
        val isFirstLaunch = !saltFile.exists()
        
        // State to track which screen is active
        var currentScreen by remember { 
            mutableStateOf(if (isFirstLaunch) "createPassword" else "login") 
        }

        // We hold the Master Key here in memory while the app is open
        var masterKey by remember { mutableStateOf<javax.crypto.SecretKey?>(null) }

        when (currentScreen) {
            "createPassword" -> {
                CreateMasterPasswordScreen(
                    onPasswordCreated = { key ->
                        masterKey = key
                        currentScreen = "home"
                        println("Navigation: Master password created, switching to Home Screen")
                    }
                )
            }
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
        }
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
