package com.citc.nce.developer.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.accountmanagement.AccountManagementApi;
import com.citc.nce.auth.accountmanagement.vo.AccountManagementResp;
import com.citc.nce.auth.csp.customer.CustomerApi;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.configure.DeveloperReceiveConfigure;
import com.citc.nce.developer.common.CommonHandle;
import com.citc.nce.developer.dao.DeveloperCustomer5gMapper;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.entity.DeveloperCustomer5gDo;
import com.citc.nce.developer.service.DeveloperCustomer5gService;
import com.citc.nce.developer.vo.*;
import com.citc.nce.utils.AesForDeveloperUtil;
import com.citc.nce.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @author ping chen
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Service
public class DeveloperCustomer5gServiceImpl extends ServiceImpl<DeveloperCustomer5gMapper, DeveloperCustomer5gDo> implements DeveloperCustomer5gService {

    private final DeveloperReceiveConfigure developerReceiveConfigure;

    private final DeveloperSendMapper developerSendMapper;

    private final AccountManagementApi accountManagementApi;

    private final CustomerApi customerApi;

    @Override
    public DeveloperCustomerInfoVo queryInfo() {
        DeveloperCustomerInfoVo developerCustomerInfoVo = new DeveloperCustomerInfoVo();
        String userId = SessionContextUtil.getLoginUser().getUserId();
        DeveloperCustomer5gDo developerCustomer5gDo = this.lambdaQuery().eq(DeveloperCustomer5gDo::getCustomerId, userId).one();
        String token = AesForDeveloperUtil.getToken(userId,"5G");
        if (developerCustomer5gDo == null) {
            //生成回调地址
            String cpsId = customerApi.queryCspId(userId);
            developerCustomer5gDo = new DeveloperCustomer5gDo();
            developerCustomer5gDo.setReceiveUrl("/developer/send/5g?token=" + token);
            developerCustomer5gDo.setUniqueId(UUIDUtil.getuuid());
            developerCustomer5gDo.setCspId(cpsId);
            developerCustomer5gDo.setCustomerId(userId);
            this.save(developerCustomer5gDo);
        }else{
            if(!developerCustomer5gDo.getReceiveUrl().endsWith(token)){
                developerCustomer5gDo.setReceiveUrl("/developer/send/5g?token=" + token);
                this.updateById(developerCustomer5gDo);
            }
        }
        BeanUtils.copyProperties(developerCustomer5gDo, developerCustomerInfoVo);
        developerCustomerInfoVo.setReceiveUrl(developerReceiveConfigure.getCallbackUrl() + developerCustomerInfoVo.getReceiveUrl());
        return developerCustomerInfoVo;
    }

    @Override
    public void saveCallbackUrl(DeveloperSaveCallbackUrlVo developerSaveCallbackUrlVo) {
        DeveloperCustomer5gDo developerCustomer5gDo = this.lambdaQuery().eq(DeveloperCustomer5gDo::getCustomerId, SessionContextUtil.getUser().getUserId()).one();
        developerCustomer5gDo.setCallbackUrl(developerSaveCallbackUrlVo.getCallbackUrl());
        this.saveOrUpdate(developerCustomer5gDo);
    }

    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        Page<DeveloperCustomerVo> smsDeveloperCustomerVoPage = Page.of(smsDeveloperSendSearchVo.getPageNo(), smsDeveloperSendSearchVo.getPageSize());
        smsDeveloperCustomerVoPage.setOrders(OrderItem.descs("csds.call_time"));
        DeveloperSendSearchCommonVo developerSendSearchCommonVo = CommonHandle.sendRequestHandle(smsDeveloperSendSearchVo.getSendResult());
        Page<DeveloperCustomerVo> page = developerSendMapper.searchDeveloperSend(
                smsDeveloperSendSearchVo.getAccountId(), SessionContextUtil.getUser().getUserId(),
                smsDeveloperSendSearchVo.getPhone(), developerSendSearchCommonVo.getCallResult(), developerSendSearchCommonVo.getSendPlatformResult(),
                developerSendSearchCommonVo.getCallbackPlatformResult(), smsDeveloperSendSearchVo.getCallbackResult(),
                smsDeveloperSendSearchVo.getCallTimeStart(), smsDeveloperSendSearchVo.getCallTimeEnd(),
                1, smsDeveloperCustomerVoPage);
        List<DeveloperCustomerVo> developerCustomerVoList = page.getRecords();
        if (!developerCustomerVoList.isEmpty()) {
            for (DeveloperCustomerVo developerCustomerVo : developerCustomerVoList) {
                if (StringUtils.isNotBlank(developerCustomerVo.getAccountId())) {
                    AccountManagementResp accountManagementResp = accountManagementApi.getAccountManagementByChatbotAccountId(developerCustomerVo.getAccountId());
                    if (accountManagementResp != null) {
                        developerCustomerVo.setAccountName(accountManagementResp.getAccountName());
                    }
                }
                if (StringUtils.isBlank(developerCustomerVo.getAccountName())) {
                    developerCustomerVo.setAccountName("---");
                }
            }
        }
        return new PageResult<>(developerCustomerVoList, page.getTotal());
    }
}
