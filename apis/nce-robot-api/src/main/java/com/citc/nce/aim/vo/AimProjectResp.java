package com.citc.nce.aim.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class AimProjectResp implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "int")
    private long id;

    @ApiModelProperty(value = "项目Id", dataType = "String")
    private String projectId;

    @ApiModelProperty(value = "项目名称", dataType = "String")
    private String projectName;

    @ApiModelProperty(value = "客户号码", dataType = "String")
    private String calling;

    @ApiModelProperty(value = "通道账号", dataType = "String")
    private String pathKey;

    @ApiModelProperty(value = "通道秘钥", dataType = "String")
    private String secret;

    @ApiModelProperty(value = "短信内容", dataType = "String")
    private String smsTemplate;

    @ApiModelProperty(value = "项目状态 0:禁用 1:启用", dataType = "int")
    private int projectStatus;

    @ApiModelProperty(value = "是否删除 0:未删除 1:已删除", dataType = "int")
    private int deleted;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "订单状态 0:无开启订单 1:有开启订单", dataType = "int")
    private int orderStatus;
}
