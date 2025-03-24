package com.citc.nce.common.web.utils.dh;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Security;

/**
 * 交换秘钥算法服务类
 * 1、注册提供者
 * 2、获取iv
 * 3、加解密数据
 *
 * @author bydud
 * @since 2024/7/17 15:20
 */
@Slf4j
@Component
@RefreshScope
public class ECDHService {
    @Value("${security.common.aes.desensitization}")
    private String IV_KEY;

    static {
        // 检查算法
        if (null == Security.getProvider("BC")) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            log.info("register BouncyCastleProvider");
        }
    }

    //解密字符串
    public String decode(String encode) {
        String dh_AES = StpUtil.getTokenSession().getString(DHHexUtil.DH_SHARE_SECRET_KEY);
        System.out.println("cao-------decode------------------dh_AES:"+dh_AES);
        System.out.println("cao-------decode------------------IV_KEY:"+IV_KEY);
        if (!StringUtils.hasLength(dh_AES)) {
            throw new BizException(401, "秘钥丢失请重新登录");
        }
        return decrypt(encode, dh_AES);
    }

    //加密字符串
    public String encode(String str) {
        if (!StringUtils.hasLength(str)) {
            return "";
        }
        String dh_AES = StpUtil.getTokenSession().getString(DHHexUtil.DH_SHARE_SECRET_KEY);
        System.out.println("cao-----encode-----dh_AES:"+dh_AES);
        if (!StringUtils.hasLength(dh_AES)) {
            throw new BizException(401, "秘钥丢失请重新登录");
        }
        return encrypt(str, dh_AES);
    }


    public String encrypt(String message, String key) {
        System.out.println("cao-------------encrypt------------message:"+message);
        System.out.println("cao-------------encrypt------------IV_KEY:"+IV_KEY);
        byte[] baseKey = HexUtil.decodeHex(key);
        byte[] ivBytes = IV_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, baseKey, ivBytes);
        return Base64.encode(aes.encrypt(messageBytes));
    }

    public String decrypt(String message, String key) {
        byte[] baseKey = HexUtil.decodeHex(key);
        byte[] ivBytes = IV_KEY.getBytes(StandardCharsets.UTF_8);
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, baseKey, ivBytes);
        return aes.decryptStr(message);
    }

}
