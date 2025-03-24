package com.citc.nce.robot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/10/16 10:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class RobotRecordPageReq extends PageParam  implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 会话id
     */
    @ApiModelProperty(value = "会话id")
    private String conversationId;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String mobileNum;

    /**
     * 5g账号
     */
    @ApiModelProperty(value = "5g账号")
    private String account;

    /**
     * 5g账号id
     */
    @ApiModelProperty(value = "5g账号id")
    private String chatbotAccountId;

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

    /**
     * 用户ids
     */
    @ApiModelProperty(value = "用户ids")
    private List<String> userIdList;

}
