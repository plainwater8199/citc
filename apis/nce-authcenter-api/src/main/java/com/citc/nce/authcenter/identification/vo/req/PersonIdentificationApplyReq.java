package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class PersonIdentificationApplyReq {
    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotBlank(message = "用户id不能为空")
    private String userId;

    @ApiModelProperty(value = "姓名", dataType = "String", required = true)
    @NotBlank(message = "姓名不能为空")
    @Length(max = 64, message = "姓名长度超过限制")
    private String personName;

    @ApiModelProperty(value = "身份证号", dataType = "String", required = true)
    @NotBlank(message = "身份证号不能为空")
    @Length(max = 18, message = "身份证号长度超过限制")
    private String idCard;

    @ApiModelProperty(value = "身份证正面照片", dataType = "String", required = true)
    @NotBlank(message = "身份证正面照片不能为空")
    private String idCardImgFront;

    @ApiModelProperty(value = "身份证反面照片", dataType = "String", required = true)
    @NotBlank(message = "身份证反面照片不能为空")
    private String idCardImgBack;

    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer", required = true)
    @NotNull(message = "平台信息不能为空")
    private Integer protal;
}
