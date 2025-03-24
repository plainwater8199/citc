package com.citc.nce.robot.api.mall.template.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateQueryRobotListReq extends MallTemplateBaseQueryReq {

    @NotNull
    private Integer templateType;

    private String cspId;
}
