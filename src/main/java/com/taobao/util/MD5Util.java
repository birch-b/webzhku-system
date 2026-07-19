package com.taobao.util;

import org.mindrot.jbcrypt.BCrypt;

import java.security.MessageDigest;

public class MD5Util {
    public static String encrypt(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }

    public static boolean verify(String input, String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }
        if (hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$")) {
            return BCrypt.checkpw(input, hash);
        }
        String md5Hash = md5Encrypt(input);
        return md5Hash.equalsIgnoreCase(hash);
    }

    private static String md5Encrypt(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}
