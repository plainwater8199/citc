package com.citc.nce.robot.api.mall.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 11:48
 */
@Data
public class FiveG {

    @ApiModelProperty(value = "素材")
    private Material material;
    @ApiModelProperty(value = "外部链接")
    private String[] formLink;
    @ApiModelProperty(value = "表单list")
    private List<Form> form;
    @ApiModelProperty(value = "消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置", dataType = "Integer")
    private Integer messageType;
    @ApiModelProperty(value = "5G消息模板内容")
    private String moduleInformation;
    private String shortcutButton;
    @ApiModelProperty(value = "预览图")
    private String thumbnail;
}
