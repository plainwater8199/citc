package com.citc.nce.auth.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: ylzouf
 * @Date: 2022/8/15 14:49
 * @Version 1.0
 * @Description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
@Builder
@HeadRowHeight(value = 30)
@ContentRowHeight(value = 25)
public class UserExcel {
    @ExcelProperty(value = "序号")
    private Integer index;
    @ExcelProperty(value = "账户名")
    private String account;
    @ExcelProperty(value = "用户ID")
    private String userId;
    @ExcelProperty("违规次数")
    private Integer unruleNum;
    @ExcelProperty("电话")
    private String phone;
    @ExcelProperty(" 邮箱")
    private String mail;
    @ExcelProperty("账号状态")
    private String userStatus;
    @ExcelProperty("注册时间")
    private Date registerTime;
    @ExcelProperty("账户资质")
    private String userCertificate;
    @ExcelProperty("认证状态")
    private String userCertificateApplyStatus;
}
