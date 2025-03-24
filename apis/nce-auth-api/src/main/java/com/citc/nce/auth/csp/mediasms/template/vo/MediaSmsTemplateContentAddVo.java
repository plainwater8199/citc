package com.citc.nce.auth.csp.mediasms.template.vo;

import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author jiancheng
 */
@Data
public class MediaSmsTemplateContentAddVo {
    /*模板内容类型,0:文本 1:图片 2:音频 3:视频*/
    @NotNull(message = "媒体类型不能为空")
    @ApiModelProperty("模板内容类型,0:文本 1:图片 2:音频 3:视频")
    private ContentMediaType mediaType;

    /*当type为文本时为文本内容,其他情况下是关联的file_uuid*/
    @NotNull(message = "媒体资源内容不能为空")
    @ApiModelProperty("当type为文本时为文本内容,其他情况下是关联的file_uuid")
    private String content;

    @ApiModelProperty("资源名称")
    @Size(max = 25,message = "资源名称最多25字符")
    private String name;

    @JsonIgnore
    private long size;
}
