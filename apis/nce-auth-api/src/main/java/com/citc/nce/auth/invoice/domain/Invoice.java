package com.citc.nce.auth.invoice.domain;

import com.citc.nce.auth.invoice.enums.InvoiceType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author bydud
 * @since 2024/2/27
 */
@Getter
@Setter
@ApiModel(value = "Invoice 个人发票信息")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "invoiceType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ElectronInvoice.class, name = "VAT_SPECIAL_ELECTRONIC"),
        @JsonSubTypes.Type(value = ElectronInvoice.class, name = "VAT_ORDINARY_ELECTRONIC"),
        @JsonSubTypes.Type(value = PaperInvoice.class, name = "VAT_ORDINARY_PAPER"),
        @JsonSubTypes.Type(value = PaperInvoice.class, name = "VAT_SPECIAL_PAPER"),
})
public abstract class Invoice {
    @NotBlank(message = "抬头类型不能为空")
    @ApiModelProperty(value = "抬头类型 enterprise organize")
    private String headerType;
    @NotNull(message = "发票类型不能为空")
    @ApiModelProperty(value = "发票类型")
    private InvoiceType invoiceType;
    @NotBlank(message = "抬头不能为空")
    @Length(max = 30,message = "抬头最多30字符")
    @ApiModelProperty(value = "抬头")
    private String headerStr;
//    @NotBlank(message = "增值税税号不能为空")
    @Length(max = 20,message = "税号最多20字符")
    @ApiModelProperty(value = "税号最多20字符")
    private String vatNum;
    @Length(max = 11,message = "电话最多11字符")
//    @NotBlank(message = "电话")
    @ApiModelProperty(value = "电话最多11字符")
    private String phone;
    @Length(max = 20,message = "开户行最多20字符")
//    @NotBlank(message = "开户行名称")
    @ApiModelProperty(value = "开户行最多20字符")
    private String bank;
    @Length(max = 20,message = "银行账号最多20字符")
//    @NotBlank(message = "开户行账号")
    @ApiModelProperty(value = "银行账号最多20字符")
    private String bankAccountNum;
}

