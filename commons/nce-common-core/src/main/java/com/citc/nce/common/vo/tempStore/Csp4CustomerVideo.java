package com.citc.nce.common.vo.tempStore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * bydud
 * 2024/2/19
 **/
@Data
@Accessors(chain = true)
public class Csp4CustomerVideo {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long newId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long videoId;

    //@ApiModelProperty("封面")
    private String cover;

    //@ApiModelProperty("持续时间")
    private String duration;

    //@ApiModelProperty("名称")
    private String name;

    //@ApiModelProperty("视频文件")
    private String fileId;

    //@ApiModelProperty("大小")
    private String size;

    //@ApiModelProperty("文件类型")
    private String format;

    private String cropObj;

    //@ApiModelProperty("视频封面推荐图")
    private List<String> covers;
}
