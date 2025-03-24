package com.citc.nce.keywordsreply.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jcrenc
 * @since 2024/5/29 16:07
 */
@Data
@ApiModel
public class KeywordsReplySearchReq extends PageParam {

    @ApiModelProperty("关联账号")
    private String applyAccount;
}
