package com.citc.nce.authcenter.auth.vo.resp;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zhujy
 * @Date: 2024/3/16 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class SupplierChatbotExcelInfoResp implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("签名")
    @ExcelProperty(value = "签名", index = 0)
    private String chatbotSmsSign;

    @ApiModelProperty("chatbot名称")
    @ExcelProperty(value = "应用名称", index = 1)
    private String chatbotName;

    @ApiModelProperty("客户企业名称")
    @ExcelProperty(value = "企业名称", index = 2)
    private String enterpriseName;

    // 二级行业
    @ApiModelProperty(value = "二级行业", dataType = "int", required = true)
    @ExcelProperty(value = "所属行业", index = 3)
    private String chatbotSecondIndustryType;

    @ApiModelProperty("所属地区")
    @ExcelProperty(value = "所属地区", index = 4)
    private String city;

    // 办公电话
    @ApiModelProperty(value = "办公电话", dataType = "String")
    @ExcelProperty(value = "办公电话", index = 5)
    private String workPhone;

    // 联系人信息
    // 客户联系人
    @ApiModelProperty(value = "联系人", dataType = "string", required = true)
    @ExcelProperty(value = "联系人名称", index = 6)
    private String contactName;

    @ApiModelProperty(value = "联系电话", dataType = "Integer")
    @ExcelProperty(value = "联系电话", index = 7)
    private String contactPhoneNumber;

    @ApiModelProperty(value = "联系邮箱", dataType = "String")
    @ExcelProperty(value = "联系邮箱", index = 8)
    private String contactMail;

    @ApiModelProperty("描述")
    @ExcelProperty(value = "描述", index = 9)
    private String chatbotServiceDesc;

    @ApiModelProperty("回叫号码")
    @ExcelProperty(value = "回叫号码", index = 10)
    private String chatbotCallBackNumber;

    @ApiModelProperty("chatbot邮箱")
    @ExcelProperty(value = "chatbot邮箱", index = 11)
    private String chatbotMail;

    @ApiModelProperty("chatbot网站地址")
    @ExcelProperty(value = "企业网址", index = 12)
    private String serviceWebsite;

    @ApiModelProperty("chatbot地址")
    @ExcelProperty(value = "企业地址", index = 13)
    private String address;//Chatbot详细地址

    @ApiModelProperty("chatbot服务条款")
    @ExcelProperty(value = "服务条款", index = 14)
    private String serviceTerm;//

//    @ApiModelProperty("背景图片url")
//    @ExcelProperty(value = "背景图片url", index = 15)
//    private String backgroundImgUrl;

    @ApiModelProperty("手机号白名单")
    @ExcelProperty(value = "手机号白名单", index = 15)
    private String whiteList;

    /**
     * 账户类型，1联通2硬核桃
     * 2023/2/20 注意：数据库存的名字，不是数字类型
     */
    @ApiModelProperty(value = "账户类型，1联通2硬核桃")
    @ExcelProperty(value = "运营商", index = 16)
    private String accountType;

}