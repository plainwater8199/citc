package com.citc.nce.developer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountDetailResp;
import com.citc.nce.auth.csp.sms.account.vo.CspSmsAccountQueryDetailReq;
import com.citc.nce.auth.identification.IdentificationApi;
import com.citc.nce.auth.identification.vo.resp.WebEnterpriseIdentificationResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.RsaUtil;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.conf.CommonKeyPairConfig;
import com.citc.nce.configure.DeveloperReceiveConfigure;
import com.citc.nce.developer.common.CommonHandle;
import com.citc.nce.developer.dao.DeveloperSendMapper;
import com.citc.nce.developer.dao.SmsDeveloperCustomerMapper;
import com.citc.nce.developer.entity.SmsDeveloperCustomerDo;
import com.citc.nce.developer.service.SmsDeveloperCustomerService;
import com.citc.nce.developer.vo.*;
import com.citc.nce.utils.AesForDeveloperUtil;
import com.citc.nce.utils.AuthInfoUtils;
import com.citc.nce.utils.UUIDUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * @author ping chen
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Service
public class SmsDeveloperCustomerServiceImpl extends ServiceImpl<SmsDeveloperCustomerMapper, SmsDeveloperCustomerDo> implements SmsDeveloperCustomerService {

    /**
     * AppId前缀
     */
    private static final String AppIdPrefix = "5GSMS";

    private final DeveloperReceiveConfigure developerReceiveConfigure;
    private final CommonKeyPairConfig commonKeyPairConfig;

    private final DeveloperSendMapper developerSendMapper;

    private final IdentificationApi identificationApi;

    private final CspCustomerApi cspCustomerApi;

    private final CspSmsAccountApi cspSmsAccountApi;


