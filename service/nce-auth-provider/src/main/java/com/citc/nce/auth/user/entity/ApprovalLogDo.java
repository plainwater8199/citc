package com.citc.nce.auth.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 审核记录表
 * </p>
 *
 * @author author
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("approval_log")
@ApiModel(value = "ApprovalLog对象", description = "审核记录表")
public class ApprovalLogDo extends BaseDo<ApprovalLogDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
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

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;

}
