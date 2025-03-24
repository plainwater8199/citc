package com.citc.nce.common.util;


import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author bydud
 * @date 2022/11/3
 **/

@Slf4j
public class RsaUtil {

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
    }

    private static final String KEY_TYPE = "RSA";

    // 最大明文加密长度（单位：字节）
    private final static int MAX_ENCRYPT_BLOCK = 318;

    // 最大密文解密长度（单位：字节）
    private final static int MAX_DECRYPT_BLOCK = 384;

    private static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    public static void main(String[] args) throws Exception {
        RsaKeyPair rsaKeyPair = generateKeyPair();
        System.out.println("rsaKeyPair.getPublicKey() = " + rsaKeyPair.getPublicKey());
        System.out.println("rsaKeyPair.getPrivateKey() = " + rsaKeyPair.getPrivateKey());
    }


    /**
     * 构建RSA密钥对
     *
     * @return 生成后的公私钥信息
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_TYPE);
        keyPairGenerator.initialize(3072);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64Utils.encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = Base64Utils.encodeToString(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {
        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    /**
     * 公钥加密
     *
     * @param publicKeyString 公钥
     * @param text            待加密的文本
     * @return 加密后的文本
     */
    public static String encryptByPublicKey(String publicKeyString, String text) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64Utils.encodeToString(result);
        } catch (Exception e) {
            log.error("encryptByPublicKey加密失败",e);
            throw new BizException(40156008, "加密失败");
        }
    }

    /**
     * 公钥解密
     *
     * @param publicKeyString 公钥
     * @param text            待解密的信息
     * @return 解密后的文本
     */
    public static String decryptByPublicKey(String publicKeyString, String text) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(Base64Utils.decodeFromString(text));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decryptByPublicKey解密失败",e);
            throw new BizException(40156009, "解密失败");
        }
    }

    /**
     * 私钥加密
     *
     * @param privateKeyString 私钥
     * @param text             待加密的信息
     * @return 加密后的文本
     */
    public static String encryptByPrivateKey(String privateKeyString, String text) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64Utils.encodeToString(result);
        } catch (Exception e) {
            log.error("encryptByPrivateKey加密失败",e);
            throw new BizException(40156008, "加密失败");
        }
    }

    /**
     * 私钥解密
     *
     * @param privateKeyString 私钥
     * @param text             待解密的文本
     * @return 解密后的文本
     */
    public static String decryptByPrivateKey(String privateKeyString, String text) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyString));
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_TYPE);
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal(Base64Utils.decodeFromString(text));
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decryptByPrivateKey解密失败",e);
            throw new BizException(40156009, "解密失败");
        }
    }


    public static Key getPrivateKey(String privateKeyString) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyString));
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_TYPE);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static Key getPublicKey(String publicKeyString){
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKeyString));
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_TYPE);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 非对称加密数据
     *
     * @param input : 原文
     * @param key   : 密钥
     * @return : 密文
     * @throws Exception
     */
    public static String encryptByAsymmetric(String input, Key key) {
        try {
            // 获取Cipher对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化模式(加密)和密钥
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] resultBytes = getMaxResultEncrypt(input, cipher);
            return Base64Utils.encodeToString(resultBytes);
        } catch (Exception e) {
            log.error("encryptByAsymmetric加密失败",e);
            throw new RuntimeException("加密失败！");
        }
    }

    /**
     * 非对称解密数据
     *
     * @param encrypted : 密文
     * @param key       : 密钥
     * @return : 原文
     * @throws Exception
     */
    public static String decryptByAsymmetric(String encrypted, Key key) {
        try {
            // 获取Cipher对象
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            // 初始化模式(解密)和密钥
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(getMaxResultDecrypt(encrypted, cipher), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decryptByAsymmetric解密失败",e);
            throw new RuntimeException("解密失败！");
        }
    }

    /**
     * 分段处理加密数据
     *
     * @param input  : 加密文本
     * @param cipher : Cipher对象
     * @return
     */
    private static byte[] getMaxResultEncrypt(String input, Cipher cipher) throws Exception {
        byte[] inputArray = input.getBytes(StandardCharsets.UTF_8);
        int inputLength = inputArray.length;
        // 最大加密字节数，超出最大字节数需要分组加密
        // 标识
        int offSet = 0;
        byte[] resultBytes = {};
        while (inputLength - offSet > 0) {
            byte[] cache;
            if (inputLength - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(inputArray, offSet, MAX_ENCRYPT_BLOCK);
                offSet += MAX_ENCRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                offSet = inputLength;
            }
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
        }
        return resultBytes;
    }

    /**
     * 分段处理解密数据
     *
     * @param decryptText : 加密文本
     * @param cipher      : Cipher对象
     * @throws Exception
     */
    private static byte[] getMaxResultDecrypt(String decryptText, Cipher cipher) throws Exception {
        byte[] inputArray = Base64Utils.decode(decryptText.getBytes(StandardCharsets.UTF_8));
        int inputLength = inputArray.length;

        // 最大解密字节数，超出最大字节数需要分组加密
        // 标识
        int offSet = 0;
        byte[] resultBytes = {};
        while (inputLength - offSet > 0) {
            byte[] cache;
            if (inputLength - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(inputArray, offSet, MAX_DECRYPT_BLOCK);
                offSet += MAX_DECRYPT_BLOCK;
            } else {
                cache = cipher.doFinal(inputArray, offSet, inputLength - offSet);
                offSet = inputLength;
            }
            resultBytes = Arrays.copyOf(resultBytes, resultBytes.length + cache.length);
            System.arraycopy(cache, 0, resultBytes, resultBytes.length - cache.length, cache.length);
        }
        return resultBytes;
    }

}
