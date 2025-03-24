package com.citc.nce.robot.api.tempStore.bean.video;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author bydud
 * @since 15:07
 */
@Data
@ApiModel(value = "ResourcesVideo对象add", description = "扩展商城-资源管理-视频")
public class VideoAdd {

    @ApiModelProperty("封面")
    private String mainCoverId;

    @ApiModelProperty("持续时间")
    private String duration;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("文件id")
    private String fileId;


    @ApiModelProperty("大小")
    private String size;

    @ApiModelProperty("文件类型")
    private String format;

    private Map<?,?> cropObj;

    private List<String> covers;
}
