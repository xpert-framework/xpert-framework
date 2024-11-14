package com.xpert.utils;

import com.xpert.security.EncryptionType;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to generate Hash
 *
 * @author ayslan
 */
public class Encryption implements Serializable {

    private static final long serialVersionUID = -8043153141256188452L;

    /**
     * Return a string based on java MessageDigest API
     *
     * @param algorithm
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMessageDigest(String algorithm, String string) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] hash = md.digest(string.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Return a hash based on MD5
     *
     * @param string
     * @param salt
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(String string, String salt) throws NoSuchAlgorithmException {
        if (salt != null) {
            string = salt + string;
        }
        return getMessageDigest(EncryptionType.MD5.getCode(), string);
    }

    /**
     * Return a hash based on MD5
     *
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getMD5(String string) throws NoSuchAlgorithmException {
        return getMD5(string, null);
    }

    /**
     * Return a hash based on SHA-256
     *
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA256(String string) throws NoSuchAlgorithmException {
        return getMessageDigest(EncryptionType.SHA256.getCode(), string);
    }

    /**
     * Return a hash based on SHA-512
     *
     * @param string
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSHA512(String string) throws NoSuchAlgorithmException {
        return getMessageDigest(EncryptionType.SHA512.getCode(), string);
    }

}
