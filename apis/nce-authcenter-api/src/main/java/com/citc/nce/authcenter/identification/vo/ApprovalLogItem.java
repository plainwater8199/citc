package com.citc.nce.authcenter.identification.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
public class ApprovalLogItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    private Long id;

    @ApiModelProperty(value = "审核记录关联id")
    private String approvalLogId;

    @ApiModelProperty(value = "操作时间")
    private Date handleTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "管理员账号id")
    private String adminUserId;

    @ApiModelProperty(value = "管理员账号name")
    private String adminUserName;

    @ApiModelProperty(value = "创建者")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updater;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
