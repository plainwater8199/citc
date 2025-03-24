package com.citc.nce.robot.api.tempStore.bean.audio;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 扩展商城-资源管理-audio
 * </p>
 *
 * @author bydud
 * @since 2023-11-29 03:11:48
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ts_resources_audio")
@ApiModel(value = "资源管理-audio-添加参数", description = "扩展商城-资源管理-audio")
public class AudioAdd implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("持续时间")
    @NotBlank(message = "持续时间不能为空")
    private String duration;

    @ApiModelProperty("名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    @ApiModelProperty("名称")
    @NotBlank(message = "文件对应的uuid")
    private String fileId;

    @ApiModelProperty("大小")
    @NotBlank(message = "大小不能为空")
    private String size;

    @ApiModelProperty("文件类型")
    @NotBlank(message = "文件类型不能为空")
    private String format;

}
