package com.citc.nce.auth.contactbacklist.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class ContactBackListReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    private String personName;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @NotBlank(message = "手机号不能为空")
    private String phoneNum;


}
