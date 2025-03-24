package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/7 16:17
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_process_setting_node")
public class RobotProcessSettingNodeDo extends BaseDo {
    private static final long serialVersionUID = 1L;


    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 流程描述
     */
    private String processValue;

    /**
     * 0放开1关闭，默认0
     */
    private int derail;

    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deletedTime;

    /**
     * 最近修改
     */
    private Date modifiedTime;

    /**
     * 最新发布
     */
    private Date releaseTime;

    private String creatorOld;

    private String updaterOld;

}
