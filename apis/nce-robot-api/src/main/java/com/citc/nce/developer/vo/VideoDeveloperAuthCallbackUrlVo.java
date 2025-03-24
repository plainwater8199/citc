package com.citc.nce.developer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @author ping chen
 */
@Data
public class VideoDeveloperAuthCallbackUrlVo {

    @ApiModelProperty("回调地址")
    @Length(max = 1000, message = "回调地址长度最大允许1000字符,可以为空")
    private String callbackUrl;

}
