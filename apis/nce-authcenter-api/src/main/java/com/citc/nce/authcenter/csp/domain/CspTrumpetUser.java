package com.citc.nce.authcenter.csp.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * csp小号
 * </p>
 *
 * @author bydud
 * @since 2024-01-26 10:01:59
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_trumpet_user")
@ApiModel(value = "CspTrumpetUser对象", description = "csp小号")
public class CspTrumpetUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("小号id")
    @TableId(value = "ctu_id", type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ctuId;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty("cspId")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty("账号")
    @TableField("account_name")
    private String accountName;

    @ApiModelProperty("手机号")
    @TableField("phone")
    private String phone;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
