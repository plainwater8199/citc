package com.citc.nce.robot.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class MaterialSubmitReq {

    @JSONField(serialize = false)
    @NotNull
    @ApiModelProperty(value = "文件",example = "aa.mp4")
    private MultipartFile file;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "视频缩略图",example = "aa.png")
    private MultipartFile thumbnail;

    @ApiModelProperty(value = "运营商字符串",example = "[\"联通\",\"硬核桃\"]")
    private String list;

    @ApiModelProperty(value = "用户信息",example = "aaaaa")
    private String creator;

    @ApiModelProperty(value = "redisKey",example = "aaaaa")
    private String redisKey;
}
