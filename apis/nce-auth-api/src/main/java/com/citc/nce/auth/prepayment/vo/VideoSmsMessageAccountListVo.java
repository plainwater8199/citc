package com.citc.nce.auth.prepayment.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author jiancheng
 */
@Data
@ApiModel("视频短信消息账号列表对象")
public class VideoSmsMessageAccountListVo extends BaseMessageAccountListVo {
    @ApiModelProperty("账户id")
    private String accountId;

    @ApiModelProperty("通道")
    private String dictCode;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("归属客户名称")
    private String enterpriseAccountName;

    @ApiModelProperty("签名")
    private List<String> signatureList;

    @ApiModelProperty("充值总数")
    private Long totalLimit;

    @ApiModelProperty("剩余总数")
    private Long totalUsable;

    @ApiModelProperty("资费定价")
    private String tariffContent;

    @ApiModelProperty("客户id")
    private String customerId;
}
