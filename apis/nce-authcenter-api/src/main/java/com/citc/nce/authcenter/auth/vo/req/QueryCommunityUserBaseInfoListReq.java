package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class QueryCommunityUserBaseInfoListReq {
    @NotNull(message = "用户的ID列表不能为空")
    @ApiModelProperty(value = "用户id列表",required = true)
    private List<String> userIds;
}
