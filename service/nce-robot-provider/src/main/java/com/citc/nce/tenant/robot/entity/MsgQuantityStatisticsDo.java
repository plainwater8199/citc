package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("msg_quantity_statistics")
public class MsgQuantityStatisticsDo extends BaseDo<MsgQuantityStatisticsDo> implements Serializable {

    /**
     * 未知数量
     */
    private Long unknowNum;

    /**
     * 成功数量
     */
    private Long successNum;

    /**
     * 失败数量
     */
    private Long failedNum;

    /**
     * 已阅数量
     */
    private Long readNum;


    /**
     * 发送数量
     */
    private Long sendNum;
    /**
     * 运营商
     */
    private String operatorCode;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 计划节点id
     */
    private Long planDetailId;

    /**
     * 来源 1 群发 2 机器人 3测试发送
     */
    private Integer messageResource;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 账号类型 账号类型：1-5G消息、2-视频短信消息、3-短信消息
     */
    private Integer accountType;

    /**
     * 账号id
     */
    private String accountId;

    /**
     * 渠道信息
     */
    private String accountDictCode;





}
