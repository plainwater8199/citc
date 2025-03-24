package com.citc.nce.keywordsreply.resp;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/5/29 15:23
 */
@Data
@ApiModel
public class KeywordsReplyListResp {
    private Long id ;

    @ApiModelProperty("关键词")
    private String keywords ;

    @ApiModelProperty("回复模板id")
    private Long replTemplateId;

    @ApiModelProperty("回复模板名称")
    private String replTemplateName;

    @ApiModelProperty("关联账号")
    private String applyAccounts;

    @ApiModelProperty("关联账号名称")
    private String applyAccountNames;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
