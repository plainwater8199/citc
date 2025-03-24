package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/11/11 10:55
 * @Version: 1.0
 * @Description: 流程图快捷回复按钮
 */
@Data
@TableName("robot_shortcut_button")
public class RobotShortcutButtonDo extends BaseDo {

    /**
     * 按钮id
     */
    @TableField(value = "uuid")
    private String uuid;

    /**
     * 流程id
     */
    @TableField(value = "process_id")
    private Long processId;

    /**
     * 0回复按钮1跳转 按钮2打开app3打 电话4发送地址5
     */
    @TableField(value = "button_type")
    private int buttonType;

    /**
     * 回复内容
     */
    @TableField(value = "button_detail")
    private String buttonDetail;

    /**
     * 0未删除  1已删除
     */
    @TableField(value = "deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value = "delete_time")
    private Date deleteTime;

    private String creatorOld;

    private String updaterOld;

}