    /**
     * 生成鉴权信息
     *
     * @return
     */
    @SneakyThrows
    @Override
    public void generate() {
        BaseUser user = SessionContextUtil.getUser();
        String customerId = user.getUserId();
        String cspId = user.getCspId();
        SmsDeveloperAuthVo smsDeveloperAuthVo = new SmsDeveloperAuthVo();
        SmsDeveloperCustomerDo smsDeveloperCustomerDo = this.lambdaQuery().eq(SmsDeveloperCustomerDo::getCustomerId, customerId).one();
        if (smsDeveloperCustomerDo == null) {
            String appId = UUIDUtil.getuuid();
            smsDeveloperCustomerDo = new SmsDeveloperCustomerDo();
            smsDeveloperCustomerDo.setCustomerId(customerId);
            smsDeveloperCustomerDo.setAppId(appId);
            smsDeveloperCustomerDo.setAppKey(AuthInfoUtils.generateRandomString(AppIdPrefix));
            String appSecret = AuthInfoUtils.generateRandomString(32);
            String rawAppSecret = RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), appSecret);
            smsDeveloperCustomerDo.setAppSecret(rawAppSecret);
            smsDeveloperCustomerDo.setReceiveUrl("/developer/send/sms?token=" + AesForDeveloperUtil.getToken(customerId,"sms"));
            smsDeveloperCustomerDo.setState(0);
            smsDeveloperCustomerDo.setCspId(cspId);
            WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(customerId);
            if (webEnterpriseIdentificationResp != null) {
                smsDeveloperCustomerDo.setEnterpriseId(webEnterpriseIdentificationResp.getId());
            }
            this.save(smsDeveloperCustomerDo);
        }
        BeanUtils.copyProperties(smsDeveloperCustomerDo, smsDeveloperAuthVo);
        smsDeveloperAuthVo.setReceiveUrl(developerReceiveConfigure.getCallbackUrl() + smsDeveloperAuthVo.getReceiveUrl());
    }

    @Override
    public SmsDeveloperAuthVo details(String customerId) {
        SmsDeveloperCustomerDo smsDeveloperCustomerDo = this.lambdaQuery().eq(SmsDeveloperCustomerDo::getCustomerId, customerId).one();
        if (Objects.isNull(smsDeveloperCustomerDo)) return null;
        //校验开发者调用地址是否正确
        checkReceiveUrl(smsDeveloperCustomerDo);
        SmsDeveloperAuthVo smsDeveloperAuthVo = new SmsDeveloperAuthVo();
        BeanUtils.copyProperties(smsDeveloperCustomerDo, smsDeveloperAuthVo);
        WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(customerId);
        if (webEnterpriseIdentificationResp != null) {
            smsDeveloperAuthVo.setEnterpriseAccountName(webEnterpriseIdentificationResp.getEnterpriseName());
        }
        if (StringUtils.hasLength(smsDeveloperAuthVo.getReceiveUrl())) {
            smsDeveloperAuthVo.setReceiveUrl(developerReceiveConfigure.getCallbackUrl() + smsDeveloperAuthVo.getReceiveUrl());
        }
        smsDeveloperAuthVo.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), smsDeveloperAuthVo.getAppSecret()));
        return smsDeveloperAuthVo;
    }

    private void checkReceiveUrl(SmsDeveloperCustomerDo smsDeveloperCustomerDo) {
        String token = AesForDeveloperUtil.getToken(smsDeveloperCustomerDo.getCustomerId(),"sms");
        if(!smsDeveloperCustomerDo.getReceiveUrl().endsWith(token)){
            smsDeveloperCustomerDo.setReceiveUrl("/developer/send/sms?token=" + token);
            this.updateById(smsDeveloperCustomerDo);
        }
    }



    /**
     * 设置回调地址
     *
     * @param smsDeveloperAuthCallbackUrlVo
     */
    @Override
    public void saveCallbackUrl(SmsDeveloperAuthCallbackUrlVo smsDeveloperAuthCallbackUrlVo) {
        String customerUserId = SessionContextUtil.getUser().getUserId();
        this.update(new LambdaUpdateWrapper<SmsDeveloperCustomerDo>()
                .set(SmsDeveloperCustomerDo::getCallbackUrl, smsDeveloperAuthCallbackUrlVo.getCallbackUrl())
                .eq(SmsDeveloperCustomerDo::getCustomerId, customerUserId));
    }

    /**
     * 回调明细列表查询
     *
     * @param smsDeveloperSendSearchVo
     * @return
     */
    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(SmsDeveloperSendSearchVo smsDeveloperSendSearchVo) {
        Page<DeveloperCustomerVo> smsDeveloperCustomerVoPage = Page.of(smsDeveloperSendSearchVo.getPageNo(), smsDeveloperSendSearchVo.getPageSize());
        smsDeveloperCustomerVoPage.setOrders(OrderItem.descs("csds.call_time"));
        DeveloperSendSearchCommonVo developerSendSearchCommonVo = CommonHandle.sendRequestHandle(smsDeveloperSendSearchVo.getSendResult());
        Page<DeveloperCustomerVo> page = developerSendMapper.searchDeveloperSend(smsDeveloperSendSearchVo.getAccountId(), SessionContextUtil.getUser().getUserId(),
                smsDeveloperSendSearchVo.getPhone(), developerSendSearchCommonVo.getCallResult(), developerSendSearchCommonVo.getSendPlatformResult()
                , developerSendSearchCommonVo.getCallbackPlatformResult(), smsDeveloperSendSearchVo.getCallbackResult(), smsDeveloperSendSearchVo.getCallTimeStart(),
                smsDeveloperSendSearchVo.getCallTimeEnd(), 3, smsDeveloperCustomerVoPage);

        page.getRecords().stream().filter(s -> {
            s.setAccountName("---");
            return StringUtils.hasLength(s.getAccountId());
        }).forEach(developerCustomerVo -> {
            CspSmsAccountQueryDetailReq cspSmsAccountQueryDetailReq = new CspSmsAccountQueryDetailReq();
            cspSmsAccountQueryDetailReq.setAccountId(developerCustomerVo.getAccountId());
            CspSmsAccountDetailResp smsAccountDetailResp = cspSmsAccountApi.queryDetail(cspSmsAccountQueryDetailReq);
            if (Objects.nonNull(smsAccountDetailResp)) {
                developerCustomerVo.setAccountName(smsAccountDetailResp.getAccountName());
            }
        });

        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 禁用/启用
     *
     * @param smsDeveloperSwitchVo
     */
    @Override
    public void setSwitch(SmsDeveloperSwitchVo smsDeveloperSwitchVo) {
        this.update(new LambdaUpdateWrapper<SmsDeveloperCustomerDo>().set(SmsDeveloperCustomerDo::getState, smsDeveloperSwitchVo.getState())
                .eq(SmsDeveloperCustomerDo::getCustomerId, smsDeveloperSwitchVo.getCustomerUserId()));
    }

    @Override
    public PageResult<SmsDeveloperCustomerManagerVo> queryList(SmsDeveloperCustomerReqVo smsDeveloperCustomerReqVo) {
        String cpsId = SessionContextUtil.getUser().getUserId();
        Page<SmsDeveloperCustomerManagerVo> smsDeveloperCustomerManagerVoPage = Page.of(smsDeveloperCustomerReqVo.getPageNo(), smsDeveloperCustomerReqVo.getPageSize());
        smsDeveloperCustomerManagerVoPage.setOrders(OrderItem.descs("sdc.create_time"));
        Page<SmsDeveloperCustomerManagerVo> page = baseMapper.searchCustomersManager(smsDeveloperCustomerReqVo.getCustomerId(),
                smsDeveloperCustomerReqVo.getState(), cpsId, smsDeveloperCustomerManagerVoPage);
        List<SmsDeveloperCustomerManagerVo> smsDeveloperCustomerManagerVoList = page.getRecords();
        if (CollectionUtil.isNotEmpty(smsDeveloperCustomerManagerVoList)) {
            List<String> customerIds = smsDeveloperCustomerManagerVoList.stream().map(SmsDeveloperCustomerManagerVo::getCustomerId).collect(Collectors.toList());
            Map<String,String> enterpriseNameMap = identificationApi.getEnterpriseIdentificationInfoByUserIds(customerIds);
            //查询发送表
            List<DeveloperSendCountVo> smsDeveloperSendDoList = developerSendMapper.queryCount(customerIds, 3);
            Map<String, Integer> sendMap = smsDeveloperSendDoList.stream().collect(Collectors.toMap(DeveloperSendCountVo::getCustomerId, DeveloperSendCountVo::getCount));

            //查询统计表
            List<DeveloperSendCountVo> developerSendCountVoList = developerSendMapper.queryCountAll(customerIds, 3);
            Map<String, Integer> sendStatisticsMap = developerSendCountVoList.stream().collect(Collectors.toMap(DeveloperSendCountVo::getCustomerId, DeveloperSendCountVo::getCount));
            smsDeveloperCustomerManagerVoList.forEach(smsDeveloperCustomerManagerDayVo -> {
                smsDeveloperCustomerManagerDayVo.setEnterpriseAccountName(enterpriseNameMap.get(smsDeveloperCustomerManagerDayVo.getCustomerId()));
                if (sendMap.get(smsDeveloperCustomerManagerDayVo.getCustomerId()) != null) {
                    smsDeveloperCustomerManagerDayVo.setCallCountToday(sendMap.get(smsDeveloperCustomerManagerDayVo.getCustomerId()));
                } else {
                    smsDeveloperCustomerManagerDayVo.setCallCountToday(0);
                }
                if (sendStatisticsMap.get(smsDeveloperCustomerManagerDayVo.getCustomerId()) != null) {
                    smsDeveloperCustomerManagerDayVo.setCallCount(sendStatisticsMap.get(smsDeveloperCustomerManagerDayVo.getCustomerId()));
                } else {
                    smsDeveloperCustomerManagerDayVo.setCallCount(0);
                }
            });
        } else {
            smsDeveloperCustomerManagerVoList = new ArrayList<>();
        }
        return new PageResult<>(smsDeveloperCustomerManagerVoList, page.getTotal());
    }

    @Override
    public PageResult<DeveloperAccountVo> getSmsDeveloperCustomerOption() {
        String cspId = SessionContextUtil.getLoginUser().getCspId();
        List<SmsDeveloperCustomerDo> applicationList = this.lambdaQuery()
                .eq(SmsDeveloperCustomerDo::getCspId, cspId)
                .select(SmsDeveloperCustomerDo::getCustomerId)
                .list();
        if (CollectionUtils.isEmpty(applicationList))
            return new PageResult<>();
        List<String> customerIds = applicationList.stream()
                .map(SmsDeveloperCustomerDo::getCustomerId)
                .distinct()
                .collect(Collectors.toList());
        List<DeveloperAccountVo> accountVos = cspCustomerApi.getUserEnterpriseInfoByUserIds(customerIds)
                .stream()
                .map(userEnterpriseInfoVo -> new DeveloperAccountVo().setCustomerId(userEnterpriseInfoVo.getUserId()).setEnterpriseName(userEnterpriseInfoVo.getEnterpriseName()))
                .collect(Collectors.toList());
        return new PageResult<>(accountVos, (long) accountVos.size());
    }

    @Override
    public void appSecretEncode() {
        List<SmsDeveloperCustomerDo> list = getBaseMapper().selectListEncode();
        if (CollectionUtil.isEmpty(list)) return;
        list.forEach(s -> s.setAppSecret(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), s.getAppSecret())));
        updateBatchById(list);
    }
}
