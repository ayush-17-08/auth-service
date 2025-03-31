package com.infinity.commerce.auth_service.utlis;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;


public class PasswordUtils {

    // Hash a password
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(10)); // 10 is the work factor
    }

    // Verify a password
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
