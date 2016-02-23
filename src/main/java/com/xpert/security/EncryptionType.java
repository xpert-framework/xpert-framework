package com.xpert.security;

/**
 *
 * Define diferents types of Encryption (or Hash Functions)
 *
 * @author ayslan
 */
public enum EncryptionType {

    MD5("MD5"), SHA256("SHA-256"), SHA512("SHA-512");

    private final String code;

    private EncryptionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
