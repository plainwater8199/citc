package com.citc.nce.module.controller;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.module.SignNamesApi;
import com.citc.nce.module.service.SignNamesService;
import com.citc.nce.module.vo.req.SignNamesReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/csp/signNames")
@Api(value = "signNamesController", tags = "CSP--打卡名单")
public class SignNamesController implements SignNamesApi {

    @Resource
    private SignNamesService signNamesService;


    @PostMapping("/save")
    @ApiOperation(value = "打卡名单新增保存", notes = "打卡名单新增保存")
    public int saveSignNames(SignNamesReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return signNamesService.saveSignNames(req);
    }

    @PostMapping("/updateSignNames")
    @ApiOperation(value = "打卡名单更新", notes = "打卡名单更新")
    public int updateSignNames(SignNamesReq req) {
        return signNamesService.updateSignNames(req);
    }

    @PostMapping("/getSignNamesList")
    @ApiOperation(value = "打卡名单列表查询", notes = "打卡名单列表查询")
    public PageResult<SignNamesReq> getSignNamesList(SignNamesReq req) {
        return signNamesService.getSignNamesList(req);
    }

    @Override
    @PostMapping("/saveSignNamesForButton")
    public void saveSignNamesForButton(SignNamesReq req) {
        signNamesService.saveSignNamesForButton(req);
    }

}
