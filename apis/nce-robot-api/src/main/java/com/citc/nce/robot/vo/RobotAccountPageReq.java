package com.citc.nce.robot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotAccountPageReq extends PageParam  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 5g消息账户
     */
    @ApiModelProperty(value = "5g消息账户")
    private String account;

    /**
     * 5g消息账户id
     */
    @ApiModelProperty(value = "5g消息账户id")
    private String chatbotAccountId;

    /**
     * 0 硬核桃 1联调 2移动
     */
    @ApiModelProperty(value = "0 硬核桃 1联调 2移动")
    private Integer channelType;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobileNum;

    @ApiModelProperty(value = "会话id")
    private String conversationId;
    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "创建人")
    private String create;
}
