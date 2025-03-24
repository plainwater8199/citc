package com.citc.nce.robot.vo;

import com.citc.nce.robot.common.ResponsePriority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class RobotBaseSettingVo {
    public static final RobotBaseSettingVo DEFAULT_SETTING;

    static {
        RobotBaseSettingVo setting = new RobotBaseSettingVo();
        setting.setTimeout(5);
        setting.setResponsePriorities(Arrays.asList(ResponsePriority.CHATBOT, ResponsePriority.KEYWORDS, ResponsePriority.DEFAULT));
        setting.setGlobalButtonShowStrategy(0); //仅在默认回复中显示
        DEFAULT_SETTING = setting;
    }

    @NotNull
    private Long id;

    @ApiModelProperty("机器人超时等待时间（分钟）")
    @NotNull
    @Positive
    private Integer timeout;

    @NotEmpty
    @ApiModelProperty("响应优先级")
    private List<ResponsePriority> responsePriorities;

    @NotNull
    private Integer globalButtonShowStrategy;
    private List<RobotProcessButtonResp> globalButtonList;

}
