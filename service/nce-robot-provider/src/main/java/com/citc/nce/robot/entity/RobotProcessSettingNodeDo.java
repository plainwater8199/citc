package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 流程定义表主表
 * @Author: yangchuang
 * @Date: 2022/7/7 16:17
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_process_setting_node")
@Accessors(chain = true)
public class RobotProcessSettingNodeDo extends BaseDo<RobotProcessSettingNodeDo> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
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
     *开关 0放开1关闭，默认0
     */
    private int derail;

    /**
     * 状态   1已发布 -1 未发布，默认-1  ；0 发布中，2 发布失败 3 更新未发布
     */
    private int status;

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
    /**
     * 流程包含模板的素材关联关联账号
     */
    private String accounts;
    /**
     * 关联账号修改状态 0 原始状态或发布前修改，或修改后已发布    1发布后再次修改过账号
     */
    private int  sceneAccountChangeStatus;

    /**
     * 已经关联的账号，流程审核通过时或发送审核时场景关联的账号
     */
    private String relatedSceneAccounts;

    /**
     * 审核结果
     */
    private  String auditResult;
}
