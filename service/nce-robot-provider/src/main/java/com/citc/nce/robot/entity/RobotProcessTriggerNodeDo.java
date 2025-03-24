package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/8 16:18
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_process_trigger_node")
@Accessors(chain = true)
public class RobotProcessTriggerNodeDo extends BaseDo<RobotProcessTriggerNodeDo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
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

}
