package com.citc.nce.auth.prepayment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.citc.nce.auth.prepayment.entity.CustomerPrepaymentConfig;
import com.citc.nce.auth.prepayment.vo.CustomerPrepaymentConfigVo;

/**
 * @author jcrenc
 * @since 2024/3/12 9:40
 */
public interface ICustomerPrepaymentConfigService extends IService<CustomerPrepaymentConfig> {
    void config(CustomerPrepaymentConfigVo req);

    CustomerPrepaymentConfigVo queryCustomerPrepaymentConfig(String customerId);
}
