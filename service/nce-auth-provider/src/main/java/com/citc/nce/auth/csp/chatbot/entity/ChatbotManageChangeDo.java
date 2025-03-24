package com.citc.nce.auth.csp.chatbot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 机器人变更信息表
 *
 * @Author zy.qiuø
 * @CreatedTime 2023/2/7 15:10
 */
@Data
@TableName("chatbot_manage_cmcc_change")
@Accessors(chain = true)
public class ChatbotManageChangeDo extends BaseDo<ChatbotManageChangeDo> {

    private static final long serialVersionUID = -6538434851921494999L;

    @TableId(type = IdType.ASSIGN_ID)
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

    private String chatbotIndustryType;

    private String chatbotSmsPort;

    private String chatbotSmsSign;

    private String chatbotServiceDesc;

    private String chatbotCallBackNumber;

    private String chatbotMail;

    private String chatbotHomepage;

    private Integer chatbotBubbleColor;

    @TableField("chatbot_bubble_color_rgb")
    private String chatbotBubbleColorRGB;

    @TableField("chatbot_ISP_is_display")
    private Integer chatbotISPIsDisplay;

    private String chatbotFileUrl;

    private String annexName;
    private Integer chatbotStatus;

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

    private String localChatbotLogoUrl;

    private String localChatbotFileUrl;
}
