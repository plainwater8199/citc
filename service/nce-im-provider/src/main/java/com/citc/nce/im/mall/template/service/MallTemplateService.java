package com.citc.nce.im.mall.template.service;

import com.citc.nce.common.core.pojo.PageResult;
import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateQueryRobotListReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateSaveReq;
import com.citc.nce.robot.api.mall.template.vo.req.MallTemplateUpdateReq;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryDetailResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateQueryListResp;
import com.citc.nce.robot.api.mall.template.vo.resp.MallTemplateSimpleQueryListResp;
import com.citc.nce.robot.api.materialSquare.vo.summary.TemplateInfo;
import com.citc.nce.robot.api.materialSquare.vo.summary.req.TemplateListQueryReq;

import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 17:00
 */
public interface MallTemplateService {

    int save(MallTemplateSaveReq req);

    int update(MallTemplateUpdateReq req);

    int delete(String templateId);

    int addSnapshotUuid(String templateId, String snapshotUuid);

    MallTemplateQueryDetailResp queryDetail(String templateId);
    MallCommonContent getMallCommonContent(String templateId);

    PageResult<MallTemplateQueryListResp> queryRobotList(String templateName, Integer pageNo, Integer pageSize);

    PageResult<MallTemplateQueryListResp> query5GMsgList(String templateName, Integer messageType, Integer pageNo, Integer pageSize);

    PageResult<MallTemplateQueryListResp> queryAvailableList(MallTemplateQueryRobotListReq req);

    List<MallTemplateSimpleQueryListResp> simpleRobotList(String templateName);

    List<MallTemplateSimpleQueryListResp> simple5GMsgList(String templateName, Integer messageType);

    List<TemplateInfo> templateListQuery(TemplateListQueryReq req);

    void updateMssID(String msId, Long mssId);

    void deleteMssIDForIds(List<String> templateIDList);
}
