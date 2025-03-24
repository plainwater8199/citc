package com.citc.nce.authcenter.utils;

import com.cuca.security.algorithm.impl.soft.SoftSM2;
import com.cuca.security.soft.sm2.SM2PrivateKey;
import com.cuca.security.util.KeyFromDER;

import java.security.PublicKey;

/**
 * 签名生成演示方法
 * 需要导入resource/lib/cucasecuritysdk-1.7.jar包
 * 导入方法：
 * 1.手动构建到maven本地仓库：mvn install:install-file -Dfile=cucasecuritysdk-1.7.jar包存放位置 -DgroupId=com.cuca -DartifactId=cucasecuritysdk -Dversion=1.7 -Dpackaging=jar
 * 2.maven依赖：
 * <dependency>
 * <groupId>com.cuca</groupId>
 * <artifactId>cucasecuritysdk</artifactId>
 * <version>1.7</version>
 * </dependency>
 */
public class SignUtil {

    private static final SoftSM2 SM2 = new SoftSM2();
    private static final String SIGN_ALG = "SM3withSM2";

    /**
     * 签名
     *
     * @param priKey Base64私钥字符串
     * @param inData 签名原文
     * @return 签名值
     */
    public static byte[] sign(String priKey, byte[] inData) {
        SM2PrivateKey privateKey = KeyFromDER.getSM2PrivateKey("", priKey);
        return SM2.externalSign(SIGN_ALG, privateKey, inData);
    }


    /**
     * @param pubkey   Base64公钥字符串
     * @param signData 签证后的字符串
     * @param inData   原始字符串
     * @return
     */
    public static boolean verify(String pubkey, byte[] signData, byte[] inData) {
        PublicKey publicKey = KeyFromDER.getSM2PublicKey(pubkey);
        return SM2.externalVerify(SIGN_ALG, publicKey, signData, inData);
    }
}
