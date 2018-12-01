package com.suvankarmitra.passwordgenerator.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class PasswordUtil {

    private Context mContext;

    public PasswordUtil(Context context) {
        mContext = context;
    }

    public HashMap<String, byte[]> encryptBytes(byte[] plainTextBytes, String passwordString)
            throws NoSuchPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException,
            InvalidKeyException, InvalidKeySpecException {

        HashMap<String, byte[]> map = new HashMap<>();
        //Random salt for next step
        SecureRandom random = new SecureRandom();
        byte salt[] = new byte[256];
        random.nextBytes(salt);
        //PBKDF2 - derive the key from the password, don't use passwords directly
        char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
        PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        //Create initialization vector for AES
        SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
        byte[] iv = new byte[16];
        ivRandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        //Encrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(plainTextBytes);
        map.put("salt", salt);
        map.put("iv", iv);
        map.put("encrypted", encrypted);
        return map;
    }

    public byte[] decryptData(HashMap<String, byte[]> map, String passwordString)
            throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException,
            IllegalBlockSizeException {

        byte[] decrypted;
        byte salt[] = map.get("salt");
        byte iv[] = map.get("iv");
        byte encrypted[] = map.get("encrypted");

        //regenerate key from password
        char[] passwordChar = passwordString.toCharArray();
        PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        //Decrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public void saveMapToDisk(Map<String, byte[]> map, String fileName) throws IOException {
        FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        oos.close();
    }

    public HashMap<String, byte[]> getMapFromDisk(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream fis = mContext.openFileInput(fileName);
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap<String, byte[]> map = (HashMap<String, byte[]>) ois.readObject();
        return map;
    }

    public static String getSaltString(int length, boolean useLetters,
                                       boolean useNumbers, boolean useSpecialChars) {
        String SALTCHARS_ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String SALTCHARS_NUMERIC = "1234567890";
        String SALT_CHARS_SPECIAL = "@#$%&*_^";
        String SALTCHARS = "";
        if (useLetters) {
            SALTCHARS += SALTCHARS_ALPHA;
        }
        if (useNumbers) {
            SALTCHARS += SALTCHARS_NUMERIC;
        }
        if (useSpecialChars) {
            SALTCHARS += SALT_CHARS_SPECIAL;
        }
        StringBuilder salt = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        if (useSpecialChars) {
            if (!salt.toString().matches("^.*[@#$%&*_^]+.*$")) {
                SecureRandom rnd2 = new SecureRandom();
                int idx1 = (int) (rnd2.nextFloat() * salt.length());
                int idx2 = (int) (rnd2.nextFloat() * SALT_CHARS_SPECIAL.length());
                salt.replace(idx1, idx1 + 1, String.valueOf(SALT_CHARS_SPECIAL.charAt(idx2)));
            }
        }
        if (useNumbers) {
            if (!salt.toString().matches("^.*[0-9]+.*$")) {
                SecureRandom rnd2 = new SecureRandom();
                int idx1 = (int) (rnd2.nextFloat() * salt.length());
                int idx2 = (int) (rnd2.nextFloat() * SALTCHARS_NUMERIC.length());
                salt.replace(idx1, idx1 + 1, String.valueOf(SALTCHARS_NUMERIC.charAt(idx2)));
            }
        }
        if (useLetters) {
            if (!salt.toString().matches("^.*[a-zA-Z]+.*$")) {
                SecureRandom rnd2 = new SecureRandom();
                int idx1 = (int) (rnd2.nextFloat() * salt.length());
                int idx2 = (int) (rnd2.nextFloat() * SALTCHARS_ALPHA.length());
                salt.replace(idx1, idx1 + 1, String.valueOf(SALTCHARS_ALPHA.charAt(idx2)));
            }
        }
        return salt.toString();
    }

    public static boolean isStrong(String password){
        boolean ok = password.matches("^.*[0-9]+.*$");
        ok &= password.matches("^.*[a-z]+.*$");
        ok &= password.matches("^.*[A-Z]+.*$");
        ok &= password.matches("^.*[@#$^%&*_]+.*$");
        ok &= password.length()>=8;
        return ok;
    }
}
