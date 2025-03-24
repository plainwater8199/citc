package com.citc.nce.im.mall.process.entity;

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
@TableName("mall_robot_process_trigger")
public class MallRobotProcessTriggerDo extends BaseDo {

    private static final long serialVersionUID = 1L;

    /**
     * 模板uuid
     */
    private String templateId;

    /**
     * 流程uuid
     */
    private String processId;

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
