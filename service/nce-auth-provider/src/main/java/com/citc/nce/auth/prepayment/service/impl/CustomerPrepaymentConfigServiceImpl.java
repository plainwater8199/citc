package com.citc.nce.auth.prepayment.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.prepayment.dao.CustomerPrepaymentConfigMapper;
import com.citc.nce.auth.prepayment.entity.CustomerPrepaymentConfig;
import com.citc.nce.auth.prepayment.service.ICustomerPrepaymentConfigService;
import com.citc.nce.auth.prepayment.vo.CustomerPrepaymentConfigVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jcrenc
 * @since 2024/3/12 9:41
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerPrepaymentConfigServiceImpl extends ServiceImpl<CustomerPrepaymentConfigMapper, CustomerPrepaymentConfig> implements ICustomerPrepaymentConfigService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void config(CustomerPrepaymentConfigVo req) {
        //clear exists config
        String customerId = req.getCustomerId();
        this.remove(Wrappers.<CustomerPrepaymentConfig>lambdaQuery().eq(CustomerPrepaymentConfig::getCustomerId, customerId));
        //save new config
        CustomerPrepaymentConfig prepaymentConfig = new CustomerPrepaymentConfig()
                .setCustomerId(customerId)
                .setFallbackPrice(req.getFallbackPrice());
        this.save(prepaymentConfig);
    }

    @Override
    public CustomerPrepaymentConfigVo queryCustomerPrepaymentConfig(String customerId) {
        return lambdaQuery()
                .eq(CustomerPrepaymentConfig::getCustomerId, customerId)
                .oneOpt()
                .map(config -> new CustomerPrepaymentConfigVo().setCustomerId(customerId).setFallbackPrice(config.getFallbackPrice()))
                .orElse(null);
    }
}
