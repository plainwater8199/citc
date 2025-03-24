package com.citc.nce.robot.api.mall.template.vo.req;

import com.citc.nce.robot.api.mall.common.MallCommonContent;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板类型 0:机器人, 1:5G消息", dataType = "Integer", required = true)
    @NotNull(message = "模板类型不能为空")
    private MsType templateType;

    @ApiModelProperty(value = "消息类型(页面上显示文字是模板类型) 1:文本 2:图片 3:视频 4:音频 5:文件 6:单卡 7:多卡 8:位置", dataType = "Integer")
    private Integer messageType;

    @ApiModelProperty(value = "模板名称", dataType = "String", required = true)
    @NotBlank(message = "模板名称不能为空")
    private String templateName;

    @ApiModelProperty(value = "模板描述", dataType = "String")
    @Length(max = 200, message = "模板描述长度超过限制(最大200位)")
    private String templateDesc;

    @ApiModelProperty(value = "模板内容", dataType = "String")
    private MallCommonContent mallCommonContent;

    @ApiModelProperty(value = "5G消息模板内容", dataType = "String")
    private String moduleInformation;
    @ApiModelProperty(value = "5G消息模板按钮", dataType = "String")
    private String shortcutButton;
}
