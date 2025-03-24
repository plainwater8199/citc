package com.citc.nce.robot.vo;

import com.citc.nce.robot.common.ResponsePriority;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: yangchuang
 * @Date: 2022/7/5 14:31
 * @Version: 1.0
 * @Description:
 */
@Data
public class RebotSettingReq implements Serializable {


    /**
     * 等待时间
     */
    @ApiModelProperty("等待时间")
    @NotNull(message = "等待时间不能为空")
    private Integer waitTime;

    /**
     * 兜底回复类型 0代表发送全部 1代表随机发送一条
     */
    @ApiModelProperty("兜底回复类型 0代表发送全部 1代表随机发送一条")
    private int lastReplyType;

    /**
     * 兜底回复
     */
    @ApiModelProperty("兜底回复")
    private String lastReply;

    /**
     * 全局按钮类型0代表仅在兜底回复显示1代表在机器人所有回复显示
     */
    @ApiModelProperty("全局按钮类型0代表仅在兜底回复显示1代表在机器人所有回复显示")
    @NotNull(message = "全局按钮类型不能为空")
    private int globalType;

    @ApiModelProperty("按钮集合")
    @Valid
    private List<RobotProcessButtonReq> buttonList = new ArrayList<RobotProcessButtonReq>();

    /**
     * 创建人
     */
    private String create;

    /**
     * 回复开关 0:关闭 1:开启
     */
    @ApiModelProperty(value = "回复开关 0:关闭 1:开启")
    private int replySwitch;

    /**
     * 回复类型 0:自定义回复 1:大型回复
     */
    @ApiModelProperty(value = "回复类型 0:自定义回复 1:大型回复")
    private int replyType;
    /**
     * 选择大模型，选项之间以,分隔
     */
    @ApiModelProperty(value = "选择大模型，选项之间以,分隔")
    private String module;
    /**
     * 回复方式 0:流式 1:非流式
     */
    @ApiModelProperty(value = "回复方式 0:流式 1:非流式")
    private int replyMethod;
    /**
     * api
     */
    @ApiModelProperty(value = "apiKey")
    @Length(max = 50, message = "apiKey长度超过限制")
    private String apiKey;
    /**
     * secret
     */
    @ApiModelProperty(value = "secretKey")
    @Length(max = 50, message = "secretKey长度超过限制")
    private String secretKey;
    /**
     * 服务地址
     */
    @ApiModelProperty(value = "服务地址")
    @Length(max = 500, message = "服务地址长度超过限制")
    private String serviceAddress;

    @ApiModelProperty(value = "回复优先级排序")
    @NotNull(message = "回复优先级排序不能为空")
    private List<ResponsePriority> responsePriorities;

}
