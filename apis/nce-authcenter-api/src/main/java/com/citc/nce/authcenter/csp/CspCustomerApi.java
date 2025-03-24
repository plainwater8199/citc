package com.citc.nce.authcenter.csp;

import com.citc.nce.authcenter.auth.vo.req.LoginReq;
import com.citc.nce.authcenter.auth.vo.req.thirdAuthReq;
import com.citc.nce.authcenter.auth.vo.req.thirdLoginReq;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.csp.vo.CspAgentLoginReq;
import com.citc.nce.authcenter.csp.vo.CustomerActiveReq;
import com.citc.nce.authcenter.csp.vo.CustomerAddReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailReq;
import com.citc.nce.authcenter.csp.vo.CustomerDetailResp;
import com.citc.nce.authcenter.csp.vo.CustomerOptionReq;
import com.citc.nce.authcenter.csp.vo.CustomerOptionVo;
import com.citc.nce.authcenter.csp.vo.CustomerProvinceResp;
import com.citc.nce.authcenter.csp.vo.CustomerSearchReq;
import com.citc.nce.authcenter.csp.vo.CustomerSearchResp;
import com.citc.nce.authcenter.csp.vo.CustomerUpdateReq;
import com.citc.nce.authcenter.csp.vo.ReduceBalanceResp;
import com.citc.nce.authcenter.csp.vo.UserEnterpriseInfoVo;
import com.citc.nce.authcenter.csp.vo.UserInfoForDropdownVo;
import com.citc.nce.authcenter.csp.vo.UserInfoVo;
import com.citc.nce.authcenter.csp.vo.resp.CspInfoResp;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.enums.MessageResourceType;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author jiancheng
 */
@FeignClient(value = "authcenter-service", contextId = "cspCustomer", url = "${authCenter:}")
public interface CspCustomerApi {

    @PostMapping("/csp/customer/save")
    @NotNull
    void save(@RequestBody @Valid CustomerAddReq req);

    @PostMapping("/csp/{encoded}/customer/login")
    LoginResp login(@PathVariable(value = "encoded") String encoded, @RequestBody LoginReq req);

    @PostMapping("/customer/thirdLogin")
    LoginResp thirdLogin(@RequestBody thirdLoginReq req);

    @PostMapping("/customer/thirdAuth")
    LoginResp thirdAuth(@RequestBody thirdAuthReq req);

    @PostMapping("/csp/customer/queryList")
    PageResult<CustomerSearchResp> queryList(@RequestBody CustomerSearchReq req);

    @GetMapping("/csp/customer/queryCustomerOfCSPForDropdown")
    List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(@RequestParam("payType") Integer payType);

    @PostMapping("/csp/customer/queryUserProvince")
    List<CustomerProvinceResp> getCustomerDistribution();

    @PostMapping("/csp/customer/updateCspActive")
    int updateCspActive(@RequestBody CustomerActiveReq req);

    @PostMapping("/csp/customer/getDetailByCustomerId")
    CustomerDetailResp getDetailByCustomerId(@RequestBody CustomerDetailReq req);


    @PostMapping("/csp/customer/updateByUserId")
    int updateCustomer(@RequestBody @Valid CustomerUpdateReq req);

    @GetMapping("/customer/queryMsgSendInfo")
    @ApiOperation(value = "查询客户消息发送情况")
    Map<MsgTypeEnum, List<MessageResourceType>> queryMsgSendInfo(@RequestParam("customerId") String customerId);


    @PostMapping("/csp/customer/getPermission")
    String getUserPermission(@RequestParam("customerId") String customerId);


    @PostMapping("/csp/getCspInfo")
    CspInfoResp getCspInfo(@RequestParam("userId") String userId);

    @GetMapping("/csp/customer/{customerId}/exists")
    Boolean existsById(@PathVariable("customerId") String customerId);

    @GetMapping("/csp/customer/getByCustomerIds")
    List<UserInfoVo> getByCustomerIds(@RequestParam("customerIds") List<String> customerIds);

