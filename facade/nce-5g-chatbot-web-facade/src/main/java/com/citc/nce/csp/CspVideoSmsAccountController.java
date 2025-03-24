package com.citc.nce.csp;

import com.citc.nce.auth.csp.chatbot.vo.ChatbotTariffAdd;
import com.citc.nce.auth.csp.recharge.vo.TariffOptions;
import com.citc.nce.auth.csp.videoSms.account.CspVideoSmsAccountApi;
import com.citc.nce.auth.csp.videoSms.account.vo.*;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:35
 */
@RestController
@Api(value = "CspVideoSmsAccountController", tags = "CSP--视频短信账号")
public class CspVideoSmsAccountController {

    @Resource
    private CspVideoSmsAccountApi cspVideoSmsAccountApi;

    @Resource
    private CspApi cspService;

    @PostMapping("/csp/videoSms/account/queryList")
    @ApiOperation(value = "列表查询", notes = "列表查询")
//    @HasCsp
    public PageResult<CspVideoSmsAccountResp> queryList(@RequestBody CspVideoSmsAccountReq req) {
        return cspVideoSmsAccountApi.queryList(req);
    }

    @GetMapping("/videoSms/account/queryListByLoginUser")
    @ApiOperation(value = "查询账号", notes = "查询账号")
    public PageResult<CspVideoSmsAccountResp> queryListByLoginUser() {
        return cspVideoSmsAccountApi.queryListByLoginUser();
    }

    @PostMapping("/csp/videoSms/account/save")
    @ApiOperation(value = "新增", notes = "新增")
    @HasCsp
    @Log(title = "视频短信新增")
    int save(@RequestBody @Valid CspVideoSmsAccountSaveReq req) {
        return cspVideoSmsAccountApi.save(req);
    }

    @PostMapping("/csp/videoSms/account/edit")
    @ApiOperation(value = "编辑", notes = "编辑")
    @HasCsp
    @Log(title = "视频短信编辑")
    int edit(@RequestBody @Valid CspVideoSmsAccountEditReq req) {
        return cspVideoSmsAccountApi.edit(req);
    }

    @PostMapping("/csp/videoSms/account/updateStatus")
    @ApiOperation(value = "更新状态", notes = "更新状态")
    @HasCsp
    @Log(title = "视频短信更新状态")
    int updateStatus(@RequestBody @Valid CspVideoSmsAccountUpdateStatusReq req) {
        return cspVideoSmsAccountApi.updateStatus(req);
    }

    @PostMapping("/csp/videoSms/account/delete")
    @ApiOperation(value = "删除", notes = "删除")
    @HasCsp
    @Log(title = "视频短信删除")
    int delete(@RequestBody @Valid CspVideoSmsAccountDeleteReq req) {
        return cspVideoSmsAccountApi.delete(req);
    }

    @PostMapping("/csp/videoSms/account/queryDetail")
    @ApiOperation(value = "查看详情", notes = "查看详情")
    @HasCsp
    CspVideoSmsAccountDetailResp queryDetail(@RequestBody CspVideoSmsAccountQueryDetailReq req) {
        CspVideoSmsAccountDetailResp resp = cspVideoSmsAccountApi.queryDetail(req);
        String cspId = cspService.queryCspId(SessionContextUtil.getUser().getUserId());
        if (!resp.getCustomerId().substring(0, 10).equals(cspId)) {
            throw new BizException("你操作的客户不属于你");
        }
        return resp;
    }
}
