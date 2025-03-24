package com.citc.nce.filecenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.filecenter.configure.SuperAdministratorConfigure;
import com.citc.nce.filecenter.entity.AvailableQuantity;
import com.citc.nce.filecenter.mapper.AvailableQuantityMapper;
import com.citc.nce.filecenter.service.IAvailableQuantityService;
import com.citc.nce.filecenter.vo.AccountDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: wenliuch
 * @Date: 2022年8月18日15:12:01
 * @Version: 1.0
 * @Description: AvailableQuantityServiceImpl
 */
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(SuperAdministratorConfigure.class)
public class AvailableQuantityServiceImpl extends ServiceImpl<AvailableQuantityMapper, AvailableQuantity> implements IAvailableQuantityService {

    @Resource
    private AvailableQuantityMapper availableQuantityMapper;

    private final SuperAdministratorConfigure superAdministratorConfigure;

    @Override
    public List<AccountDetails> getDetails() {
        LambdaQueryWrapper<AvailableQuantity> wrapper = new LambdaQueryWrapper<>();
        String userId = SessionContextUtil.getUser().getUserId();
        if (!StringUtils.equals(userId, superAdministratorConfigure.getSuperAdministrator())) {
            wrapper.eq(AvailableQuantity::getCreator, userId);
        }
        List<AccountDetails> list = new ArrayList<>();
        List<AvailableQuantity> availableQuantities = availableQuantityMapper.selectList(wrapper);
        availableQuantities.forEach(availableQuantity -> {
            AccountDetails accountDetails = new AccountDetails();
            BeanUtils.copyProperties(availableQuantity, accountDetails);
            list.add(accountDetails);
        });
        return list;
    }
}
