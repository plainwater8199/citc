package com.citc.nce.auth.invoice.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author bydud
 * @since 2024/2/27
 */

@Getter
@Setter
public class PaperInvoice extends Invoice {
    @Length(max = 50,message = "邮寄地址最多50字符")
    @NotBlank(message = "邮寄地址")
    @ApiModelProperty(value = "邮寄地址最多50字符")
    private String mailingAddress;
    @Length(max =20,message = "收件人姓名最多20字符")
    @NotBlank(message = "邮寄收件人姓名")
    @ApiModelProperty(value = "收件人姓名最多20字符")
    private String mailingUserName;
    @Length(max =11,message = "收件人电话最多11字符")
    @NotBlank(message = "邮寄收件人电话")
    @ApiModelProperty(value = "收件人电话最多11字符")
    private String mailingPhone;

}
