package com.citc.nce.robot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/11 20:13
 */
@Data
public class RobotCustomCommandDataParam {

    @ApiModelProperty(value = "文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String urlId;

    @ApiModelProperty(value = "文件格式",example = "mp3")
    private String fileFormat;

    @ApiModelProperty(value = "文件名称",example = "aa.mp3")
    private String fileName;

    @ApiModelProperty(value = "文件大小",example = "1.2M")
    private String fileSize;

    @ApiModelProperty(value = "文件时长",example = "1分20秒")
    private String fileDuration;

    @ApiModelProperty(value = "平台fileId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileTid;

    @ApiModelProperty(value = "缩略图id",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;

    @ApiModelProperty(value = "operator",example = "联通")
    @NotNull
    private String operator;

    @ApiModelProperty(value = "useable",example = "素材是否可用 1 待审核，2审核通过，3 审核失败，4 无状态")
    private Integer useable;

    @ApiModelProperty(value = "音频存储urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String audioUrlId;

    @ApiModelProperty(value = "视频urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String videoUrlId;

    @ApiModelProperty(value = "封面urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverId;

    @ApiModelProperty(value = "主封面背景",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String mainCoverBackId;
}
