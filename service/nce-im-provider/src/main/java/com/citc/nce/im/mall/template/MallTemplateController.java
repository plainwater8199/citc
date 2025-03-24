package com.citc.nce.im.mall.template;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.im.mall.template.service.MallTemplateService;
import com.citc.nce.robot.api.mall.template.MallTemplateApi;
import com.citc.nce.robot.api.mall.template.vo.req.*;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryListResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateSimpleQueryListResp;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>扩展商城-商品</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 16:59
 */
@RestController
public class MallTemplateController implements MallTemplateApi {


    @Resource
    MallTemplateService service;

    @Override
    public int save(MallTemplateSaveReq req) {
        return service.save(req);
    }

    @Override
    public int update(MallTemplateUpdateReq req) {
        return service.update(req);
    }

    @Override
    public int delete(MallTemplateDeletedReq req) {
        return service.delete(req.getTemplateId());
    }

    @Override
    public MallTemplateQueryDetailResp queryDetail(MallTemplateQueryDetailReq req) {
        return service.queryDetail(req.getTemplateId());
    }

    @Override
    public PageResult<MallTemplateQueryListResp> queryRobotList(MallTemplateQueryRobotListReq req) {
        return service.queryRobotList(req.getTemplateName(), req.getPageNo(), req.getPageSize());
    }

    @Override
    public PageResult<MallTemplateQueryListResp> query5GMsgList(MallTemplateQuery5GMsgListReq req) {
        return service.query5GMsgList(req.getTemplateName(), req.getMessageType(), req.getPageNo(), req.getPageSize());
    }

    @Override
    public PageResult<MallTemplateQueryListResp> queryAvailableList(@RequestBody MallTemplateQueryRobotListReq req) {
        return service.queryAvailableList(req);
    }

    @Override
    public List<MallTemplateSimpleQueryListResp> simpleRobotList(@RequestBody MallTemplateQueryRobotListReq req) {
        return service.simpleRobotList(req.getTemplateName());
    }

    @Override
    public List<MallTemplateSimpleQueryListResp> simple5GMsgList(@RequestBody MallTemplateQuery5GMsgListReq req) {
        return service.simple5GMsgList(req.getTemplateName(), req.getMessageType());
    }
}
