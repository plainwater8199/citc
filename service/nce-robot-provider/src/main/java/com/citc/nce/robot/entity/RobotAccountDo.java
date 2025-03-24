package com.citc.nce.robot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 14:14
 * @Version: 1.0
 * @Description:
 */
@Data
@TableName("robot_account")
public class RobotAccountDo extends BaseDo {

    /**
     * 5g消息账户
     */
    @TableField(value ="account")
    private String account;

    /**
     * 5g消息账户id
     */
    @TableField(value ="chatbot_account_id")
    private String chatbotAccountId;

    @ApiModelProperty("机器人名称")
    private String accountName;


    /**
     * 0 硬核桃 1联调 2移动
     */
    @TableField(value ="channel_type")
    private int channelType;

    /**
     * 手机号
     */
    @TableField(value ="mobile_num")
    private String mobileNum;

    /**
     * 会话id
     */
    @TableField(value ="conversation_id")
    private String conversationId;
    

    /**
     * 0未删除  1已删除
     */
    @TableField(value ="deleted")
    private int deleted;

    /**
     * 删除时间
     */
    @TableField(value ="deleted_time")
    private Date deletedTime;
}
