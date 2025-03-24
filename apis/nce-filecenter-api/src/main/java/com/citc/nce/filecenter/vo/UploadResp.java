package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UploadResp {

    //里面file_uuid
    @ApiModelProperty(value = "文件urlId", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String urlId;

    @ApiModelProperty(value = "文件格式", example = "mp3")
    private String fileFormat;

    @ApiModelProperty(value = "文件名称", example = "aa.mp3")
    private String fileName;

    @ApiModelProperty(value = "文件大小", example = "1.2M")
    private String fileSize;
    @ApiModelProperty(value = "文件大小")
    private Long fileLength;

    @ApiModelProperty(value = "文件时长", example = "1分20秒")
    private String fileDuration;

    @ApiModelProperty(value = "平台fileId", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileTid;

    @ApiModelProperty(value = "缩略图id", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;

    @ApiModelProperty(value = "账号已用数量", example = "10")
    private Integer fileCount;

    @ApiModelProperty(value = "账号最大容量", example = "1000")
    private Integer totalCount;

    @ApiModelProperty(value = "chatbotName", example = "中讯")
    private String chatbotName;

    @ApiModelProperty(value = "operator", example = "联通")
    private String operator;

    @ApiModelProperty
    private String accountId;

    @ApiModelProperty
    private String errorMsg;
}
