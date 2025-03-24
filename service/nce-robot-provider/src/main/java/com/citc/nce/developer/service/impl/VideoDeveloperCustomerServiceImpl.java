package com.citc.nce.developer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountDetailResp;
import com.citc.nce.auth.csp.videoSms.account.vo.CspVideoSmsAccountQueryDetailReq;
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
import com.citc.nce.developer.dao.VideoDeveloperCustomerMapper;
import com.citc.nce.developer.entity.VideoDeveloperCustomerDo;
import com.citc.nce.developer.service.VideoDeveloperCustomerService;
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
public class VideoDeveloperCustomerServiceImpl extends ServiceImpl<VideoDeveloperCustomerMapper, VideoDeveloperCustomerDo> implements VideoDeveloperCustomerService {

    /**
     * AppId前缀
     */
    private static final String AppIdPrefix = "5GVIDEO";

    private final DeveloperReceiveConfigure developerReceiveConfigure;
    private final CommonKeyPairConfig commonKeyPairConfig;

    private final DeveloperSendMapper developerSendMapper;


    private final IdentificationApi identificationApi;

    private final CspCustomerApi cspCustomerApi;

    private final CspVideoSmsAccountApi cspVideoSmsAccountApi;


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
        VideoDeveloperAuthVo videoDeveloperAuthVo = new VideoDeveloperAuthVo();
        VideoDeveloperCustomerDo videoDeveloperCustomerDo = this.lambdaQuery().eq(VideoDeveloperCustomerDo::getCustomerId, customerId).one();
        if (videoDeveloperCustomerDo == null) {
            String appId = UUIDUtil.getuuid();
            videoDeveloperCustomerDo = new VideoDeveloperCustomerDo();
            videoDeveloperCustomerDo.setCustomerId(customerId);
            videoDeveloperCustomerDo.setAppId(appId);
            videoDeveloperCustomerDo.setAppKey(AuthInfoUtils.generateRandomString(AppIdPrefix));
            String appSecret = AuthInfoUtils.generateRandomString(32);
            String rawAppSecret = RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), appSecret);
            videoDeveloperCustomerDo.setAppSecret(rawAppSecret);
            videoDeveloperCustomerDo.setReceiveUrl("/developer/send/video?token=" + AesForDeveloperUtil.getToken(customerId,"video"));
            videoDeveloperCustomerDo.setState(0);
            videoDeveloperCustomerDo.setCspId(cspId);
            WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(customerId);
            if (webEnterpriseIdentificationResp != null) {
                videoDeveloperCustomerDo.setEnterpriseId(webEnterpriseIdentificationResp.getId());
            }
            this.save(videoDeveloperCustomerDo);
        }
        BeanUtils.copyProperties(videoDeveloperCustomerDo, videoDeveloperAuthVo);
        videoDeveloperAuthVo.setReceiveUrl(developerReceiveConfigure.getCallbackUrl() + videoDeveloperAuthVo.getReceiveUrl());
    }

    @Override
    public VideoDeveloperAuthVo details(String customerId) {
        VideoDeveloperCustomerDo videoDeveloperCustomerDo = this.lambdaQuery().eq(VideoDeveloperCustomerDo::getCustomerId, customerId).one();
        if (Objects.isNull(videoDeveloperCustomerDo)) return null;
        //校验开发者调用地址是否正确
        checkReceiveUrl(videoDeveloperCustomerDo);
        VideoDeveloperAuthVo videoDeveloperAuthVo = new VideoDeveloperAuthVo();
        BeanUtils.copyProperties(videoDeveloperCustomerDo, videoDeveloperAuthVo);
        WebEnterpriseIdentificationResp webEnterpriseIdentificationResp = identificationApi.getEnterpriseIdentificationInfoByUserId(customerId);
        if (webEnterpriseIdentificationResp != null) {
            videoDeveloperAuthVo.setEnterpriseAccountName(webEnterpriseIdentificationResp.getEnterpriseName());
        }
        if (StringUtils.hasLength(videoDeveloperAuthVo.getReceiveUrl())) {
            videoDeveloperAuthVo.setReceiveUrl(developerReceiveConfigure.getCallbackUrl() + videoDeveloperAuthVo.getReceiveUrl());
        }
        if(StringUtils.hasLength(videoDeveloperAuthVo.getAppSecret())) {
            videoDeveloperAuthVo.setAppSecret(RsaUtil.decryptByPrivateKey(commonKeyPairConfig.getPrivateKey(), videoDeveloperAuthVo.getAppSecret()));
        }
        return videoDeveloperAuthVo;
    }

    private void checkReceiveUrl(VideoDeveloperCustomerDo videoDeveloperCustomerDo) {
        String token = AesForDeveloperUtil.getToken(videoDeveloperCustomerDo.getCustomerId(),"video");
        if(!videoDeveloperCustomerDo.getReceiveUrl().endsWith(token)){
            videoDeveloperCustomerDo.setReceiveUrl("/developer/send/video?token=" + token);
            this.updateById(videoDeveloperCustomerDo);
        }
    }

    /**
     * 设置回调地址
     *
     * @param videoDeveloperAuthCallbackUrlVo
     */
    @Override
    public void saveCallbackUrl(VideoDeveloperAuthCallbackUrlVo videoDeveloperAuthCallbackUrlVo) {
        String customerUserId = SessionContextUtil.getLoginUser().getUserId();
        this.update(new LambdaUpdateWrapper<VideoDeveloperCustomerDo>()
                .set(VideoDeveloperCustomerDo::getCallbackUrl, videoDeveloperAuthCallbackUrlVo.getCallbackUrl())
                .eq(VideoDeveloperCustomerDo::getCustomerId, customerUserId));
    }

    /**
     * 回调明细列表查询
     *
     * @param videoDeveloperSendSearchVo
     * @return
     */
    @Override
    public PageResult<DeveloperCustomerVo> searchDeveloperSend(VideoDeveloperSendSearchVo videoDeveloperSendSearchVo) {

        Page<DeveloperCustomerVo> videoDeveloperCustomerVoPage = Page.of(videoDeveloperSendSearchVo.getPageNo(), videoDeveloperSendSearchVo.getPageSize());
        videoDeveloperCustomerVoPage.setOrders(OrderItem.descs("csds.call_time"));
        DeveloperSendSearchCommonVo developerSendSearchCommonVo = CommonHandle.sendRequestHandle(videoDeveloperSendSearchVo.getSendResult());
        Page<DeveloperCustomerVo> page = developerSendMapper.searchDeveloperSend(videoDeveloperSendSearchVo.getAccountId(), SessionContextUtil.getUser().getUserId(),
                videoDeveloperSendSearchVo.getPhone(), developerSendSearchCommonVo.getCallResult(), developerSendSearchCommonVo.getSendPlatformResult()
                , developerSendSearchCommonVo.getCallbackPlatformResult(), videoDeveloperSendSearchVo.getCallbackResult(), videoDeveloperSendSearchVo.getCallTimeStart(),
                videoDeveloperSendSearchVo.getCallTimeEnd(), 2, videoDeveloperCustomerVoPage);
        //没有设置accountId的数据不查询机器人名称
        page.getRecords().stream().filter(s -> {
                    s.setAccountName("---");
                    return StringUtils.hasLength(s.getAccountId());
                }
        ).forEach(developerCustomerVo -> {
            CspVideoSmsAccountQueryDetailReq cspVideoSmsAccountQueryDetailReq = new CspVideoSmsAccountQueryDetailReq();
            cspVideoSmsAccountQueryDetailReq.setAccountId(developerCustomerVo.getAccountId());
            CspVideoSmsAccountDetailResp cspVideoSmsAccountDetailResp = cspVideoSmsAccountApi.queryDetail(cspVideoSmsAccountQueryDetailReq);
            if (cspVideoSmsAccountDetailResp != null) {
                developerCustomerVo.setAccountName(cspVideoSmsAccountDetailResp.getAccountName());
            }
        });
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    /**
     * 禁用/启用
     *
     * @param videoDeveloperSwitchVo
     */
    @Override
    public void setSwitch(VideoDeveloperSwitchVo videoDeveloperSwitchVo) {
        LambdaUpdateWrapper<VideoDeveloperCustomerDo> videoDeveloperCustomerDoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        videoDeveloperCustomerDoLambdaUpdateWrapper.set(VideoDeveloperCustomerDo::getState, videoDeveloperSwitchVo.getState());
        videoDeveloperCustomerDoLambdaUpdateWrapper.eq(VideoDeveloperCustomerDo::getCustomerId, videoDeveloperSwitchVo.getCustomerUserId());
        this.update(videoDeveloperCustomerDoLambdaUpdateWrapper);
    }

    @Override
    public PageResult<VideoDeveloperCustomerManagerVo> queryList(VideoDeveloperCustomerReqVo videoDeveloperCustomerReqVo) {
        String cpsId = SessionContextUtil.getUser().getUserId();
        Page<VideoDeveloperCustomerManagerVo> videoDeveloperCustomerManagerVoPage = Page.of(videoDeveloperCustomerReqVo.getPageNo(), videoDeveloperCustomerReqVo.getPageSize());
        videoDeveloperCustomerManagerVoPage.setOrders(OrderItem.descs("sdc.create_time"));
        Page<VideoDeveloperCustomerManagerVo> page = baseMapper.searchCustomersManager(videoDeveloperCustomerReqVo.getCustomerId(),
                videoDeveloperCustomerReqVo.getState(), cpsId, videoDeveloperCustomerManagerVoPage);
        List<VideoDeveloperCustomerManagerVo> videoDeveloperCustomerManagerVoList = page.getRecords();
        if (CollectionUtil.isNotEmpty(videoDeveloperCustomerManagerVoList)) {
            List<String> customerIds = videoDeveloperCustomerManagerVoList.stream().map(VideoDeveloperCustomerManagerVo::getCustomerId).distinct().collect(Collectors.toList());
            Map<String,String> enterpriseNameMap = identificationApi.getEnterpriseIdentificationInfoByUserIds(customerIds);

            //查询发送表
            List<DeveloperSendCountVo> videoDeveloperSendDoList = developerSendMapper.queryCount(customerIds, 2);
            Map<String, Integer> sendMap = videoDeveloperSendDoList.stream().collect(Collectors.toMap(DeveloperSendCountVo::getCustomerId, DeveloperSendCountVo::getCount));

            //查询统计表
            List<DeveloperSendCountVo> developerSendCountVoList = developerSendMapper.queryCountAll(customerIds, 2);
            Map<String, Integer> sendStatisticsMap = developerSendCountVoList.stream().collect(Collectors.toMap(DeveloperSendCountVo::getCustomerId, DeveloperSendCountVo::getCount));
            videoDeveloperCustomerManagerVoList.forEach(videoDeveloperCustomerManagerVo -> {
                videoDeveloperCustomerManagerVo.setEnterpriseAccountName(enterpriseNameMap.getOrDefault(videoDeveloperCustomerManagerVo.getCustomerId(),null));
                if (sendMap.get(videoDeveloperCustomerManagerVo.getCustomerId()) != null) {
                    videoDeveloperCustomerManagerVo.setCallCountToday(sendMap.get(videoDeveloperCustomerManagerVo.getCustomerId()));
                } else {
                    videoDeveloperCustomerManagerVo.setCallCountToday(0);
                }
                if (sendStatisticsMap.get(videoDeveloperCustomerManagerVo.getCustomerId()) != null) {
                    videoDeveloperCustomerManagerVo.setCallCount(sendStatisticsMap.get(videoDeveloperCustomerManagerVo.getCustomerId()));
                } else {
                    videoDeveloperCustomerManagerVo.setCallCount(0);
                }
            });
        } else {
            videoDeveloperCustomerManagerVoList = new ArrayList<>();
        }
        return new PageResult<>(videoDeveloperCustomerManagerVoList, page.getTotal());
    }

    @Override
    public PageResult<DeveloperAccountVo> getVideoDeveloperCustomerOption() {
        String cspId = SessionContextUtil.getLoginUser().getCspId();
        List<VideoDeveloperCustomerDo> applicationList = this.lambdaQuery()
                .eq(VideoDeveloperCustomerDo::getCspId, cspId)
                .select(VideoDeveloperCustomerDo::getCustomerId)
                .list();
        if (CollectionUtils.isEmpty(applicationList))
            return new PageResult<>();
        List<String> customerIds = applicationList.stream()
                .map(VideoDeveloperCustomerDo::getCustomerId)
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
        List<VideoDeveloperCustomerDo> list = getBaseMapper().selectListEncode();
        if (CollectionUtil.isEmpty(list)) return;
        list.forEach(s -> s.setAppSecret(RsaUtil.encryptByPublicKey(commonKeyPairConfig.getPublicKey(), s.getAppSecret())));
        updateBatchById(list);
    }
}
