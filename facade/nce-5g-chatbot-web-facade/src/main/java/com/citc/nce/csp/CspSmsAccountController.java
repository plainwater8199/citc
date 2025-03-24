package com.citc.nce.csp;

import com.citc.nce.auth.csp.sms.account.CspSmsAccountApi;
import com.citc.nce.auth.csp.sms.account.vo.*;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@Api(value = "CspSmsAccountController", tags = "CSP--短信账号")
public class CspSmsAccountController {

    @Resource
    private CspSmsAccountApi cspSmsAccountApi;
    @Resource
    private CspApi cspApi;


    @PostMapping("/csp/sms/account/save")
    @ApiOperation(value = "新增", notes = "新增")
    @HasCsp
    @Log(title = "短信账号新增")
    int save(@RequestBody @Valid CspSmsAccountSaveReq req) {
        return cspSmsAccountApi.save(req);
    }

    @PostMapping("/csp/sms/account/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @HasCsp
    @Log(title = "短信账号编辑")
    int edit(@RequestBody @Valid CspSmsAccountEditReq req) {
        return cspSmsAccountApi.edit(req);
    }

    @PostMapping("/csp/sms/account/updateStatus")
    @ApiOperation(value = "更新状态", notes = "更新状态")
    @HasCsp
    @Log(title = "短信账号更新状态")
    int updateStatus(@RequestBody @Valid CspSmsAccountUpdateStatusReq req) {
        return cspSmsAccountApi.updateStatus(req);
    }

    @PostMapping("/csp/sms/account/delete")
    @ApiOperation(value = "删除", notes = "删除")
    @HasCsp
    @Log(title = "短信账号更新删除")
    int delete(@RequestBody @Valid CspSmsAccountDeleteReq req) {
        return cspSmsAccountApi.delete(req);
    }

    @PostMapping("/csp/sms/account/queryDetail")
    @ApiOperation(value = "查看详情", notes = "查看详情")
    @HasCsp
    CspSmsAccountDetailResp queryDetail(@RequestBody CspSmsAccountQueryDetailReq req) {
        CspSmsAccountDetailResp resp = cspSmsAccountApi.queryDetail(req);
        String cspId = cspApi.queryCspId(SessionContextUtil.getUser().getUserId());
        if (!resp.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        return resp;
    }

    @GetMapping("/sms/account/queryListByLoginUser")
    @ApiOperation(value = "查询账号", notes = "查询账号")
    PageResult<CspSmsAccountResp> queryListByLoginUser() {
        return cspSmsAccountApi.queryListByLoginUser();
    }

    ;


    @PostMapping("/csp/sms/account/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
    @HasCsp
    public PageResult<CspSmsAccountResp> queryList(@RequestBody CspSmsAccountReq req) {
        return cspSmsAccountApi.queryList(req);
    }
}
