package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 签约客户信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SignedCustomer extends BaseRequest{

   //申请时间YYYY-MM-DD
   private String applyTime;
   private String customerNum;

   //客户名称。同一个代理商下客户名称+归属区域唯一
   private String customerName;
   //客户联系人
   private String customerContactPerson;
   //联系人手机
   private String contactPersonPhone;
   //联系人邮箱
   private String contactPersonEmail;
   //归属区域编码，大区，省分，城市用逗号间隔 省份必须与代理商的省份一致
   private String belongRegionCode;
   //代理商名称
   private String belongAgentName;
   //代理商编码
   private String belongAgentCode;
   //行业类型编码。参见附录4.3行业编码的二级行业编码
   private String industryTypeCode;
   //附件url通过地址返回实体附件
   //(支持的文件类型：
   //pdf,doc,docx,xls,xlsx,ppt,pptx,
   //jpg,jpeg,gif,rar,7z,zip 大小为10M）
   private String customerUrl;
   //备注
   private String remarkText;
   //合同编号
   private String contractCode;
   //合同名称
   private String contractName;
   //合同生效日期YYYY-MM-DD
   private String contractValidDate;
   //合同失效日期YYYY-MM-DD
   private String contractInvalidDate;
   //合同是否自动续签：1：是、0：否
   private String isRenewed;
   //合同续签日期YYYY-MM-DD
   private String contractRenewDate;
   //合同电子扫描件url通过地址返回实体附件
   //（附件类型支持：pdf、doc、docx、jpg、jpeg、gif、rar；大小限10M）
   private String contractUrl;
   //审核人员
   private String auditPerson;
   //审核意见
   private String auditOpinion;
   //审核时间YYYY-MM-DD
   private String auditTime;
   //统一社会信用代码
   private String unifySocialCreditCodes;
   //企业责任人姓名
   private String enterpriseOwnerName;
   //企业责任人证件类型
   //证件类型：01-居民身份证、02-中国人民解放军军人身份证件、03-中国人民武装警察身份证件
   private String certificateType;
   //企业责任人证件号码
   private String certificateCode;
}
