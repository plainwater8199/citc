package com.citc.nce.auth.prepayment.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author jiancheng
 */
@Data
@ApiModel
public class VideoSmsPlanOrderQueryVo extends PageParam {
    @NotEmpty
    @ApiModelProperty("视频短信账号")
    private String accountId;

}
