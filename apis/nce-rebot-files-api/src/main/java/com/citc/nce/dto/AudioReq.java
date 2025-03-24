package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AudioReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -4241224463079481930L;
    
    @NotNull
    @ApiModelProperty(value = "音频名称",example = "aa.mp3")
    private String audioName;

    @NotNull
    @ApiModelProperty(value = "音频时长",example = "1分20秒")
    private String audioDuration;

    @NotNull
    @ApiModelProperty(value = "音频大小",example = "1.2M")
    private String audioSize;

    @NotNull
    @ApiModelProperty(value = "音频存储urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String audioUrlId;

}
