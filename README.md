# Secure-Password-Manager

Secure Password Manager is an application that hosts a local database on the user’s computer to store different passwords. The application will initially open to allow the user to create a master password that will be used to access the stored passwords. The master password is hashed for security purposes. Following the initial password setup, the user can add passwords to be saved with the website, email, and any notes. These passwords are encrypted for security purposes. When adding a password, the user can toggle over to generate a random secure password. During this process, the user is also able to select different specifications such as password length. Passwords can be edited, viewed, and copied if the application is unlocked. The user can lock the application at any time.

Options for Running Application
 1. Run Main.kt in IntelliJ using Java 17 or 21
 2. Create Executable files for Host Machine
     - Run following commands on Project Folder
        - chmod +x ./gradlew
        - ./gradlew clean
        - ./gradlew package
     - Executable file will be generated in /Secure-Password-Manager/Secure-Password-Manager/build/compose/binaries/main/
  
