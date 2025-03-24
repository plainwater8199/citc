package com.citc.nce.module;

import cn.hutool.core.util.ObjectUtil;
import com.citc.nce.module.vo.req.SignModuleReq;
import com.citc.nce.common.core.exception.BizException;
import com.citc.nce.common.core.exception.GlobalErrorCode;
import com.citc.nce.common.core.pojo.BaseUser;
import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.util.SessionContextUtil;
import com.citc.nce.module.vo.req.SignModuleUpdateReq;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/csp/sign")
@Api(value = "signModuleController", tags = "CSP--打卡组件")
public class SignModuleController {

    @Resource
    private SignModuleApi signModuleApi;


    @PostMapping("/save")
    @ApiOperation(value = "打卡组件新增保存", notes = "打卡组件新增保存")
    public int saveSignModule(@RequestBody SignModuleReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return signModuleApi.saveSignModule(req);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "打卡组件删除", notes = "打卡组件删除")
    public int deleteSignModule(@RequestBody SignModuleReq req) {
        BaseUser baseUser = SessionContextUtil.getUser();
        if (ObjectUtil.isEmpty(baseUser)){
            throw new BizException(GlobalErrorCode.NOT_LOGIN_ERROR);
        }
        return signModuleApi.deleteSignModule(req);
    }

    @PostMapping("/update")
    @ApiOperation(value = "打卡组件更新", notes = "打卡组件更新")
    public int updateSignModule(@RequestBody SignModuleUpdateReq req) {
        return signModuleApi.updateSignModule(req);
    }

    @PostMapping("/getSignModuleList")
    @ApiOperation(value = "打卡组件列表查询", notes = "打卡组件列表查询")
    public PageResult<SignModuleReq> getSignModuleList(@RequestBody SignModuleReq req) {
        return signModuleApi.getSignModuleList(req);
    }

    @PostMapping("/getSignModules")
    @ApiOperation(value = "打卡组件查询全部", notes = "打卡组件查询全部")
    public List<SignModuleReq> getSignModules() {
        return signModuleApi.getSignModules();
    }

    @PostMapping("/getSignModule")
    @ApiOperation(value = "打卡组件查询单个", notes = "打卡组件查询单个")
    public SignModuleReq getSignModule(@RequestBody SignModuleReq req) {
        return signModuleApi.getSignModule(req);
    }

}
