package com.citc.nce.auth.csp.smsTemplate.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateSearchVo {
    @ApiModelProperty("模板名称/Id，模糊查找")
    private String templateName;

    @ApiModelProperty("账号ID,其它(-1)")
    private String accountId;

    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    @ApiModelProperty("审核状态 0等待送审 1审核中 2审核通过 3审核被拒")
    private Integer status;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
