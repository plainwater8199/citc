package com.citc.nce.authcenter.auth;

import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.citc.nce.common.web.utils.dh.DHHexUtil;

import java.security.Security;

/**
 * @author bydud
 * @since 2024/7/17 15:01
 */
public class ECDHTEST {
    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        SymmetricCrypto aes = new AES(Mode.ECB, Padding.PKCS5Padding, DHHexUtil.toByteArray("015c8c189d254a70fd63ee2ee5a2ff5b"));
        String s = aes.decryptStr(DHHexUtil.toByteArray("55b71106f64e914e4e288697920f769ec3576694f4cfac1384bc720e50ee368a"));
        System.out.println(s);
    }

    private static void change() {
        DHHexUtil.ECDH bob = DHHexUtil.initECDH();
        byte[] encoded = bob.getPublicKey().getEncoded();
        System.out.println(DHHexUtil.toHexString(encoded));
        byte[] pk = {};
        byte[] secretKey = bob.generateShareSecretKey(pk);
        long time = 0;
        String hexString = DHHexUtil.getAesHexString(String.valueOf(time), secretKey);
        System.out.println("hexString = " + hexString);
        SymmetricCrypto aes = new AES(Mode.CTS, Padding.PKCS5Padding, DHHexUtil.toByteArray(hexString), "0200430405101708".getBytes());
        System.out.println("bob.encode = " + aes.encryptHex("大中国小猫咪"));
    }
}
