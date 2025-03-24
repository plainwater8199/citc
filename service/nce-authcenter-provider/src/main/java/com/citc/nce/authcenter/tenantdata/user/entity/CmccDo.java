package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("chatbot_manage_cmcc")
public class CmccDo extends BaseDo<CmccDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("账户管理Id")
    //private Long accountManagementId;  // TODO 废弃

    private String chatbotAccountId;

    @ApiModelProperty("chatbot名称")
    private String chatbotName;

    @ApiModelProperty("服务代码")
    private String chatbotServiceCode;

    @ApiModelProperty("服务扩展码")
    private String chatbotServiceExtraCode;

    @ApiModelProperty("机器人Logo文件url")
    private String chatbotLogoUrl;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("大区")
    private String region;

    @ApiModelProperty("chatbot类型")
    private String chatbotType;

    @ApiModelProperty("行业类型 1-党政军,2-民生，3-金融，4-物流，5-游戏，6-电商，7-微商（个人），8-沿街商铺（中小），9-企业（大型）,10-教育培训,11-房地产,12-医疗器械、药店")
    private String chatbotIndustryType;

    @ApiModelProperty("短信端口")
    private String chatbotSmsPort;

    @ApiModelProperty("短信签名")
    private String chatbotSmsSign;

    @ApiModelProperty("服务描述")
    private String chatbotServiceDesc;

    @ApiModelProperty("回叫号码")
    private String chatbotCallBackNumber;

    @ApiModelProperty("电子邮箱")
    private String chatbotMail;

    @ApiModelProperty("首页地址")
    private String chatbotHomepage;

    @ApiModelProperty("气泡颜色 默认0:跟随终端， 1:自定义")
    private String chatbotBubbleColor;

    @ApiModelProperty("气泡颜色RGB编码")
    @TableField("chatbot_bubble_color_rgb")
    private String chatbotBubbleColorRgb;

    @ApiModelProperty("chatbos提供者 0：不显示 1：显示")
    @TableField(value = "chatbot_ISP_is_display")
    private String chatbotISPIsDisplay;

    @ApiModelProperty("附件Url")
    private String chatbotFileUrl;

    @ApiModelProperty("移动chatbot状态 11：新增审核不通过，12：变更审核不通过，20：管理平台新增审核中，21：管理平台变更审核中，24：上架审核中，25：上架审核不通过，26：调试白名单审核，27：调试白名单审核不通过，30：在线，31：已下线，40：暂停，41：黑名单，42：已下线（关联的CSP被下线），50：调试")
    private String chatbotStatus;

    @ApiModelProperty("chatbotId")
    private String chatbotId; // TODO 废弃

    @ApiModelProperty("归属客户Id")
    //private String enterpriseId; // TODO 废弃

    private String customerId;

    @ApiModelProperty("客户名称")
    private String enterpriseName;

    private String creatorOld;

    private String updaterOld;
}
