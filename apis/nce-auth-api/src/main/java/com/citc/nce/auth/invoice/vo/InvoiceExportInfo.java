package com.citc.nce.auth.invoice.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 导出开票
 *
 * @author bydud
 * @since 2024/3/12
 */

@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "开票记录 导出开票")
public class InvoiceExportInfo {

    @ExcelProperty("客户名称")
    private String enterpriseAccountName;
    @ExcelProperty("发票类型")
    private String type;
    @ExcelProperty("发票金额")
    private String invoiceValue;

    @ExcelProperty("发票抬头")
    private String headerStr;
    @ExcelProperty("税号")
    private String vatNum;
    @ExcelProperty("手机号")
    private String phone;
    @ExcelProperty("开户银行")
    private String bank;
    @ExcelProperty("银行账号")
    private String bankAccountNum;
    @ExcelProperty("邮寄地址")
    private String mailingAddress;
    @ExcelProperty("收件人")
    private String mailingUserName;
    @ExcelProperty("收件人电话")
    private String mailingPhone;
    @ExcelProperty("邮件地址")
    private String email;
}
