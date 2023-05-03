package authenticator.utils;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtils {

    private static final String ALGORITHM = "SHA-256";
    private static final String ENCODING = "UTF-8";

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] passwordBytes = password.getBytes(ENCODING);
            byte[] hashBytes = digest.digest(passwordBytes);
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

}