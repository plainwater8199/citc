package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 16:18
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_process_trigger_node")
public class RobotProcessTriggerNodeDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 场景id
     */
    private Long sceneId;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 关键字json集合
     */
    private String primaryCodeList;

    /**
     * 正则词
     */
    private String regularCode;


    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deletedTime;


    private String creatorOld;

    private String updaterOld;
}
