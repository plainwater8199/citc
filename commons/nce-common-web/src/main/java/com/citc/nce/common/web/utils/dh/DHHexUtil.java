package com.citc.nce.common.web.utils.dh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.crypto.KeyAgreement;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @author bydud
 * @since 2024/7/16 11:43
 */
@Slf4j
public class DHHexUtil {

    // TokenSession key
    public static final String DH_SHARE_SECRET_KEY = "dh_shareSecretKey";

    //not creat object
    private DHHexUtil() {
    }

    /**
     * To byte array byte [ ].
     *
     * @param hexString the hex string
     * @return the byte [ ]
     */
    public static byte[] toByteArray(String hexString) {
        Assert.hasLength(hexString, "hexString must not be empty");
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() >> 1];
        int index = 0;
        for (int i = 0; i < hexString.length(); i++) {
            if (index > hexString.length() - 1)
                return byteArray;
            byte highDit = (byte) (Character.digit(hexString.charAt(index), 16) & 0xFF);
            byte lowDit = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xFF);
            byteArray[i] = (byte) (highDit << 4 | lowDit);
            index += 2;
        }
        return byteArray;
    }


    /**
     * byte[] to Hex string.
     *
     * @param byteArray the byte array
     * @return the string
     */

    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length <= 0) {
            return null;
        }
        final StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hexString.append(0);
            }
            hexString.append(hv);
        }
        return hexString.toString().toLowerCase();
    }

    /**
     * @param salt  用户计算偏移量，前后端一致
     * @param bytes dh获取的共享密码
     */
    public static String getAesHexString(String salt, byte[] bytes) {
        int length = 32;//32 字节用于 AES-256
        int group = bytes.length / length;
        int offset = Math.abs(salt.hashCode()) % group * length / 2;
        if (bytes.length == 0) {
            throw new IllegalArgumentException("Empty key");
        } else if (bytes.length - offset < length) {
            throw new IllegalArgumentException("Invalid offset/length combination");
        } else {
            byte[] key = new byte[length];
            System.arraycopy(bytes, offset, key, 0, length);
            return DHHexUtil.toHexString(key);
        }
    }

    public static ECDH initECDH() {
        return new ECDH();
    }

    /**
     * 共享秘钥用户工具类
     *
     * @author bydud
     * @since 2024/7/16 9:35
     */
    public static class ECDH {

        private PublicKey publicKey;
        private PrivateKey privateKey;

        private ECDH() {
            generateKeyPair();
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        // 生成本地KeyPair:
        private void generateKeyPair() {
            try {
                ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp521r1");
                KeyPairGenerator kpGen = KeyPairGenerator.getInstance("EC");
                kpGen.initialize(ecSpec);
                KeyPair kp = kpGen.generateKeyPair();
                this.privateKey = kp.getPrivate();
                this.publicKey = kp.getPublic();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

        public byte[] generateShareSecretKey(byte[] receivedPubKeyBytes) {
            try {
                // 从byte[]恢复PublicKey:
                PublicKey receivedPublicKey = KeyFactory.getInstance("ECDH", "BC")
                        .generatePublic(new X509EncodedKeySpec(receivedPubKeyBytes));
                KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", "BC");
                keyAgreement.init(privateKey);
                keyAgreement.doPhase(receivedPublicKey, true); // 对方的PublicKey
                // 生成SecretKey密钥:
                return keyAgreement.generateSecret();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
