package com.citc.nce.authcenter.csp.multitenant.apiImpl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.citc.nce.authcenter.auth.vo.req.LoginReq;
import com.citc.nce.authcenter.auth.vo.req.thirdAuthReq;
import com.citc.nce.authcenter.auth.vo.req.thirdLoginReq;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.multitenant.dao.CspCustomerMapper;
import com.citc.nce.authcenter.csp.multitenant.entity.CspCustomer;
import com.citc.nce.authcenter.csp.multitenant.service.CspCustomerService;
import com.citc.nce.authcenter.csp.multitenant.service.CspService;
import com.citc.nce.authcenter.csp.multitenant.utils.CspUtils;
import com.citc.nce.authcenter.csp.vo.*;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.tenant.MsgRecordApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jiancheng
 */
@RestController
@Slf4j
public class CspCustomerController implements CspCustomerApi {
    @Autowired
    private CspCustomerService cspCustomerService;
    @Autowired
    private CspService cspService;
    @Autowired
    private MsgRecordApi msgRecordApi;

    @Override
    @RequestMapping("/csp/customer/save")
    public void save(CustomerAddReq req) {
        cspCustomerService.addCustomer(req);
    }

    @PostMapping("/csp/{encoded}/customer/login")
    @Override
    public LoginResp login(@PathVariable(value = "encoded") String encoded, @RequestBody LoginReq req) {
        String cspId = CspUtils.decodeCspId(encoded);
        return cspCustomerService.cusLogin(cspId, req);
    }

    @PostMapping("/customer/thirdLogin")
    @Override
    public LoginResp thirdLogin(@RequestBody thirdLoginReq req) {
        return cspCustomerService.thirdLogin(req);
    }

    @PostMapping("/customer/thirdAuth")
    @Override
    public LoginResp thirdAuth(@RequestBody thirdAuthReq req) {
        return cspCustomerService.thirdAuth(req);
    }

    @PostMapping("/csp/customer/queryList")
    @Override
    public PageResult<CustomerSearchResp> queryList(CustomerSearchReq req) {
        return cspCustomerService.queryList(req);
    }
    @GetMapping("/csp/customer/queryCustomerOfCSPForDropdown")
    @Override
    public List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(Integer payType) {
        return cspCustomerService.queryCustomerOfCSPForDropdown(payType);
    }
    @PostMapping("/csp/customer/queryUserProvince")
    @Override
    public List<CustomerProvinceResp> getCustomerDistribution() {
        return cspCustomerService.getCustomerDistribution();
    }

    @PostMapping("/csp/customer/updateCspActive")
    @Override
    public int updateCspActive(CustomerActiveReq req) {
        return cspCustomerService.updateCspActive(req.getCustomerId(), req.getCustomerActive());
    }

    @PostMapping("/csp/customer/getDetailByCustomerId")
    @Override
    public CustomerDetailResp getDetailByCustomerId(CustomerDetailReq req) {
        return cspCustomerService.getCustomerDetail(req.getCustomerId());
    }

