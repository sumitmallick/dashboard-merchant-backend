package com.freewayemi.merchant.commons.utils;

import org.apache.commons.codec.DecoderException;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

public class EncryptionUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    public static final int GCM_TAG_LENGTH = 16;

    public static String encrypt(String request, String symmetricKey, String ivKey, String secretKeyType,
                                 String cipherAlgo)
            throws DecoderException, NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = getCipher(symmetricKey, ivKey, secretKeyType, cipherAlgo, Cipher.ENCRYPT_MODE);
        return Base64.getEncoder().encodeToString(cipher.doFinal(request.getBytes()));
    }

    public static String decrypt(String encryptedRequest, String symmetricKey, String ivKey, String secretKeyType,
                                 String cipherAlgo)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = getCipher(symmetricKey, ivKey, secretKeyType, cipherAlgo, Cipher.DECRYPT_MODE);
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(encryptedRequest));
        return new String(result);
    }

    private static Cipher getCipher(String symmetricKey, String ivKey, String secretKeyType, String cipherAlgo,
                                    int opMode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException {

        String transformation = StringUtils.hasText(cipherAlgo) ? cipherAlgo : AES_GCM_NO_PADDING;

        SecretKeySpec secretKeySpec;
        if (!StringUtils.isEmpty(secretKeyType)) {
            if ("String".equalsIgnoreCase(secretKeyType)) {
                secretKeySpec = new SecretKeySpec(symmetricKey.getBytes(), AES_ALGORITHM);
            } else if ("Hex".equalsIgnoreCase(secretKeyType)) {
                secretKeySpec = new SecretKeySpec(Hex.decode(symmetricKey), AES_ALGORITHM);
            } else {
                secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(symmetricKey.getBytes()), AES_ALGORITHM);
            }
        } else if (StringUtils.hasText(cipherAlgo)) {
            // For merchant specific cipher, using key as is without any transformation. If specific transformation
            // is required then that can be addressed by using secretKeyType parameter
            secretKeySpec = new SecretKeySpec(symmetricKey.getBytes(), AES_ALGORITHM);
        } else {
            secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(symmetricKey.getBytes()), AES_ALGORITHM);
        }

        Cipher cipher = Cipher.getInstance(transformation);

        AlgorithmParameterSpec algorithmParameterSpec;
        if (AES_GCM_NO_PADDING.equals(transformation)) {
            algorithmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, ivKey.getBytes());
        } else {
            algorithmParameterSpec = new IvParameterSpec(ivKey.getBytes());
        }
        cipher.init(opMode, secretKeySpec, algorithmParameterSpec);

        return cipher;
    }
}
