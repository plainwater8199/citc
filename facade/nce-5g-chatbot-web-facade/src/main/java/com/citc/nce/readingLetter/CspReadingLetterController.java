package com.citc.nce.readingLetter;

import com.citc.nce.auth.csp.readingLetter.CspReadingLetterAccountApi;
import com.citc.nce.auth.csp.readingLetter.vo.*;
import com.citc.nce.auth.prepayment.vo.MessageAccountSearchVo;
import com.citc.nce.authcenter.csp.CspApi;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.log.annotation.Log;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@Api(value = "CspReadingLetterController", tags = "CSP--阅信+账号")
public class CspReadingLetterController {

    @Resource
    private CspReadingLetterAccountApi cspReadingLetterAccountApi;

    @Resource
    private CspApi cspApi;

    @ApiOperation("查询阅信+消息账号列表")
    @PostMapping("/csp/readingLetter/account/list")
    @HasCsp
    public PageResult<CspReadingLetterAccountListResp> queryList(@RequestBody @Valid MessageAccountSearchVo searchVo) {
        return cspReadingLetterAccountApi.queryList(searchVo);
    }

    @ApiOperation("阅信+消息账号新增")
    @PostMapping("/csp/readingLetter/account/save")
    @HasCsp
    @Log(title = "阅信+账号新增")
    Boolean save(@RequestBody @Valid CspReadingLetterAccountSaveReq req) {
        return cspReadingLetterAccountApi.save(req);
    }

    @ApiOperation("阅信+消息账号编辑")
    @PostMapping("/csp/readingLetter/account/edit")
    @HasCsp
    @Log(title = "阅信+账号编辑")
    Boolean edit(@RequestBody @Valid CspReadingLetterAccountEditReq req) {
        return cspReadingLetterAccountApi.edit(req);
    }

    @ApiOperation("阅信+消息账号启/禁用")
    @PostMapping("/csp/readingLetter/account/updateStatus")
    @HasCsp
    @Log(title = "阅信+账号更新状态")
    int updateStatus(@RequestBody @Valid CspReadingLetterAccountUpdateStatusReq req) {
        return cspReadingLetterAccountApi.updateStatus(req);
    }

    @ApiOperation("阅信+消息账号删除")
    @PostMapping("/csp/readingLetter/account/delete")
    @HasCsp
    @Log(title = "阅信+账号更新删除")
    int delete(@RequestBody @Valid CspReadingLetterIdReq req) {
        return cspReadingLetterAccountApi.delete(req);
    }

    @ApiOperation("阅信+消息账号详情")
    @PostMapping("/csp/readingLetter/account/queryDetail")
    @HasCsp
    CspReadingLetterDetailResp queryDetail(@RequestBody @Valid CspReadingLetterIdReq req) {
        return cspReadingLetterAccountApi.queryDetail(req);
    }

    @ApiOperation("阅信+消息账号客户验证")
    @PostMapping("/csp/readingLetter/account/customer/check")
    @HasCsp
    CspReadingLetterMsgResp customerCheck(@RequestBody @Valid CspReadingLetterCustomerCheckReq req) {
        return cspReadingLetterAccountApi.customerCheck(req);
    }

    @ApiOperation("阅信+消息账号名称验证")
    @PostMapping("/csp/readingLetter/account/name/check")
    @HasCsp
    CspReadingLetterMsgResp accountNameCheck(@RequestBody @Valid CspReadingLetterAccountNameCheckReq req) {
        return cspReadingLetterAccountApi.accountNameCheck(req);
    }

    @ApiOperation("查询customer可以使用的阅信+账号")
    @PostMapping("/customer/readingLetter/account/available")
    public List<CustomerReadingLetterAccountVo> available() {
        return cspReadingLetterAccountApi.available();
    }

    @ApiOperation("查询5G阅信/阅信+消息账号列表")
    @PostMapping("/customer/readingLetter/account/list")
    public List<CustomerReadingLetterAccountListVo> queryList(@RequestBody CustomerReadingLetterAccountSearchReq searchVo) {
        return cspReadingLetterAccountApi.queryCustomerReadingLetterAccountList(searchVo);
    }

}
