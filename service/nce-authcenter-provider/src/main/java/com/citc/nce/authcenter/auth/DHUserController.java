package com.citc.nce.authcenter.auth;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.citc.nce.authcenter.auth.vo.DHExchange;
import com.citc.nce.authcenter.auth.vo.DHVerify;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.web.utils.dh.DHHexUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author bydud
 * @since 2024/7/16 10:36
 */
@RestController
@Slf4j
public class DHUserController implements DHUserApi {

    @Autowired
    private ECDHService ecdhService;

    /**
     * 在已经建立tls/ssl的环境下  通过DH算法交换秘钥
     *
     * @param exchange 前端的公钥
     * @return 后端的公钥
     */
    public DHExchange exchange(@RequestBody DHExchange exchange) {
        System.out.println("------request----public_key------------:" + exchange.getPublicKey());

        String dh_AES = StpUtil.getTokenSession().getString(DHHexUtil.DH_SHARE_SECRET_KEY);
        if (StringUtils.hasLength(dh_AES)) {
            throw new BizException(401, "已交换过秘钥，如果秘钥丢失请重新登录");
        }
        //计算共享秘钥
        DHHexUtil.ECDH ecdh = DHHexUtil.initECDH();
        byte[] serverPkEn = ecdh.getPublicKey().getEncoded();
        byte[] shareSecretKey = ecdh.generateShareSecretKey(DHHexUtil.toByteArray(exchange.getPublicKey()));
        //通过共享秘钥派生aes256秘钥(前端需要保持一致)
        String randomString = RandomUtil.randomString(20);
        log.info("shareSecretKey..............{}", shareSecretKey);
        String aesHexString = DHHexUtil.getAesHexString(randomString, shareSecretKey);
        StpUtil.getTokenSession().set(DHHexUtil.DH_SHARE_SECRET_KEY, aesHexString);
        System.out.println("------request----aesHexString------------:" + aesHexString);
        //将自己的公钥给前端
        System.out.println("------response----public_key------------:" + DHHexUtil.toHexString(serverPkEn));
        return new DHExchange().setPublicKey(DHHexUtil.toHexString(serverPkEn)).setTime(randomString);
    }

    /**
     * 验证秘钥算法是否正常
     *
     * @param verify 原则数据
     * @return 解密后的数据
     */
    public String verify(@RequestBody DHVerify verify) {
        return ecdhService.decode(verify.getEncodeHexStr());
    }
}
