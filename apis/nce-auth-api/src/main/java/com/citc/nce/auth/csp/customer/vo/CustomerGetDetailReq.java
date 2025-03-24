package com.citc.nce.auth.csp.customer.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class CustomerGetDetailReq implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "用户id不能为空")
    private String userId;


}
