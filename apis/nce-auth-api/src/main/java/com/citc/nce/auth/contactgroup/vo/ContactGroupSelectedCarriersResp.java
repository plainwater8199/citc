package com.citc.nce.auth.contactgroup.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zjy
 * @Date: 2024/8/6 17:19
 * @Version: 1.0
 * @Description:
 * 各联系人组的人数
 */
@Data
public class ContactGroupSelectedCarriersResp implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("移动人数")
    private int mobileNumber;

    @ApiModelProperty("电信人数")
    private int telecomNumber;

    @ApiModelProperty("联通人数")
    private int unicomNumber;
}
