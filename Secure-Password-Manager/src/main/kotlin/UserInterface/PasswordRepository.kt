package UserInterface

import Database.SafeByteDatabase
import javax.crypto.SecretKey

class PasswordRepository {
    // Initialize the Database
    private val driver = DatabaseDriverFactory.createDriver()
    private val database = SafeByteDatabase(driver)
    private val dbQueries = database.safeByteQueries

    // Function to add a NEW password (Encrypts it first!)
    fun addPassword(website: String, username: String, plainPass: String, masterKey: SecretKey) {
        val id = java.util.UUID.randomUUID().toString()

        // 1. Call Secure Kernel to Encrypt
        val (iv, cipherText) = CryptoManager.encrypt(plainPass.toByteArray(), masterKey)

        // 2. We need to store the salt used for the master key so we can login later.
        // For this simple version, we re-use the salt from the encryption key generation,
        // or we generate a specific salt per entry.
        // To keep it simple per your architecture: We store the IV and Ciphertext.
        // NOTE: Ideally, the Master Key Salt is stored in a separate config, but we will store
        // a dummy salt here or the actual salt if passed in, to satisfy the schema.
        val dummySalt = ByteArray(16) // In a real app, pass the actual salt used for KDF

        // 3. Save to Database
        dbQueries.insertOrReplace(
            id,
            website,
            username,
            cipherText,
            iv,
            dummySalt
        )
        println("Repository: Saved encrypted password for $website")
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
                    passwordEncrypted = plainPassword // We show it decrypted in the UI
                )
            } catch (e: Exception) {
                // If decryption fails (wrong master password), return error text
                PasswordEntry(entity.id, entity.website, entity.username, "Decryption Failed")
            }
        }
    }

    fun deletePassword(id: String) {
        dbQueries.deleteById(id)
    }
}