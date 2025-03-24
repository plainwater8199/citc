package com.citc.nce.module;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.module.vo.SubscribeContentInfo;
import com.citc.nce.module.vo.SubscribeModuleInfo;
import com.citc.nce.module.vo.req.SubscribeContentDeleteReq;
import com.citc.nce.module.vo.req.SubscribeContentQueryListReq;
import com.citc.nce.module.vo.req.SubscribeContentSaveReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.util.SessionContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/csp/subscribeContent")
@Api(value = "subscribeNamesController", tags = "CSP--订阅内容")
public class SubscribeContentController {

    @Resource
    private SubscribeContentApi subscribeContentApi;


    @PostMapping("/save")
    @ApiOperation(value = "订阅内容新增保存", notes = "订阅内容新增保存")
    public String saveSubscribeContent(@RequestBody SubscribeContentSaveReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return subscribeContentApi.saveSubscribeContent(req);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "订阅组件删除", notes = "订阅组件删除")
    public int deleteSubscribeContent(@RequestBody SubscribeContentDeleteReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return subscribeContentApi.deleteSubscribeContent(req);
    }

    @PostMapping("/update")
    @ApiOperation(value = "订阅内容更新", notes = "订阅内容更新")
    public int updateSubscribeContent(@RequestBody SubscribeContentSaveReq req) {
        return subscribeContentApi.updateSubscribeContent(req);
    }

    @PostMapping("/getSubscribeContentList")
    @ApiOperation(value = "订阅内容列表查询", notes = "订阅内容列表查询")
    public PageResult<SubscribeContentInfo> getSubscribeContentList(@RequestBody SubscribeContentQueryListReq req) {
        return subscribeContentApi.getSubscribeContentList(req);
    }

}
