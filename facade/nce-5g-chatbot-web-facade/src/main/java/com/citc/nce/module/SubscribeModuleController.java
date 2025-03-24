package com.citc.nce.module;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.module.vo.SubscribeModuleInfo;
import com.citc.nce.module.vo.req.SubscribeModuleReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.vo.req.SubscribeModuleSaveReq;
import com.citc.nce.module.vo.resp.SubscribeModuleQueryResp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/csp/subscribe")
@Api(value = "subscribeModuleController", tags = "CSP--订阅组件")
public class SubscribeModuleController {

    @Resource
    private SubscribeModuleApi subscribeModuleApi;


    @PostMapping("/save")
    @ApiOperation(value = "订阅组件新增保存", notes = "订阅组件新增保存")
    public int saveSubscribeModule(@RequestBody SubscribeModuleSaveReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return subscribeModuleApi.saveSubscribeModule(req);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "订阅组件删除", notes = "订阅组件删除")
    public int deleteSubscribeModule(@RequestBody SubscribeModuleReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return subscribeModuleApi.deleteSubscribeModule(req);
    }

    @PostMapping("/update")
    @ApiOperation(value = "订阅组件更新", notes = "订阅组件更新")
    public int updateSubscribeModule(@RequestBody SubscribeModuleSaveReq req) {
        return subscribeModuleApi.updateSubscribeModule(req);
    }

    @PostMapping("/getSubscribeModuleList")
    @ApiOperation(value = "订阅组件列表查询", notes = "订阅组件列表查询")
    public PageResult<SubscribeModuleInfo> getSubscribeModuleList(@RequestBody SubscribeModuleReq req) {
        return subscribeModuleApi.getSubscribeModuleList(req);
    }

    @PostMapping("/getSubscribeModule")
    @ApiOperation(value = "订阅组件单个查询", notes = "订阅组件单个查询")
    public SubscribeModuleQueryResp getSubscribeModule(@RequestBody SubscribeModuleReq req) {
        return subscribeModuleApi.getSubscribeModule(req);
    }

    @PostMapping("/getSubscribeModules")
    @ApiOperation(value = "订阅组件查询全部", notes = "订阅组件查询全部")
    public List<SubscribeModuleReq> getSubscribeModules() {
        return subscribeModuleApi.getSubscribeModules();
    }
}
