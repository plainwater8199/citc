package com.citc.nce.conf;

import com.citc.nce.common.Jasypt.JasyptUtils;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.util.EdaFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author jcrenc
 * @since 2024/3/20 15:48
 */
@ConfigurationProperties(prefix = "security.common.keypair")
@Configuration
@Slf4j
public class CommonKeyPairConfig {
    private String publicKeyPath;
    private String privateKeyPath;
    private String salt;

    public void setSalt(String salt) {
        this.salt = salt;
    }

    //不在nacos中配置
    private String publicKey;
    private String privateKey;


    public String getPublicKey() {
        if (!StringUtils.hasLength(publicKey)) {
            try {
                log.info("slat_slat_slat {}", salt);
                log.info("publicKey {}",EdaFileUtil.readToString(publicKeyPath));
                publicKey = JasyptUtils.decrypt(salt, EdaFileUtil.readToString(publicKeyPath));
            } catch (IOException ioException) {
                throw new BizException("秘钥获取失败");
            }
        }
        return publicKey;
    }

    public String getPrivateKey() {
        if (!StringUtils.hasLength(privateKey)) {
            try {
                log.info("slat_slat_slat {}", salt);
                log.info("privateKey {}",EdaFileUtil.readToString(privateKeyPath));
                privateKey =JasyptUtils.decrypt(salt, EdaFileUtil.readToString(privateKeyPath));
            } catch (IOException ioException) {
                throw new BizException("秘钥获取失败");
            }
        }
        return privateKey;
    }

    public String getPublicKeyPath() {
        return publicKeyPath;
    }

    public void setPublicKeyPath(String publicKeyPath) {
        this.publicKeyPath = publicKeyPath;
    }

    public String getPrivateKeyPath() {
        return privateKeyPath;
    }

    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
}
