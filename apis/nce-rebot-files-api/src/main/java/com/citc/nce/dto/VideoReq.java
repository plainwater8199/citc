package com.citc.nce.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: VideoDto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -7806901278514043345L;

    @ApiModelProperty(value = "视频id",example = "2")
    private Long id;

    @NotNull
    @ApiModelProperty(value = "视频名称",example = "a.mp4")
    private String videoName;

    @NotNull
    @ApiModelProperty(value = "视频时长",example = "1分20秒")
    private String videoDuration;

    @NotNull
    @ApiModelProperty(value = "视频大小",example = "2M")
    private String videoSize;

    @NotNull
    @ApiModelProperty(value = "视频urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String videoUrlId;

    @ApiModelProperty(value = "主封面urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverId;

    @NotNull
    @ApiModelProperty(value = "视频格式",example = "Mp4")
    private String videoFormat;

    @ApiModelProperty(value = "封面地址集合",example = "2b34ef74fe564d8d9790ac239197b4b9，2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> covers;

    @ApiModelProperty(value = "主封面背景",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverBackId;

    @ApiModelProperty(value = "缩略图id",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;

    private JSONObject cropObj;
}
