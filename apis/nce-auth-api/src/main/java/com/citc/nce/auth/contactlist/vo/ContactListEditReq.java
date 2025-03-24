package com.citc.nce.auth.contactlist.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/15 14:34
 * @Version: 1.0
 * @Description:
 */
@Data
public class ContactListEditReq implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @ApiModelProperty("id")
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 联系人组id
     */
    @ApiModelProperty("联系人组id")
    @NotNull(message = "联系人组id不能为空")
    private Long groupId;

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
