package com.citc.nce.auth.messagetemplate.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/8/8 16:23
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageTemplateResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 样式名称
     */
    @ApiModelProperty("样式名称")
    private String templateName;

    /**
     * 消息类型  1 文本  2 图片 3 视频 4 音频  6 单卡 7 多卡 8  位置
     */
    @ApiModelProperty("消息类型")
    private int messageType;

    /**
     * 模板类型(普通模板:1,个性模板:2)
     */
    @ApiModelProperty("模板类型(普通模板:1,个性模板:2)")
    private Integer templateType;

    /**
     * 数据json
     */
    @ApiModelProperty("消息数据JSON")
    private String moduleInformation;

    /**
     * 快捷按钮
     */
    @ApiModelProperty("快捷按钮")
    private String shortcutButton;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String creator;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    @ApiModelProperty("更新者")
    private String updater;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 模板状态
     */
    @ApiModelProperty("模板状态")
    private int status;
    /**
     * 删除时间
     */
    @ApiModelProperty("删除时间")
    private Date deleteTime;
    /**
     * 模板对应运营商审核状态
     */
    @ApiModelProperty("模板对应运营商审核状态")
    List<TemplateOwnershipReflectResp> audits;
    /**
     * 所属chatbot账号
     */
    @ApiModelProperty("所属chatbot账号")
    String accounts;
    /**
     * 样式信息
     */
    @ApiModelProperty("所属chatbot账号")
    String styleInformation;

    private String platformTemplateId;

    //============= 以下内容在5G消息发送节点中进行设置
    @ApiModelProperty("回落短信内容")
    private String fallbackSmsContent;

    @ApiModelProperty("允许回落阅信模板id")
    private Long fallbackReadingLetterTemplateId;

    @ApiModelProperty("是否有未过审的素材")
    private boolean hasNotReviewedMaterial = false;

}
