package com.citc.nce.authcenter.csp.multitenant.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.citc.nce.authcenter.csp.multitenant.dao.CspMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.Csp;
import com.citc.nce.common.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * @author jiancheng
 */
@Slf4j
@Component
public class CspUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    public static String encodeCspId(String cspId) {
        if (cspId == null || cspId.length() != 10)
            throw new BizException(500, "非法cspId");
        CspMapper cspMapper = context.getBean(CspMapper.class);
        if (!cspMapper.exists(Wrappers.<Csp>lambdaQuery().eq(Csp::getCspId, cspId).eq(Csp::getCspActive, true)))
            throw new BizException(500, "csp不存在");
        String encoded;
        try {
            //对csp id先base64编码，再使用urlEncode编码
            encoded = URLEncoder.encode(Base64.getEncoder().encodeToString(cspId.getBytes()), "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new BizException(500, "编码cspId未知错误");
        }
        return encoded;
    }

    public static String decodeCspId(String encoded) {
        if (StringUtils.isBlank(encoded))
            return null;
        try {
            String cspId = new String(Base64.getDecoder().decode(URLDecoder.decode(encoded, "utf-8")));
            if (cspId.length() == 10) return cspId;
        } catch (UnsupportedEncodingException e) {
            log.warn("解码cspId失败:{}", encoded);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 将customerId转换为cspId
     *
     * @param customerId csp客户ID
     * @return csp客户对应的cspId
     */
    public static String convertCspId(String customerId) {
        Assert.notNull(customerId, "customerId不能为空");
        if (customerId.length() != 15)
            throw new IllegalArgumentException("非法customerId");
        return customerId.substring(0, 10);
    }

    public static void main(String[] args) {
        System.out.println(decodeCspId("MjQ4NDE3NjQ2Mg%3D%3D"));
    }
}
