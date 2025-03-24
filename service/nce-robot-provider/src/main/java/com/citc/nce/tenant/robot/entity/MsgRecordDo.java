package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("msg_record")
@EqualsAndHashCode(callSuper = true)
public class MsgRecordDo extends BaseDo<MsgRecordDo> implements Serializable {

    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 发送结果
     */
    private Integer sendResult;

    /**
     * 消息id
     */
    private String messageId;

    /**
     * 主叫账号
     */
    private String callerAccount;

    /**
     * 运营商编码： 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    private Integer operatorCode;

    /**
     * 来源 1 群发 2 机器人 3测试发送 4开发者服务
     */
    private Integer messageResource;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 按钮内容
     */
    private String buttonContent;

    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 签名
     */
    private String sign;


    /**
     * 节点Id
     */
    private Long planDetailId;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 回执时间
     */
    private Date receiptTime;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 最终结果
     */
    private Integer finalResult;

    /**
     * 阅读时间
     */
    private Date readTime;

    /**
     * 账号类型  账号类型：1-5G消息、2-视频短信消息、3-短信消息
     */
    private Integer accountType;

    /**
     * 账号ID
     */
    private String accountId;

    /**
     * 账号名称
     */
    private String accountName;

    /**
     * 账号通道编码
     */
    private String accountDictCode;

    //会话id
    private String conversationId;

    //失败原因
    @TableField("failed_reason")
    private String failedReason;

    // 消费种类   1 充值  2 套餐
    private Integer consumeCategory;
}
