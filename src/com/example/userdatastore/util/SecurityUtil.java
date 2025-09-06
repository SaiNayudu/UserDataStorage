package com.example.userdatastore.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Simple AES encryption/decryption using PBKDF2 key derivation.
 * Format: [16 bytes salt][16 bytes iv][ciphertext]
 */
public class SecurityUtil {
    private static final int SALT_LEN = 16;
    private static final int IV_LEN = 16;
    private static final int ITER = 65536;
    private static final int KEY_LEN = 256;

    public static byte[] encrypt(byte[] plaintext, String passphrase) throws Exception {
        SecureRandom rnd = new SecureRandom();
        byte[] salt = new byte[SALT_LEN];
        rnd.nextBytes(salt);
        byte[] iv = new byte[IV_LEN];
        rnd.nextBytes(iv);

        SecretKey key = deriveKey(passphrase.toCharArray(), salt);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] cipherBytes = cipher.doFinal(plaintext);

        byte[] out = new byte[SALT_LEN + IV_LEN + cipherBytes.length];
        System.arraycopy(salt, 0, out, 0, SALT_LEN);
        System.arraycopy(iv, 0, out, SALT_LEN, IV_LEN);
        System.arraycopy(cipherBytes, 0, out, SALT_LEN + IV_LEN, cipherBytes.length);
        return out;
    }

    public static byte[] decrypt(byte[] fileBytes, String passphrase) throws Exception {
        if (fileBytes.length < SALT_LEN + IV_LEN)
            throw new IllegalArgumentException("invalid file");
        byte[] salt = Arrays.copyOfRange(fileBytes, 0, SALT_LEN);
        byte[] iv = Arrays.copyOfRange(fileBytes, SALT_LEN, SALT_LEN + IV_LEN);
        byte[] cipherBytes = Arrays.copyOfRange(fileBytes, SALT_LEN + IV_LEN, fileBytes.length);

        SecretKey key = deriveKey(passphrase.toCharArray(), salt);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(cipherBytes);
    }

    private static SecretKey deriveKey(char[] passphrase, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(passphrase, salt, ITER, KEY_LEN);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = skf.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}