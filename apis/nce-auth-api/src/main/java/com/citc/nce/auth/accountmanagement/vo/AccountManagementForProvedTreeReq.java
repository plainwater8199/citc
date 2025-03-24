package com.citc.nce.auth.accountmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class AccountManagementForProvedTreeReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板id")
    private Long templateId;

    /**
     * 账号名称
     */
    @ApiModelProperty(value = "isHide是否展示")
    private int isHide;

}
