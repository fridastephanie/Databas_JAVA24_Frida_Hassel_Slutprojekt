package se.gritacademy.fulkopingsbibliotek.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHashing {
    //Skapar längden på saltet
    private static final int SALT_LENGTH = 16;

    /**
     * Skapar ett slumpmässigt salt
     *
     * @return En byte-array som representerar saltet
     */
    private static byte[] createSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Skapar en hash av lösenordet genom att använda SHA-256 samt lägger till salt
     *
     * @param inputPassword - Användarens inmatade lösenord som ska hashas
     * @return - En sträng med det hashade lösenordet inklusive saltet
     */
    public static String createHash(String inputPassword) {
        byte[] salt = createSalt();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedInputPassword = digest.digest(inputPassword.getBytes(StandardCharsets.UTF_8));
            return byteArrayToHexString(hashedInputPassword) + byteArrayToHexString(salt);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    /**
     * Verifierar om det inmatade lösenordet matchar det hashade lösenordet som finns lagrat
     *
     * @param inputPassword - Användarens inmatade lösenord som ska verifieras
     * @param storedHashedPassword - Användarens sparade lösenord som är hashat och saltat
     * @return - True om det inmatade lösenordet matchar det sparade lösenordet, annars False
     */
    public static boolean verifyPassword(String inputPassword, String storedHashedPassword) {
        String hashedPart = storedHashedPassword.substring(0, 64);
        String saltPart = storedHashedPassword.substring(64);

        byte[] salt = hexStringToByteArray(saltPart);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            byte[] hashedInputPassword = digest.digest(inputPassword.getBytes(StandardCharsets.UTF_8));
            String hashedInputPasswordHex = byteArrayToHexString(hashedInputPassword);
            return hashedPart.equals(hashedInputPasswordHex);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    /**
     * Konverterar en byte-array (hashad sträng) till en hex-sträng
     *
     * @param byteArray - Byte-array som ska konverteras till hex-sträng
     * @return - En hex-sträng som motsvarar den givna byte-arrayen
     */
    private static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder sb = new StringBuilder();
        for (byte b : byteArray) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Konverterar en hex-sträng till en byte-array
     *
     * @param hexString - Hex-sträng som representerar de ursprungliga byte-värdena
     * @return - Byte-array som motsvarar hex-strängen
     */
    private static byte[] hexStringToByteArray(String hexString) {
        int length = hexString.length();
        byte[] data = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}