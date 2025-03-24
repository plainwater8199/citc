package com.citc.nce.auth.csp.chatbot.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("chatbot_manage_cmcc")
@Accessors(chain = true)
public class ChatbotManageDo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String customerId;

    private String chatbotAccountId;

    private String chatbotId;

    private String chatbotName;

    private String chatbotServiceCode;

    private String chatbotServiceExtraCode;

    private String chatbotLogoUrl;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("大区")
    private String region;

    private String chatbotType;

    /**
     * 行业类型
     * 1-党政军,2-民生，3-金融，4-物流，5-游戏，6-电商，7-微商（个人），8-沿街商铺（中小），9-企业（大型）,10-教育培训,11-房地产,12-医疗器械、药店，13-其他
     */
    private String chatbotIndustryType;

    private String chatbotSmsPort;

    private String chatbotSmsSign;

    private String chatbotServiceDesc;

    private String chatbotCallBackNumber;

    private String chatbotMail;

    private String chatbotHomepage;

    /**
     * 气泡颜色 默认0:跟随终端， 1:自定义
     */
    private Integer chatbotBubbleColor;

    @TableField("chatbot_bubble_color_rgb")
    private String chatbotBubbleColorRGB;

    /**
     * chatbos提供者 0：不显示 1：显示
     */
    @TableField("chatbot_ISP_is_display")
    private Integer chatbotISPIsDisplay;

    private String chatbotFileUrl;

    private String annexName;

    /**
     * 移动chatbot状态
     * 11：新增审核不通过，12：变更审核不通过，20：管理平台新增审核中，21：管理平台变更审核中，24：上架审核中，25：上架审核不通过，26：调试白名单审核，27：调试白名单审核不通过，30：在线，31：已下线，40：暂停，41：黑名单，42：已下线（关联的CSP被下线），50：调试
     */
    /**
     * 60 新增待审核
     * 61 变更待审核
     * 62 注销待审核
     * 63 测试转上线待审核
     * 64 新增审核不通过
     * 65 变更审核不通过
     * 66 注销审核不通过
     * 67 测试转上线审核不通过
     * 68 上线
     * 69 已注销
     * 70 测试
     * 71 下线
     */
    private Integer chatbotStatus;

    @TableField(updateStrategy = FieldStrategy.ALWAYS)
    private Integer actualState;

    private String failureReason;

    private Integer deleted;

    private Integer operatorCode;

    private String accessTagNo;

    private String address;//Chatbot详细地址
    private String serviceWebsite;//Chatbot官网(主页地址)
    private String callBackNumber;//Chatbot服务电话
    private String longitude;//经度
    private String latitude;//纬度
    private String serviceTerm;//服务条款

    @ApiModelProperty(value = "本地机器人Logo文件url", dataType = "String")
    private String localChatbotLogoUrl;

    @ApiModelProperty(value = "本地附件Url", dataType = "String")
    private String localChatbotFileUrl;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty("更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    // 蜂动新增
    private Integer chatbotFirstIndustryType;
    private Integer chatbotSecondIndustryType;
    private String backgroundImgUrl;
}
