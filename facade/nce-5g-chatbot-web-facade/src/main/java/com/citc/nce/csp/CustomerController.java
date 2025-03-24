package com.citc.nce.csp;

import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.authcenter.auth.vo.resp.LoginResp;
import com.citc.nce.authcenter.csp.CspCustomerApi;
import com.citc.nce.authcenter.csp.vo.*;
import com.citc.nce.common.core.enums.MsgTypeEnum;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.log.enums.OperatorType;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.common.web.utils.dh.ECDHService;
import com.citc.nce.robot.enums.MessageResourceType;
import com.citc.nce.security.annotation.HasCsp;
import com.citc.nce.tenant.MsgRecordApi;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@RequestMapping("/csp")
@Api(value = "CustomerController", tags = "CSP--客户管理")
public class CustomerController {
    @Autowired
    private CspCustomerApi cspCustomerApi;
    @Resource
    private ECDHService ecdhService;
    @Autowired
    private MsgRecordApi msgRecordApi;

    @PostMapping("/customer/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @HasCsp
    public PageResult<CustomerSearchResp> queryList(@RequestBody CustomerSearchReq req) {
        PageResult<CustomerSearchResp> page = cspCustomerApi.queryList(req);
        for (CustomerSearchResp body : page.getList()) {
            body.setPhone(ecdhService.encode(body.getPhone()));
            body.setIdCard(ecdhService.encode(body.getIdCard()));
        }
        return page;
    }
    @GetMapping("/customer/queryCustomerOfCSPForDropdown")
    @ApiOperation(value = "查询csp所有未删除客户（下拉框用）", notes = "查询csp所有未删除客户")
    @HasCsp
    public List<UserInfoForDropdownVo> queryCustomerOfCSPForDropdown(@RequestParam("payType") Integer payType) {
        return cspCustomerApi.queryCustomerOfCSPForDropdown(payType);
    }
    @PostMapping("/customer/save")
    @ApiOperation(value = "新增保存", notes = "新增保存")
    @HasCsp
    @Log(title = "CSP侧-新增CUS", operatorType = OperatorType.CSP, isSaveRequestData = false)
    public int save(@RequestBody @Valid CustomerAddReq req) {
        cspCustomerApi.save(req);
        return 0;
    }

    @PostMapping("/customer/getDetailByCustomerId")
    @ApiOperation(value = "查看详情", notes = "查看详情")
    @HasCsp
    public CustomerDetailResp getDetailByCustomerId(@RequestBody CustomerDetailReq req) {
        String cspId = SessionContextUtil.verifyCspLogin();
        if (!req.getCustomerId().startsWith(cspId)) {
            throw new BizException("不是你的客户禁止操作");
        }
        CustomerDetailResp body = cspCustomerApi.getDetailByCustomerId(req);
        body.setPhone(ecdhService.encode(body.getPhone()));
        body.setIdCard(ecdhService.encode(body.getIdCard()));
        body.setCreditCode(ecdhService.encode(body.getCreditCode()));
        return body;
    }

    @PostMapping("/customer/updateByUserId")
    @ApiOperation(value = "编辑", notes = "编辑")
    @HasCsp
    @Log(title = "CSP侧-编辑CUS", operatorType = OperatorType.CSP, isSaveRequestData = false)
    public int updateByUserId(@RequestBody @Valid CustomerUpdateReq req) {
        return cspCustomerApi.updateCustomer(req);
    }


    @GetMapping("/customer/queryMsgSendInfo")
    @ApiOperation(value = "查询客户消息发送情况")
    public Map<MsgTypeEnum, List<MessageResourceType>> queryMsgSendInfo(@RequestParam String customerId) {
        return cspCustomerApi.queryMsgSendInfo(customerId);
    }


    @PostMapping("/customer/delete")
    @ApiOperation(value = "删除", notes = "删除")
    @HasCsp
    @Log(title = "删除客户")
    public void delete(@RequestParam("customerId") String customerId) {
        String cspId = SessionContextUtil.verifyCspLogin();
        if (!customerId.startsWith(cspId)) {
            throw new BizException(GlobalErrorCode.USER_AUTH_ERROR);
        }
        cspCustomerApi.delete(customerId);
    }


    @PostMapping("/customer/updateCspActive")
    @ApiOperation(value = "禁用/启用", notes = "禁用/启用")
    @HasCsp
    @Log(title = "CSP侧-禁用/启用CUS", operatorType = OperatorType.CSP, isSaveRequestData = false)
    public int updateCspActive(@RequestBody @Valid CustomerActiveReq req) {
        return cspCustomerApi.updateCspActive(req);
    }

    @PostMapping("/customer/agentLogin")
    @ApiOperation(value = "服务商代登录", notes = "服务商代登录")
    @HasCsp
    @Log(title = "CSP侧-服务商代登录", operatorType = OperatorType.CSP, isSaveRequestData = false)
    public String agentLogin(@RequestBody @Valid CspAgentLoginReq req) {
        return cspCustomerApi.agentLogin(req);
    }

    @GetMapping("/customer/agentLogin")
    @ApiOperation(value = "服务商代登录", notes = "服务商代登录")
    @Log(title = "CSP侧-服务商代登录", operatorType = OperatorType.CSP, isSaveRequestData = false)
    public LoginResp getAgentLoginInfo(@RequestParam("token") String token) {
        return cspCustomerApi.getAgentLoginInfo(token);
    }


    @PostMapping("/getCustomerOption")
    @ApiOperation("获取合同客户下拉框")
    @HasCsp
    public PageResult<CustomerOptionVo> getContractCustomerOptions(@RequestBody @Valid CustomerOptionReq optionReq) {
        return cspCustomerApi.getCustomerOption(optionReq);
    }
}
