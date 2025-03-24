package com.citc.nce.common.vo.tempStore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bydud
 * 2024/2/19
 **/

@Data
@Accessors(chain = true)
public class Csp4CustomerAudio {

    @JsonSerialize(using = ToStringSerializer.class)
    //保存素材到客户侧后产生的id
    private Long newId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long audioId;

//    @ApiModelProperty("持续时间")
    private String duration;

//    @ApiModelProperty("名称")
    private String name;

//    @ApiModelProperty("音频文件")
    private String fileId;

//    @ApiModelProperty("大小")
    private String size;

//    @ApiModelProperty("文件类型")
    private String format;

}
