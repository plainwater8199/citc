package com.citc.nce.auth.postpay.scheme.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.auth.constant.csp.common.CSPOperatorCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author jcrenc
 * @since 2024/2/28 16:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("postpay_scheme_config")
@ApiModel(value = "PostpaySchemeConfig", description = "后付费方案配置")
public class PostpaySchemeConfig implements Serializable {
    private static final long serialVersionUID = -3619003825249295242L;
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("方案id")
    private Long schemeId;

    @ApiModelProperty("运营商编码：1：联通，2：移动，3：电信")
    private CSPOperatorCodeEnum operator;

    @ApiModelProperty("文本消息单价")
    private BigDecimal textMessagePrice;

    @ApiModelProperty("富媒体消息单价")
    private BigDecimal richMessagePrice;

    @ApiModelProperty("会话单价")
    private BigDecimal conversionPrice;

    @ApiModelProperty("回落消息单价")
    private BigDecimal fallbackPrice;

}
