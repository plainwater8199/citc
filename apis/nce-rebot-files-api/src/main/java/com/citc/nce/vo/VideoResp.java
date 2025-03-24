package com.citc.nce.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class VideoResp implements Serializable {

    private static final long serialVersionUID = -4539614053016786214L;

    @ApiModelProperty(value = "视频id",example = "2")
    private Long id;

    @ApiModelProperty(value = "视频名称",example = "a.mp4")
    private String videoName;

    @ApiModelProperty(value = "视频时长",example = "1分20秒")
    private String videoDuration;

    @ApiModelProperty(value = "视频大小",example = "2M")
    private String videoSize;

    @ApiModelProperty(value = "视频urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String videoUrlId;

    @ApiModelProperty(value = "封面urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverId;

    @ApiModelProperty(value = "封面图base64")
    private String mainCoverThumbnail;

    @ApiModelProperty(value = "主封面urlIds",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> covers;

    @ApiModelProperty(value = "封面地址集合",example = "2b34ef74fe564d8d9790ac239197b4b9，2b34ef74fe564d8d9790ac239197b4b9")
    private Date videoUploadTime;

    @ApiModelProperty(value = "账户集合")
    private List<AccountResp> accounts;

    @ApiModelProperty(value = "是否被引用")
    private Integer used;

    @ApiModelProperty(value = "主封面背景",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverBackId;

    private JSONObject cropObj;

    @ApiModelProperty(value = "缩略图",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;
}
