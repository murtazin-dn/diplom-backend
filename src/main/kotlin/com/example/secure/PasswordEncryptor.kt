package com.example.secure

import org.mindrot.jbcrypt.BCrypt

object PasswordEncryptor {
    fun validatePassword(attempt: String, userPassword: String): Boolean {
        return BCrypt.checkpw(attempt, userPassword)
    }

    fun encryptPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}