package com.citc.nce.auth.csp.mediasms.template.vo;

import com.citc.nce.auth.csp.mediasms.template.enums.ContentMediaType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jiancheng
 */
@Data
@Accessors(chain = true)
public class MediaSmsTemplateContentVo {

    @ApiModelProperty("id")
    private Long id;

    /*关联视频模板ID*/
    @ApiModelProperty("关联视频模板ID")
    private Long mediaTemplateId;

    /*模板内容类型,0:文本 1:图片 2:音频 3:视频*/
    @ApiModelProperty("模板内容类型")
    private ContentMediaType mediaType;

    /*文件类型*/
    private String fileType;

    /*当type为文本时为文本内容,其他情况下是关联的file_uuid*/
    @ApiModelProperty("内容")
    private String content;

    /*媒体资源所占字节数*/
    @ApiModelProperty("媒体资源所占字节数")
    private long size;

    /*媒体资源名称，文本内容可不传*/
    @ApiModelProperty("媒体资源名称")
    private String name;

    /*排序字段*/
    @ApiModelProperty("排序字段")
    private Integer sortNum;
}
