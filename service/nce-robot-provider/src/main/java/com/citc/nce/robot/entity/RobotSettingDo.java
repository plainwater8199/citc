package com.citc.nce.robot.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 15:11
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_setting")
public class RobotSettingDo extends BaseDo  {

    private static final long serialVersionUID = 1L;


    /**
     * 机器人编号
     */
    private Long robotId;

    /**
     * 等待时间
     */
    private Integer waitTime;

    /**
     * 兜底回复类型 0代表发送全部 1代表随机发送一条
     */
    private int lastReplyType;

    /**
     * 兜底回复
     */
    private String lastReply;

    /**
     * 兜底回复的模板ID(supplier方式)
     */
    private String templateId;


    /**
     * 全局按钮类型0代表仅在兜底回复显示1代表在机器人所有回复显示
     */
    private int globalType;

    /**
     * 回复开关 0:关闭 1:开启
     */
    private int replySwitch;

    /**
     * 回复类型 0:自定义回复 1:大型回复
     */
    private int replyType;

    /**
     * 选择大模型，多选项以,分隔
     */
    private String module;

    /**
     * 回复方式 0:流式 1:非流式
     */
    private Integer replyMethod;

    /**
     * api
     */
    private String apiKey;
    /**
     * secret
     */
    private String secretKey;
    /**
     * 服务地址
     */
    private String serviceAddress;


    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;


    private String priority;

    private String responsePriorities;

    public RobotSettingDo() {
    }

}
