package com.citc.nce.auth.postpay.customerconfig.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.postpay.customerconfig.dao.CustomerPostpayConfigMapper;
import com.citc.nce.auth.postpay.customerconfig.entity.CustomerPostpayConfig;
import com.citc.nce.auth.postpay.customerconfig.entity.CustomerPostpayFifthConfig;
import com.citc.nce.auth.utils.MsgPaymentUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.citc.nce.auth.utils.MsgPaymentUtils.formatPrice;

/**
 * @author jcrenc
 * @since 2024/3/6 16:20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerPostpayConfigService extends ServiceImpl<CustomerPostpayConfigMapper, CustomerPostpayConfig> implements IService<CustomerPostpayConfig> {
    private final CustomerPostpayFifthConfigService fifthConfigService;

    /**
     * 查询客户的后付费配置
     *
     * @param customerId 要查询的客户id
     */
    public CustomerPostpayConfigVo queryCustomerPostpayConfig(String customerId) {
        CustomerPostpayConfigVo config = null;
        CustomerPostpayConfig postpayConfig = this.lambdaQuery()
                .eq(CustomerPostpayConfig::getCustomerId, customerId)
                .one();
        if (postpayConfig != null) {
            config = new CustomerPostpayConfigVo()
                    .setCustomerId(customerId)
                    .setSmsPrice(postpayConfig.getSmsPrice())
                    .setVideoPrice(postpayConfig.getVideoPrice());
            Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> operatorConfigMap = fifthConfigService.lambdaQuery()
                    .eq(CustomerPostpayFifthConfig::getCustomerId, customerId)
                    .list()
                    .stream()
                    .collect(Collectors.toMap(
                            CustomerPostpayFifthConfig::getOperator,
                            operatorConfig -> new CustomerPostpayConfigVo.Config()
                                    .setConversionPrice(operatorConfig.getConversionPrice())
                                    .setTextMessagePrice(operatorConfig.getTextMessagePrice())
                                    .setRichMessagePrice(operatorConfig.getRichMessagePrice())
                                    .setFallbackPrice(operatorConfig.getFallbackPrice())
                    ));
            config.setFifthConfigMap(operatorConfigMap);
        }
        return config;
    }

    /**
     * 配置客户后付费单价
     *
     * @param request 配置参数
     */
    @Transactional(rollbackFor = Exception.class)
    public void config(CustomerPostpayConfigVo request) {
        String customerId = request.getCustomerId();
        Map<CSPOperatorCodeEnum, CustomerPostpayConfigVo.Config> fifthConfigMap = request.getFifthConfigMap();
        //校验配置是否完整
        if(fifthConfigMap != null){
            //清空历史配置
            cleanFifthConfig(customerId);
            MsgPaymentUtils.checkConfiguredOperators(fifthConfigMap.keySet());
            List<CustomerPostpayFifthConfig> fifthConfigs = request.getFifthConfigMap()
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        CSPOperatorCodeEnum operatorCodeEnum = entry.getKey();
                        CustomerPostpayConfigVo.Config c = entry.getValue();
                        return new CustomerPostpayFifthConfig()
                                .setCustomerId(customerId)
                                .setOperator(operatorCodeEnum)
                                .setTextMessagePrice(formatPrice(c.getTextMessagePrice()))
                                .setRichMessagePrice(formatPrice(c.getRichMessagePrice()))
                                .setConversionPrice(formatPrice(c.getConversionPrice()))
                                .setFallbackPrice(formatPrice(c.getFallbackPrice()));
                    })
                    .collect(Collectors.toList());
            fifthConfigService.saveBatch(fifthConfigs);
        }
        if(request.getSmsPrice() != null || request.getVideoPrice() != null){
            LambdaQueryWrapper<CustomerPostpayConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CustomerPostpayConfig::getCustomerId, request.getCustomerId());
            List<CustomerPostpayConfig> postpayConfigList = this.baseMapper.selectList(queryWrapper);
            if(!CollectionUtils.isEmpty(postpayConfigList)){
                CustomerPostpayConfig postpayConfig = postpayConfigList.get(0);
                if(request.getSmsPrice() != null){
                    postpayConfig.setSmsPrice(formatPrice(request.getSmsPrice()));
                }
                if(request.getVideoPrice() != null){
                    postpayConfig.setVideoPrice(formatPrice(request.getVideoPrice()));
                }
                this.updateById(postpayConfig);
            }else{
                CustomerPostpayConfig postpayConfig = new CustomerPostpayConfig()
                        .setCustomerId(customerId)
                        .setSmsPrice(formatPrice(request.getSmsPrice()))
                        .setVideoPrice(formatPrice(request.getVideoPrice()));
                this.save(postpayConfig);
            }
        }
    }

    private void cleanFifthConfig(String customerId) {
//        this.remove(Wrappers.<CustomerPostpayConfig>lambdaQuery().eq(CustomerPostpayConfig::getCustomerId, customerId));
        fifthConfigService.remove(Wrappers.<CustomerPostpayFifthConfig>lambdaQuery().eq(CustomerPostpayFifthConfig::getCustomerId, customerId));
    }
}
