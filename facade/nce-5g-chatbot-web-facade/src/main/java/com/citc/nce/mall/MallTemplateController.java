package com.citc.nce.mall;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspPermission;
import com.citc.nce.common.facadeserver.annotations.permissions.chatbot.csp.CspServiceType;
import com.citc.nce.robot.api.mall.template.MallTemplateApi;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateDeletedReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQuery5GMsgListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryDetailReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryRobotListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateSaveReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateUpdateReq;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryListResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateSimpleQueryListResp;
import com.citc.nce.security.annotation.HasCsp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/22 19:35
 */
@RestController
@Api(value = "MallTemplateController", tags = "扩展商城-模板管理")
public class MallTemplateController {

    @Resource
    private MallTemplateApi mallTemplateApi;

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/save")
    @ApiOperation(value = "新增", notes = "新增")
    @HasCsp
    public int save(@RequestBody @Valid MallTemplateSaveReq req) {
        return mallTemplateApi.save(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/update")
    @ApiOperation(value = "更新模板", notes = "更新")
    @HasCsp
    public int update(@RequestBody @Valid MallTemplateUpdateReq req) {
        return mallTemplateApi.update(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/delete")
    @ApiOperation(value = "删除", notes = "删除")
    @HasCsp
    public int delete(@RequestBody @Valid MallTemplateDeletedReq req) {
        return mallTemplateApi.delete(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/queryDetail")
    @ApiOperation(value = "模板详情", notes = "模板详情")
    @HasCsp
    public MallTemplateQueryDetailResp queryDetail(@RequestBody @Valid MallTemplateQueryDetailReq req) {
        return mallTemplateApi.queryDetail(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/queryAvailableList")
    @ApiOperation(value = "可用模板列表查询", notes = "可用模板列表查询")
    public PageResult<MallTemplateQueryListResp> queryAvailableList(@RequestBody @Valid MallTemplateQueryRobotListReq req) {
        return mallTemplateApi.queryAvailableList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/queryRobotList")
    @ApiOperation(value = "机器人模板列表查询", notes = "机器人模板列表查询")
    public PageResult<MallTemplateQueryListResp> queryRobotList(@RequestBody MallTemplateQueryRobotListReq req) {
        return mallTemplateApi.queryRobotList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/query5GMsgList")
    @ApiOperation(value = "5G消息模板列表查询", notes = "5G消息模板列表查询")
    public PageResult<MallTemplateQueryListResp> query5GMsgList(@RequestBody MallTemplateQuery5GMsgListReq req) {
        return mallTemplateApi.query5GMsgList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/simpleRobotList")
    @ApiOperation(value = "极简机器人模板列表查询", notes = "极简机器人模板列表查询")
    public List<MallTemplateSimpleQueryListResp> simpleRobotList(@RequestBody MallTemplateQueryRobotListReq req) {
        return mallTemplateApi.simpleRobotList(req);
    }

    @CspPermission(CspServiceType.TEMP_STORE)
    @PostMapping("/mall/template/simple5GMsgList")
    @ApiOperation(value = "极简5G消息模板列表查询", notes = "极简5G消息模板列表查询")
    public List<MallTemplateSimpleQueryListResp> simple5GMsgList(@RequestBody MallTemplateQuery5GMsgListReq req) {
        return mallTemplateApi.simple5GMsgList(req);
    }
}
