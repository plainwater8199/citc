package com.citc.nce.tenant.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

//记录发送了哪些下行消息(机器人和群发都是此)
@Data
@Accessors(chain = true)
public class MsgRecordVo {
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    /**
     * 发送结果
     */
    @ApiModelProperty(value = "发送结果")
    private Integer sendResult;

    /**
     * 消息id
     */
    @ApiModelProperty(value = "消息id")
    private String messageId;

    /**
     * 主叫账号
     */
    @ApiModelProperty(value = "主叫账号")
    private String callerAccount;

    /**
     * 主叫账号
     */
    @ApiModelProperty(value = "运营商编码： 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    /**
     * 来源 1 群发 2 机器人
     */
    @ApiModelProperty(value = "来源 1 群发 2 机器人 3测试发送 4开发者服务发送 5 组件  6 快捷群发  7 关键字发送")
    private Integer messageResource;

    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private Integer messageType;

    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String messageContent;
//    /**
//     * 按钮code
//     */
//    @ApiModelProperty(value = "按钮code")
//    private String buttonCode;

    /**
     * 按钮内容
     */
    @ApiModelProperty(value = "按钮内容")
    private String buttonContent;

    /**
     * 模板id
     */
    @ApiModelProperty(value = "模板id")
    private Long templateId;

    /**
     * 签名
     */
    @ApiModelProperty(value = "签名")
    private String sign;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    private String templateName;

    /**
     * 节点Id
     */
    @ApiModelProperty(value = "节点Id")
    private Long planDetailId;

    /**
     * 计划id
     */
    @ApiModelProperty(value = "计划id")
    private Long planId;

    /**
     * 回执时间
     */
    @ApiModelProperty(value = "回执时间")
    private Date receiptTime;

    /**
     * 发送时间
     */
    @ApiModelProperty(value = "发送时间")
    private Date sendTime;

    /**
     * 最终结果
     */
    @ApiModelProperty(value = "最终结果")
    private Integer finalResult;

    /**
     * 阅读时间
     */
    @ApiModelProperty(value = "阅读时间")
    private Date readTime;

    /**
     * 账号类型  账号类型：1-5G消息、2-视频短信消息、3-短信消息
     */
    @ApiModelProperty(value = "账号类型  账号类型：1-5G消息、2-视频短信消息、3-短信消息")
    private Integer accountType;

    /**
     * 账号ID
     */
    @ApiModelProperty(value = "账号ID")
    private String accountId;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "账号名称")
    private String accountName;

    /**
     * 账号通道编码
     */
    @ApiModelProperty(value = "账号通道编码")
    private String accountDictCode;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private Date updateTime;
    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    @NotNull
    private String creator;
    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private String updater;

    @ApiModelProperty("会话消息id")
    private String conversationId;

    @ApiModelProperty("失败原因")
    private String failedReason;

    // 消费种类   1 充值  2 套餐
    private Integer consumeCategory;
}
