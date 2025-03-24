package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
@Data
@Accessors(chain = true)
public class InsertIdentificationAuditRecordReq {

    @ApiModelProperty(value = "表主键")
    private Long id;

    @ApiModelProperty(value = "资质id")
    private Integer identificationId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "审核结果：0 未认证 1 认证审核中 2 认证不通过 3 认证通过")
    private Integer auditStatus;

    @ApiModelProperty(value = "审核备注")
    private String auditRemark;

    @ApiModelProperty(value = "是否删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;

    @ApiModelProperty(value = "审核人")
    private String reviewer;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "更新者")
    private String updater;


}
