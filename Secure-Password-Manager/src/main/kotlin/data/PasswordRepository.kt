/**
* Created largly using Gemini Script
* Implements password storage (with encryption) and retreival (with decryption)
*/



package data

import Database.SafeByteDatabase
import encryption.CryptoManager
import ui.PasswordEntry
import javax.crypto.SecretKey

class PasswordRepository {
    // Initialize the Database
    private val driver = DatabaseDriverFactory.createDriver()
    private val database = SafeByteDatabase(driver)
    private val dbQueries = database.safeByteQueries

    // Function to add a NEW password
    fun addPassword(website: String, username: String, plainPass: String, notes: String, masterKey: SecretKey) {
        val id = java.util.UUID.randomUUID().toString()
        saveToDb(id, website, username, plainPass, notes, masterKey)
        println("Repository: Added password for $website")
    }

    // Function to UPDATE an existing password
    fun updatePassword(id: String, website: String, username: String, plainPass: String, notes: String, masterKey: SecretKey) {
        saveToDb(id, website, username, plainPass, notes, masterKey)
        println("Repository: Updated password for $website")
    }

    // Helper function to handle the encryption and saving logic
    private fun saveToDb(id: String, website: String, username: String, plainPass: String, notes: String, masterKey: SecretKey) {
        // 1. Encrypt
        val (iv, cipherText) = CryptoManager.encrypt(plainPass.toByteArray(), masterKey)
        // Dummy salt for database schema compliance (real salt is managed by LoginScreen/File)
        val dummySalt = ByteArray(16)

        // 2. Insert or Replace (SQLDelight handles the update automatically if ID exists)
        dbQueries.insertOrReplace(
            id,
            website,
            username,
            cipherText,
            iv,
            dummySalt,
            notes // <--- The 7th argument (Notes)
        )
    }

    // Function to get ALL passwords (Decrypts them!)
    fun getAllPasswords(masterKey: SecretKey): List<PasswordEntry> {
        val entities = dbQueries.selectAll().executeAsList()

        return entities.map { entity ->
            try {
                // 1. Call Secure Kernel to Decrypt
                val decryptedBytes = CryptoManager.decrypt(entity.ciphertext, entity.iv, masterKey)
                val plainPassword = String(decryptedBytes)

                // 2. Return the UI Model
                PasswordEntry(
                    id = entity.id,
                    website = entity.website,
                    username = entity.username,
                    passwordEncrypted = plainPassword, // Shown decrypted in the list
                    notes = entity.notes ?: "" // Load notes (default to empty string if null)
                )
            } catch (e: Exception) {
                // Return "Decryption Failed" if the wrong key was used
                PasswordEntry(entity.id, entity.website, entity.username, "Decryption Failed", "")
            }
        }
    }

    fun deletePassword(id: String) {
        dbQueries.deleteById(id)
    }
}