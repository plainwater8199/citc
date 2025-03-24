package com.citc.nce.auth.postpay.customerconfig.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.postpay.customerconfig.dao.CustomerPostpayFifthConfigMapper;
import com.citc.nce.auth.postpay.customerconfig.entity.CustomerPostpayFifthConfig;
import org.springframework.stereotype.Service;

/**
 * @author jcrenc
 * @since 2024/3/6 16:37
 */
@Service
public class CustomerPostpayFifthConfigService extends ServiceImpl<CustomerPostpayFifthConfigMapper, CustomerPostpayFifthConfig> implements IService<CustomerPostpayFifthConfig> {
}
