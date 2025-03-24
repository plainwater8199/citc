package com.citc.nce.im.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("short_msg_quantity_statistics")
public class ShortMsgQuantityStatisticsDo extends BaseDo<ShortMsgQuantityStatisticsDo> implements Serializable {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 群发个数
     */
    private Long sendNum;

    /**
     * 运营商
     */
    private String shortMsgOperatorCode;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 计划节点id
     */
    private Long planDetailId;

    /**
     * 发送时间(年月日)
     */
    private String sendTimeDay;

    /**
     * 发送时间(小时)
     */
    private String sendTimeHour;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    private Date createTime;

    private Date updateTime;

    private String creator;

    private String updater;

    private String shortMsgAccountId;
}
