package com.citc.nce.developer.vo;

import com.citc.nce.auth.messagetemplate.vo.MessageTemplateResp;
import com.citc.nce.auth.readingLetter.template.vo.ReadingLetterTemplateVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author ping chen
 */
@Data
public class Developer5gApplicationVo {

    @ApiModelProperty("5G开发者信息表主键Id")
    @NotBlank(message = "5G开发者信息表主键Id不能为空")
    private String developerCustomerUniqueId;

    @ApiModelProperty("唯一标识")
    private String appId;

    @ApiModelProperty("秘钥")
    private String appSecret;

    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String applicationName;

    @ApiModelProperty("描述")
    private String applicationDescribe;

    @ApiModelProperty("状态，0:启用，1:禁用，2:模板检查中，3:模板异常")
    private Integer applicationState;

    @ApiModelProperty("公钥")
    private String appKey;

    @ApiModelProperty("模板类型，1:普通模板，2:个性模板")
    @NotNull(message = "模板类型不能为空")
    private Integer templateType;

    @ApiModelProperty("模板Id")
    @NotNull(message = "模板Id不能为空")
    private Long templateId;

    @ApiModelProperty("模板名称")
    private String templateName;

    @ApiModelProperty("模板状态:2:模板检查中，3:模板异常,4:审核成功")
    private Integer templateState;

    @ApiModelProperty("chabot账号Id,多个逗号分隔")
    @NotBlank(message = "chabot账号Id不能为空")
    private String chatbotAccountId;

    @ApiModelProperty("chabot账号名称,多个逗号分隔")
    private String chatbotAccountName;

    @ApiModelProperty("删除时间")
    private Date deleteTime;

    @ApiModelProperty("唯一键")
    private String uniqueId;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("回调地址")
    private String callbackUrl;

    @ApiModelProperty("企业名称")
    private String enterpriseAccountName;

    @ApiModelProperty("是否允许回落( 0 不允许, 1允许)")
    @NotNull(message = "是否允许回落不能为空")
    private Integer allowFallback;
    @ApiModelProperty("回落类型 1:短信 ,  2:5g阅信")

    private Integer fallbackType;
    @ApiModelProperty("回落短信内容(allowFallback = 1时, 必填)")
    private String fallbackSmsContent;
    @ApiModelProperty("允许回落阅读信模板id(fallbackType = 2时, 必填)")
    private Long fallbackReadingLetterTemplateId;
    //阅信模板详情
    private ReadingLetterTemplateVo fallbackReadingLetterTemplate;
    //5G消息模板详情
    private MessageTemplateResp messageTemplate;
}