    @GetMapping("/csp/customer/getByCustomerId")
    UserInfoVo getByCustomerId(@RequestParam("customerId") String customerId);

    @GetMapping("/csp/customer/recharge")
    Boolean recharge(@RequestParam("customerId") String customerId, @RequestParam("chargeAmount") Long chargeAmount);

    @GetMapping("/csp/customer/getBalance")
    Long getBalance(@RequestParam("customerId") String customerId);

    @GetMapping("/csp/customer/addBalance")
    Long addBalance(@RequestParam("customerId") String customerId, @RequestParam("addMoney") Long addMoney);

    @PostMapping("/csp/customer/reduceBalance")
    ReduceBalanceResp reduceBalance(@RequestParam("customerId") String customerId, @RequestParam("price") Long price, @RequestParam("num") Long num, @RequestParam("isTryMax") boolean isTryMax);

    @GetMapping("/csp/customer/getDetailByCustomerIds")
    List<CustomerDetailResp> getDetailByCustomerIds(@RequestParam("customerIds") Collection<String> customerIds);

    @GetMapping("/csp/customer/queryByNameOrPhone")
    List<UserInfoVo> queryByNameOrPhone(@RequestParam("nameOrPhone") String nameOrPhone);

    @GetMapping("/csp/customer/queryCustomerIdsByCspId")
    List<String> queryCustomerIdsByCspId(@RequestParam("cspId") String cspId);

    @GetMapping("/csp/customer/count")
    Long countCustomer(@RequestParam("cspId") String cspId, @RequestParam(value = "endTime", required = false) String endTime);

    @PostMapping("/csp/getCustomerOption")
    PageResult<CustomerOptionVo> getCustomerOption(@RequestBody @Valid CustomerOptionReq optionReq);


    @GetMapping("/csp/customer/getUserEnterpriseInfoByUserIds")
    Collection<UserEnterpriseInfoVo> getUserEnterpriseInfoByUserIds(@RequestParam("userIds") Collection<String> userIds);

    @GetMapping("/csp/customer/getUserUserIdsLikeEnterpriseAccountName")
    Collection<UserEnterpriseInfoVo> getUserUserIdsLikeEnterpriseAccountName(@RequestParam("enterpriseAccountName") String enterpriseAccountName);

    /**
     * 查询指定csp已激活得客户数
     *
     * @param cspId 客户数
     */
    @GetMapping("/csp/countActiveCustomerByCspId")
    Long countActiveCustomerByCspId(@RequestParam("cspId") String cspId);

    /**
     * 查询指定csp所以客户数
     *
     * @param cspId 客户数
     */
    @GetMapping("/csp/countCustomerByCspId")
    Long countCustomerByCspId(@RequestParam("cspId") String cspId);

    /**
     * 转正
     *
     * @param customerId
     * @return
     */
    @GetMapping("/csp/changeCustomerToFormal")
    void changeCustomerToFormal(@RequestParam("customerId") String customerId);

    /**
     * 删除客户
     */
    @GetMapping("/csp/customer/delete")
    void delete(@RequestParam("customerId") String customerId);

    /**
     * CSP代登录
     */
    @PostMapping("/csp/customer/agentLogin")
    String agentLogin(@RequestBody @Valid CspAgentLoginReq req);

    @GetMapping("/csp/customer/agentLogin")
    LoginResp getAgentLoginInfo(@RequestParam("token") String token);

    /**
     * 开发者服务使用，不需要校验用户是否已登录
     *
     * @param customerId
     * @return
     */
    @GetMapping("/csp/customer/getDetailByCustomerIdForDontCheck")
    CustomerDetailResp getDetailByCustomerIdForDontCheck(@RequestParam("customerId") String customerId);


    @GetMapping("/csp/customer/allCustomer")
    List<UserInfoVo> getAllCustomer(@RequestParam("cspId") String cspId);

}

