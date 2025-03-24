package com.citc.nce.robot.api.mall.template.vo.resp;

import com.citc.nce.robot.api.mall.common.MallCommonContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateQueryDetailResp {

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "模板名称", dataType = "String")
    private String templateName;

    @ApiModelProperty(value = "模板uuid", dataType = "String")
    private String templateId;

    @ApiModelProperty(value = "快照uuid", dataType = "String")
    private String snapshotUuid;

    @ApiModelProperty(value = "模板描述", dataType = "String")
    private String templateDesc;

    @ApiModelProperty(value = "5G消息模板内容", dataType = "String")
    private String moduleInformation;

    @ApiModelProperty(value = "5G消息模板按钮", dataType = "String")
    private String shortcutButton;

    @ApiModelProperty(value = "模板内容", dataType = "String")
    private MallCommonContent mallCommonContent;

    @ApiModelProperty(value = "模板类型 0:机器人, 1:5G消息", dataType = "Integer")
    private Integer templateType;

    @ApiModelProperty(value = "消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置", dataType = "Integer")
    private Integer messageType;

    @ApiModelProperty(value = "商品绑定状态 0:未绑定 1:已绑定", dataType = "Integer")
    private Integer status;

    @ApiModelProperty(value = "创建者id", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    private Integer deleted;

    @ApiModelProperty("关联作品ID")
    private Long mssId;


}
