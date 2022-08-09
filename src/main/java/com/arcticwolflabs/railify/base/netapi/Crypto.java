package com.arcticwolflabs.railify.base.netapi;

import androidx.annotation.Nullable;
import android.util.Base64;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    public static final int INIT_VECTOR_LENGTH = 16;
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    protected String data;
    protected String initVector;
    protected String errorMessage;

    private Crypto() {
        super();
    }

    private Crypto(@Nullable String initVector, @Nullable String data, @Nullable String errorMessage) {
        super();

        this.initVector = initVector;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static Crypto encrypt(String secretKey, String plainText) {
        String initVector = null;
        try {
            if (!isKeyLengthValid(secretKey)) {
                throw new Exception("Secret key's length must be 128, 192 or 256 bits");
            }

            SecureRandom secureRandom = new SecureRandom();
            byte[] initVectorBytes = new byte[INIT_VECTOR_LENGTH / 2];
            secureRandom.nextBytes(initVectorBytes);
            initVector = bytesToHex(initVectorBytes);
            initVectorBytes = initVector.getBytes("UTF-8");

            IvParameterSpec ivParameterSpec = new IvParameterSpec(initVectorBytes);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

            ByteBuffer byteBuffer = ByteBuffer.allocate(initVectorBytes.length + encrypted.length);
            byteBuffer.put(initVectorBytes);
            byteBuffer.put(encrypted);

            String result = Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT);

            return new Crypto(initVector, result, null);
        } catch (Throwable t) {
            t.printStackTrace();
            return new Crypto(initVector, null, t.getMessage());
        }
    }


    public static Crypto decrypt(String secretKey, String cipherText) {
        try {
            if (!isKeyLengthValid(secretKey)) {
                throw new Exception("Secret key's length must be 128, 192 or 256 bits");
            }

            byte[] encrypted = Base64.decode(cipherText, Base64.DEFAULT);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(encrypted, 0, INIT_VECTOR_LENGTH);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            String result = new String(cipher.doFinal(encrypted, INIT_VECTOR_LENGTH, encrypted.length - INIT_VECTOR_LENGTH));

            return new Crypto(bytesToHex(ivParameterSpec.getIV()), result, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Crypto(null, null, e.getMessage());
        }
    }

    public static boolean isKeyLengthValid(String key) {
        return key.length() == 16 || key.length() == 24 || key.length() == 32;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public String getData() {
        return data;
    }

    public String getInitVector() {
        return initVector;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return this.errorMessage != null;
    }

    public String toString() {
        return getData();
    }


}
