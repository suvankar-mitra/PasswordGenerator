package com.suvankarmitra.passwordgenerator.util;

import android.support.annotation.NonNull;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    private static final String[] SPECIAL_CHARS = {"@","#","$","%","&","*","_","^"};

    public static String generateRandomPassword (int length, boolean useLetters,
                                                 boolean useNumbers, boolean useSpecialChars) {
        String generated = generateRandomPassword(length, useLetters, useNumbers);
        if (useSpecialChars) {
            int replaceCount = getRandomNumberInRange(1,length/2);
            for(int i=0; i<replaceCount; i++) {
                int posToReplace = getRandomNumberInRange(0, generated.length()-1);
                int specialRandom = getRandomNumberInRange(0, SPECIAL_CHARS.length-1);
                /*if(!Character.isAlphabetic(generated.charAt(posToReplace))) {
                    continue;
                }*/
                String from = String.valueOf(generated.charAt(posToReplace));
                String to = SPECIAL_CHARS[specialRandom];
                generated = generated.replaceFirst(from, to);
            }
        }
        return generated;
    }

    @NonNull
    private static String generateRandomPassword(int length, boolean useLetters, boolean useNumbers) {
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    private static int getRandomNumberInRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }




}
