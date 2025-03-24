package com.citc.nce.common.Jasypt;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * 参考文档
 * Link @{<a href="https://blog.csdn.net/qq_45400200/article/details/131659248">...</a>}
 *
 * @author bydud
 * @since 2024/4/12
 */
public class JasyptUtils {
    public static final String salt = "hahaNoIvGenerator";
    public static final String algorithm = "PBEWITHHMACSHA512ANDAES_256";

    public static void main(String[] args) {
        String mysql = encrypt("0300431405101700");
        System.out.println("mysql = " + mysql);
        System.out.println(decrypt(salt, "wxkMK82QA05UbGIyOsJ4COfl+iFQuYZvO+ZUfMKiDUXMCNLN30wnB/j9JNRrr39YtH+hTWXo7hcpPBG0ELMtCg=="));
    }

    /**
     * @Description: textToEncrypt, 需要加密的明文, salt，加密的盐，需要与解密保持一致, algorithm，加密算法，需要与解密算法保持一致
     **/
    public static String encrypt(String textToEncrypt) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        // 2. 加解密配置
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(salt);
        // 3. 加密算法，需要与解密算法一致
        config.setAlgorithm(algorithm);
        // 为减少配置文件的书写，以下都是 Jasyp 3.x 版本，配置文件默认配置
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        // 4. 加密
        return encryptor.encrypt(textToEncrypt);
    }


    /**
     * @Description: textToDecrypt, 需要解密的密文, salt，解密的盐，需要与加密保持一致, algorithm，解密算法，需要与加密算法保持一致
     **/
    public static String decrypt(String salt, String textToDecrypt) {
        // 1. 创建加解密工具实例
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        // 2. 加解密配置
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(salt);
        // 3. 解密算法，必须与加密算法一致
        config.setAlgorithm(algorithm);
        // 为减少配置文件的书写，以下都是 Jasyp 3.x 版本，配置文件默认配置
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        // 4. 解密
        return encryptor.decrypt(textToDecrypt);
    }
}
