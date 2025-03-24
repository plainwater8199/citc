package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 16:50
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_process_button")
public class RobotProcessButtonDo extends BaseDo {

    private static final long serialVersionUID = 1L;



    /**
     * 场景管理中，该值非空，机器设置该值为空
     */
    private Long processId;

    /**
     * 节点id
     */
    private Long nodeId;

    /**
     * 0回复按钮1跳转 按钮2打开app3打 电话4发送地址5
     */
    private int buttonType;

    /**
     * 回复内容
     */
    private String buttonDetail;

    /**
     * 按钮id
     */
    private String uuid;

//    /**
//     * 创建者
//     */
//
//    private String creator;
//
//    /**
//     * 创建时间
//     */
//    private Date createTime;
//
//    /**
//     * 更新者
//     */
//    private String updater;
//
//    /**
//     * 更新时间
//     */
//    private Date updateTime;

    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    public RobotProcessButtonDo() {
    }

    private String creatorOld;

    private String updaterOld;

}