    @GetMapping("/customer/queryMsgSendInfo")
    @Override
    public Map<MsgTypeEnum, List<MessageResourceType>> queryMsgSendInfo(@RequestParam("customerId") String customerId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime end = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);
        return msgRecordApi.querySendMsgTypeListBetween(customerId, start, end);
    }

    @PostMapping("/csp/customer/updateByUserId")
    @Override
    public int updateCustomer(CustomerUpdateReq req) {
        return cspCustomerService.updateCustomer(req);
    }


    @Override
    @PostMapping("/csp/customer/getPermission")
    public String getUserPermission(String customerId) {
        return cspCustomerService.getUserPermission(customerId);
    }

    @Override
    @PostMapping("/csp/getCspInfo")
    public CspInfoResp getCspInfo(String userId) {
        return cspCustomerService.getCspInfo(userId);
    }

    @Override
    @GetMapping("/csp/customer/{customerId}/exists")
    public Boolean existsById(@PathVariable String customerId) {
        return cspCustomerService.lambdaQuery().eq(CspCustomer::getCustomerId, customerId).exists();
    }

    @Override
    @GetMapping("/csp/customer/getByCustomerIds")
    public List<UserInfoVo> getByCustomerIds(@RequestParam("customerIds") List<String> customerIds) {
        if (!CollectionUtils.isEmpty(customerIds)) {
            return cspCustomerService.lambdaQuery()
                    .select(
                            CspCustomer::getId,
                            CspCustomer::getCspId,
                            CspCustomer::getCustomerId,
                            CspCustomer::getName,
                            CspCustomer::getPhone,
                            CspCustomer::getMail,
                            CspCustomer::getCustomerActive,
                            CspCustomer::getPayType
                    )
                    .in(CspCustomer::getCustomerId, customerIds)
                    .list()
                    .stream()
                    .map(cspCustomer -> {
                        UserInfoVo userInfoVo = new UserInfoVo();
                        BeanUtils.copyProperties(cspCustomer, userInfoVo);
                        return userInfoVo;
                    })
                    .collect(Collectors.toList());
        }else{
            return null;
        }

    }

    @Override
    public UserInfoVo getByCustomerId(String customerId) {
        return cspCustomerService.queryById(customerId);
    }

    @Override
    public Boolean recharge(String customerId, Long chargeAmount) {
        return cspCustomerService.recharge(customerId,chargeAmount);
    }
    @Override
    public Long getBalance(String customerId)
    {
        return cspCustomerService.getBalance(customerId);
    }
    @Override
    public Long addBalance(String customerId,Long addMoney){
        return cspCustomerService.addBalance(customerId,addMoney);
    }
    @Override
    public ReduceBalanceResp reduceBalance(String customerId,Long price,Long num,boolean isTryMax){
        return cspCustomerService.reduceBalance(customerId,price,num,isTryMax);
    }
    @GetMapping("/csp/customer/getDetailByCustomerIds")
    @Override
    public List<CustomerDetailResp> getDetailByCustomerIds(Collection<String> customerIds) {
        if(!CollectionUtils.isEmpty(customerIds)){
            return cspCustomerService.lambdaQuery()
                    .in(CspCustomer::getCustomerId, customerIds)
                    .list()
                    .stream()
                    .map(cspCustomer -> {
                        CustomerDetailResp customerDetailResp = new CustomerDetailResp();
                        BeanUtils.copyProperties(cspCustomer, customerDetailResp);
                        return customerDetailResp;
                    })
                    .collect(Collectors.toList());
        }else{
            return new ArrayList<>();
        }
    }

    @GetMapping("/csp/customer/queryByNameOrPhone")
    @Override
    public List<UserInfoVo> queryByNameOrPhone(String nameOrPhone) {
        return cspCustomerService.lambdaQuery()
                .select(
                        CspCustomer::getId,
                        CspCustomer::getCspId,
                        CspCustomer::getCustomerId,
                        CspCustomer::getName,
                        CspCustomer::getPhone,
                        CspCustomer::getMail,
                        CspCustomer::getCustomerActive,
                        CspCustomer::getPayType
                )
                .like(StringUtils.isNotBlank(nameOrPhone), CspCustomer::getName, nameOrPhone)
                .or()
                .like(StringUtils.isNotBlank(nameOrPhone), CspCustomer::getPhone, nameOrPhone)
                .list()
                .stream()
                .map(cspCustomer -> {
                    UserInfoVo userInfoVo = new UserInfoVo();
                    BeanUtils.copyProperties(cspCustomer, userInfoVo);
                    return userInfoVo;
                })
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("/csp/customer/queryCustomerIdsByCspId")
    public List<String> queryCustomerIdsByCspId(String cspId) {
        return cspCustomerService.queryCustomerIdsByCspId(cspId);
    }

    @Override
    @GetMapping("/csp/customer/count")
    public Long countCustomer(@RequestParam("cspId") String cspId, @RequestParam(value = "endTime", required = false) String endTime) {
        return cspCustomerService.lambdaQuery()
                .eq(CspCustomer::getCspId, cspId)
                .lt(endTime != null, CspCustomer::getCreateTime, endTime)
                .count();
    }

    @PostMapping("/csp/getCustomerOption")
    @Override
    public PageResult<CustomerOptionVo> getCustomerOption(@RequestBody CustomerOptionReq optionReq) {
        CspCustomerMapper cspCustomerMapper = (CspCustomerMapper) cspCustomerService.getBaseMapper();
        Page<CustomerOptionVo> page = new Page<>(optionReq.getPageNo(), optionReq.getPageSize());
        cspCustomerMapper.getCustomerOption(optionReq.getOperatorCode(),
                optionReq.getName(),
                SessionContextUtil.getUser().getCspId(),
                page);
        if (CollectionUtils.isNotEmpty(optionReq.getExcludeCustomerIds())) {
            optionReq.getExcludeCustomerIds()
                    .forEach(excludeCustomerId -> page.getRecords()
                            .stream()
                            .filter(option -> option.getCustomerId().equals(excludeCustomerId))
                            .findAny()
                            .ifPresent(option -> option.setContractBanding(false).setChatbotBanding(false))
                    );
        }
        return new PageResult<>(page.getRecords(), page.getTotal());
    }


    @Override
    public Collection<UserEnterpriseInfoVo> getUserEnterpriseInfoByUserIds(Collection<String> userIds) {
        return cspCustomerService.getUserEnterpriseInfoByUserIds(userIds);
    }

    @Override
    public Collection<UserEnterpriseInfoVo> getUserUserIdsLikeEnterpriseAccountName(String enterpriseAccountName) {
        return cspCustomerService.getUserUserIdsLikeEnterpriseAccountName(enterpriseAccountName);
    }

    @Override
    public Long countActiveCustomerByCspId(String cspId) {
        return cspCustomerService.countActiveCustomerByCspId(cspId);
    }

    @Override
    public Long countCustomerByCspId(String cspId) {
        return cspCustomerService.countCustomerByCspId(cspId);
    }

    @Override
    public void changeCustomerToFormal(String customerId) {
        cspCustomerService.changeCustomerToFormal(customerId);
    }

    @Override
    public void delete(String customerId) {
        cspCustomerService.deleteCustomer(customerId);
    }

    @Override
    public String agentLogin(@RequestBody CspAgentLoginReq req) {
        return cspCustomerService.agentLogin(req);
    }

    @Override
    public LoginResp getAgentLoginInfo(String token) {
        return cspCustomerService.getAgentLoginInfo(token);
    }

    @Override
    public CustomerDetailResp getDetailByCustomerIdForDontCheck(String customerId) {
        return cspCustomerService.getDetailByCustomerIdForDontCheck(customerId);
    }

    @Override
    public List<UserInfoVo> getAllCustomer(String cspId) {
        return cspCustomerService.lambdaQuery()
                .select(
                        CspCustomer::getId,
                        CspCustomer::getCspId,
                        CspCustomer::getCustomerId,
                        CspCustomer::getName,
                        CspCustomer::getPhone,
                        CspCustomer::getMail,
                        CspCustomer::getCustomerActive,
                        CspCustomer::getPayType
                )
                .eq(CspCustomer::getCspId, cspId)
                .list()
                .stream()
                .map(cspCustomer -> {
                    UserInfoVo userInfoVo = new UserInfoVo();
                    BeanUtils.copyProperties(cspCustomer, userInfoVo);
                    return userInfoVo;
                })
                .collect(Collectors.toList());
    }

}
