package com.citc.nce.authcenter.auth.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserNameInfo {
    @ExcelProperty(value = "账户ID")
    @ApiModelProperty(value = "用户ID", dataType = "String")
    private String userId;
    @ExcelProperty(value = "账户名")
    @ApiModelProperty(value = "用户姓名", dataType = "String")
    private String name;
}
