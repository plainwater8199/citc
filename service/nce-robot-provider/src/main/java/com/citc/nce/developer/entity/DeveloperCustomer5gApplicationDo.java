package com.citc.nce.developer.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("developer_customer_5g_application")
@Accessors(chain = true)
public class DeveloperCustomer5gApplicationDo extends BaseDo<DeveloperCustomer5gApplicationDo> {
    private static final long serialVersionUID = 4265349361741658723L;

    @ApiModelProperty("5G开发者信息表主键Id")
    private String developerCustomerUniqueId;

    @ApiModelProperty("唯一标识")
    private String appId;

    @ApiModelProperty("秘钥")
    private String appSecret;

    @ApiModelProperty("名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String applicationDescribe;

    @ApiModelProperty("应用状态，0:启用，1:禁用")
    private Integer applicationState;

    @ApiModelProperty("模板状态:2:模板检查中，3:模板异常,4:审核成功")
    private Integer applicationTemplateState;

    @ApiModelProperty("公钥")
    private String appKey;

    @ApiModelProperty("模板类型，1:普通模板，2:个性模板")
    private Integer templateType;

    @ApiModelProperty("模板Id")
    private Long templateId;

    @ApiModelProperty("chabot账号Id,多个逗号分隔")
    private String chatbotAccountId;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "null", delval = "now()")
    private Date deleteTime;

    @ApiModelProperty("唯一键")
    private String uniqueId;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("csp账号")
    private String cspId;

    @ApiModelProperty("企业Id")
    private Long enterpriseId;

    @ApiModelProperty("是否允许回落( 0 不允许, 1允许)")
    private int allowFallback;
    @ApiModelProperty("回落类型 1:短信 ,  2:5g阅信")

    private Integer fallbackType;
    @ApiModelProperty("回落短信内容(allowFallback = 1时, 必填)")
    private String fallbackSmsContent;
    @ApiModelProperty("回落阅读信模板id(fallbackType = 2时, 必填)")
    @TableField(value = "fallback_reading_letter_template_id", updateStrategy = FieldStrategy.IGNORED)
    private Long fallbackReadingLetterTemplateId;
}
