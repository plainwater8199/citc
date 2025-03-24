package com.citc.nce.robot.vo;

import lombok.Data;

import java.util.Date;

@Data
public class RobotGroupSendPlansReq {

    /**
     * 计划名称
     */
    private String planName;
    /**
     * 计划描述
     */
    private String planDuration;
    /**
     * 0待启动，1执行中，2执行完毕，3已暂停，4执行失败，5已关闭
     */
    private Integer planStatus;
    /**
     * 消息账号,多个账号用|分离
     */
    private String planAccount;
    /**
     * 是否删除,0 未删除  1 已删除
     */
    private Integer deleted;
    /**
     * 删除时间
     */
    private Date deleteTime;

    private Integer isStart;

    private Date startTime;

    /**
     * 富媒体账号,多个账号用,分离
     */
    private String richMediaIds;

    /**
     * 短信账号,多个账号用,分离
     */
    private String shortMsgIds;
}
