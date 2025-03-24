package com.citc.nce.auth.postpay.scheme.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.postpay.scheme.dao.PostpaySchemeConfigMapper;
import com.citc.nce.auth.postpay.scheme.entity.PostpaySchemeConfig;
import com.citc.nce.auth.postpay.scheme.vo.SchemeAddVo;
import com.citc.nce.common.core.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.citc.nce.auth.utils.MsgPaymentUtils.formatPrice;

/**
 * @author jcrenc
 * @since 2024/3/6 10:21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PostpaySchemeConfigService extends ServiceImpl<PostpaySchemeConfigMapper, PostpaySchemeConfig> implements IService<PostpaySchemeConfig> {

    @Transactional(rollbackFor = Exception.class)
    public void addSchemeConfig(Long schemeId, List<SchemeAddVo.Config> configs) {
        List<PostpaySchemeConfig> schemeConfigs = configs.stream()
                .map(vo -> {
                    PostpaySchemeConfig schemeConfig = new PostpaySchemeConfig();
                    schemeConfig.setSchemeId(schemeId)
                            .setOperator(vo.getOperator())
                            .setTextMessagePrice(formatPrice(vo.getTextMessagePrice()))
                            .setRichMessagePrice(formatPrice(vo.getRichMessagePrice()))
                            .setConversionPrice(formatPrice(vo.getConversionPrice()))
                            .setFallbackPrice(formatPrice(vo.getFallbackPrice()));
                    return schemeConfig;
                })
                .collect(Collectors.toList());
        if (!this.saveBatch(schemeConfigs))
            throw new BizException("保存失败");
    }
}
