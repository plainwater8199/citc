package com.citc.nce.auth.sharelink.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class ShareLinkReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 链接名称
     */
    @ApiModelProperty(value ="链接名称")
    @NotBlank(message = "链接名称不能为空")
    private String linkName;

    @ApiModelProperty(value = "账户id")
    @NotNull(message = "账户id不能为空")
    private String chatbotAccountId;

}
