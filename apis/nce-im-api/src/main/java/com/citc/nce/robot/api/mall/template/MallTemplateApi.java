package com.citc.nce.robot.api.mall.template;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateDeletedReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQuery5GMsgListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryDetailReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryRobotListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateSaveReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateUpdateReq;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryListResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateSimpleQueryListResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>扩展商城-模板</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 15:35
 */
@FeignClient(value = "im-service",contextId="MallTemplateApi", url = "${im:}")
public interface MallTemplateApi {

    @PostMapping("/mall/template/save")
    int save(@RequestBody @Valid MallTemplateSaveReq req);
    @PostMapping("/mall/template/update")
    int update(@RequestBody @Valid MallTemplateUpdateReq req);
    @PostMapping("/mall/template/delete")
    int delete(@RequestBody @Valid MallTemplateDeletedReq req);
    @PostMapping("/mall/template/queryDetail")
    MallTemplateQueryDetailResp queryDetail(@RequestBody @Valid MallTemplateQueryDetailReq req);
    @PostMapping("/mall/template/queryRobotList")
    PageResult<MallTemplateQueryListResp> queryRobotList(@RequestBody MallTemplateQueryRobotListReq req);
    @PostMapping("/mall/template/query5GMsgList")
    PageResult<MallTemplateQueryListResp> query5GMsgList(@RequestBody MallTemplateQuery5GMsgListReq req);
    @PostMapping("/mall/template/queryAvailableList")
    PageResult<MallTemplateQueryListResp> queryAvailableList(@RequestBody MallTemplateQueryRobotListReq req);

    @PostMapping("/mall/template/simpleRobotList")
    List<MallTemplateSimpleQueryListResp> simpleRobotList(@RequestBody MallTemplateQueryRobotListReq req);
    @PostMapping("/mall/template/simple5GMsgList")
    List<MallTemplateSimpleQueryListResp> simple5GMsgList(@RequestBody MallTemplateQuery5GMsgListReq req);

}
