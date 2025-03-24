package com.citc.nce.auth.formmanagement.tempStore;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * bydud
 * 2024/2/20
 **/

@Data
@Accessors(chain = true)
public class Csp4CustomerFrom {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long newId;

    @ApiModelProperty(value = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 表单名称
     */
    @ApiModelProperty(value = "表单名称")
    private String formName;

    /**
     * 表单内容
     */
    @ApiModelProperty(value = "表单内容")
    private String formDetails;

    /**
     * 表单草稿
     */
    @ApiModelProperty(value = "表单草稿")
    private String formDraft;

    /**
     * 2已发布，1待发布，0未发布
     */
    @ApiModelProperty(value = "发布状态2已发布，1待发布，0未发布，默认0")
    private int formStatus;

    @ApiModelProperty(value = "表单封面")
    private String formCover;

    /**
     * 2已发布，1待发布，0未发布
     */
    @ApiModelProperty(value = "提交条数")
    private Long submitNum;


    @ApiModelProperty(value = "分享链接")
    private String formShareUrl;

    @ApiModelProperty(value = "短链接")
    private String shortUrl;

}
