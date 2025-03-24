package com.citc.nce.utils;

import com.citc.nce.auth.constant.AuthError;
import com.citc.nce.common.core.exception.BizException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AesForDeveloperUtil {

    // 固定的 AES 密钥（16 字节 = 128 位）
    private static final String FIXED_KEY = "CITC5GVIDEOSMS20"; // 需要保证固定密钥长度为 16 字节

    // 加密方法
    public static String encrypt(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // 解密方法
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    // 生成128位密钥（16字节）
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 生成128位密钥
        return keyGen.generateKey();
    }

    // 加密方法
    public static String getToken(String customerId,String type)  {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(FIXED_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            String encrypt = encrypt(customerId+":"+type, secretKeySpec);
            return URLEncoder.encode(encrypt,StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            throw new BizException("生成地址失败，请联系管理员");
        }
    }

    // 加密方法
    public static String getInfo(String encryptedText) {
        try {
//            encryptedText = URLDecoder.decode(encryptedText, StandardCharsets.UTF_8.toString());
            SecretKeySpec secretKeySpec = new SecretKeySpec(FIXED_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            String decrypt = decrypt(encryptedText, secretKeySpec);
            return decrypt.split(":")[0];
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BizException(AuthError.SMS_DEVELOPER_RECEIVE_URL_ERROR);
        }
    }



    public static void main(String[] args) {
        try {
//            String originalText = "Hello, World!";
//            SecretKeySpec secretKeySpec = new SecretKeySpec(FIXED_KEY.getBytes(StandardCharsets.UTF_8), "AES");
//            // 加密
//            String encryptedText = encrypt(originalText, secretKeySpec);
//            System.out.println("Encrypted: " + encryptedText);
//
//            // 解密
//            String decryptedText = decrypt(encryptedText, secretKeySpec);
//            System.out.println("Decrypted: " + decryptedText);

            // 加密
            String encryptedText = getToken("310645060843494","video");
            System.out.println("Encrypted: " + encryptedText);
            System.out.println("M6jkwqPDItD1V5gzLv76yoEAAHye%2BjK1GJRBBIH50Gs%3D");
            // 解密
            String decryptedText = getInfo("M6jkwqPDItD1V5gzLv76yoEAAHye%2BjK1GJRBBIH50Gs%3D");
            System.out.println("Decrypted: " + decryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

