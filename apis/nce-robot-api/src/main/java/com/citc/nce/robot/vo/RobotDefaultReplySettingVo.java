package com.citc.nce.robot.vo;

import com.citc.nce.robot.common.ResponsePriority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/30 10:38
 */
@Data
@ApiModel
@Accessors(chain = true)
public class RobotDefaultReplySettingVo {
    public final static RobotDefaultReplySettingVo DEFAULT_REPLY_SETTING;

    static {
        DEFAULT_REPLY_SETTING = new RobotDefaultReplySettingVo();
        DEFAULT_REPLY_SETTING.setReplySwitch(false);
    }

    @NotNull
    private Long id;

    @NotNull
    @ApiModelProperty("是否开启默认回复")
    private Boolean replySwitch;

    @ApiModelProperty("回复类型 0:自定义回复 1:大型回复")
    private Integer replyType;

    private String lastReply;

    /**
     * 选择大模型，多选项以,分隔
     */
    private String module;

    /**
     * 回复方式 0:流式 1:非流式
     */
    private Integer replyMethod;

    /**
     * api
     */
    private String apiKey;
    /**
     * secret
     */
    private String secretKey;
    /**
     * 服务地址
     */
    private String serviceAddress;

}
