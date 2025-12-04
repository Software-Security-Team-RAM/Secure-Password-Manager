import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"  // Updated from 1.6.10
    id("org.jetbrains.compose") version "1.5.0"  // Updated from 1.1.0
    id("org.sonarqube") version "3.5.0.2730"
    // ADDED: SQLDelight Plugin
    id("com.squareup.sqldelight") version "1.5.5"
}

group = "Software-Security-Team-RAM"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material:material-icons-extended:1.5.0")  // Updated version
    // ADDED: SQLite Driver
    implementation("com.squareup.sqldelight:sqlite-driver:1.5.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
    // This line silences the warning about Experimental features
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

// ADDED: Database Configuration
sqldelight {
    database("SafeByteDatabase") {
        packageName = "Database"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Secure-Password-Manager"
            packageVersion = "1.0.0"
        }
    }
}