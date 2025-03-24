package com.citc.nce.auth.accountmanagement.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class AccountManagementResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Id", dataType = "Integer", required = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 我们平台激励账号
     */
    @ApiModelProperty(value = "账号ID(唯一标识)")
    private String chatbotAccountId;

    /**
     * 账户类型，1联通2硬核桃
     * 2023/2/20 注意：数据库存的名字，不是数字类型
     */
    @ApiModelProperty(value = "账户类型，1联通2硬核桃")
    private String accountType;

    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    @ApiModelProperty(value = "归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer accountTypeCode;

    /**
     * 机器人状态( CSPChatbotStatusEnum  用这个枚举  硬核桃和联通电信一样)
     */
    @ApiModelProperty(value = "机器人状态")
    private Integer chatbotStatus;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    private String accountName;

    /**
     * 运营商机器人账号 账号id
     */
    @ApiModelProperty(value = "账号id")
    private String chatbotAccount;

    /**
     * appid
     */
    @ApiModelProperty(value = "appid")
    private String appId;

    /**
     * appkey
     */
    @ApiModelProperty(value = "appkey")
    private String appKey;

    /**
     * token
     */
    @ApiModelProperty(value = "token")
    private String token;

    /**
     * 消息地址
     */
    @ApiModelProperty(value = "消息地址")
    private String messageAddress;

    /**
     * 文件地址
     */
    @ApiModelProperty(value = "文件地址")
    private String fileAddress;

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
     * 更新时间
     */
    @ApiModelProperty("是否删除")
    private int deleted;

    @ApiModelProperty(value = "归属客户id", dataType = "int")
    private String customerId;

    private Integer menuStatus;

    private String result;

    private Integer isAddOther;

    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @ApiModelProperty("归属客户名称")
    private String enterpriseAccountName;
    @ApiModelProperty("chatbot服务提供商tag  蜂动: fontdo   自有chatbot: owner")
    private String supplierTag;
    @ApiModelProperty("chatbot服务提供代理商id")
    private String agentId;
    @ApiModelProperty("chatbot服务提供租户id")
    private String ecId;
}
