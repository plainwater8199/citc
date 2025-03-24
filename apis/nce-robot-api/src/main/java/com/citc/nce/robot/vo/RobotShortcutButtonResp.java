package com.citc.nce.robot.vo;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/11/11 11:45
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotShortcutButtonResp implements Serializable {
    /**
     * 流程id
     */
    private Long id;

    /**
     * 按钮id
     */
    private String uuid;

    /**
     * 流程id
     */
    private Long processId;

    /**
     * 0
     * 回复按钮1
     * 跳转 按钮2
     * 打开app3
     * 打 电话4
     * 发送地址5
     */
    private int buttonType;

    /**
     * 回复内容
     */
    private String buttonDetail;

    /**
     * 创建者
     */
    private String creator;


    /**
     * 0未删除  1已删除
     */
    private int deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

}
