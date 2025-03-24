package com.citc.nce.auth.csp.chatbot.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Id", dataType = "Integer", required = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "移动Id", dataType = "Integer", required = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cmccId;

    @ApiModelProperty("chatbot名称")
    private String chatbotName;

    @ApiModelProperty("chatbotAccountId")
    private String chatbotAccountId;

    @ApiModelProperty("chatbotId")
    private String chatbotAccount;

    @ApiModelProperty("归属客户id")
    private String customerId;

    @ApiModelProperty("企业名称")
    private String enterpriseName;
    /**
     * 企业账户名称
     */
    @ApiModelProperty(value = "企业账户名称", dataType = "String")
    private String enterpriseAccountName;

    @ApiModelProperty("归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("移动chatbot状态 11：新增审核不通过，12：变更审核不通过，20：管理平台新增审核中，21：管理平台变更审核中，24：上架审核中，25：上架审核不通过，26：调试白名单审核，27：调试白名单审核不通过，30：在线，31：已下线，40：暂停，41：黑名单，42：已下线（关联的CSP被下线），50：调试")
    private Integer chatbotStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("变更时间")
    private Date updateTime;

    @ApiModelProperty("白名单手机号(多个时以英文半角逗号,分隔)")
    private String whiteList;

    @ApiModelProperty("白名单审核状态 0：待审核，26：审核通过，27：审核不通过")
    private Integer whiteListStatus;

    @ApiModelProperty("上架审核状态 0：待审核，1：审核通过，2：审核不通过")
    private Integer shelvesStatus;

    @ApiModelProperty("菜单状态 0：待审核，1：审核通过，2：审核不通过")
    private Integer menuStatus;

    private String result;

    private Integer availableStatus;

    private String failureReason;

    private Integer isAddOther;

    private String supplierTag;

    private String tariffContent;

    private String packageContent;
}
