package com.citc.nce.keywordsreply.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jcrenc
 * @since 2024/5/29 16:07
 */
@Data
@ApiModel
public class KeywordsReplyAddReq {
    @ApiModelProperty("关键词")
    @NotNull
    @Length(max = 25)
    private String keywords ;

    @ApiModelProperty("回复模板id")
    @NotNull
    private Long replyTemplateId;

    @ApiModelProperty("关联账号")
    @NotEmpty
    private String applyAccounts;
}
