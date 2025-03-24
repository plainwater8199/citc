package com.citc.nce.auth.sharelink.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class ShareLinkPageReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

//    @ApiModelProperty(value = "账户主键id")
//    @NotNull(message = "accountId不能为空")
//    private Long accountId;
    @ApiModelProperty(value = "账户id")
    @NotNull(message = "账户id不能为空")
    private String chatbotAccountId;
}
