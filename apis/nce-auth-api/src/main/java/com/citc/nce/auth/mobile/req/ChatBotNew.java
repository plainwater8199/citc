package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 新增chatbot 内容
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ChatBotNew extends BaseRequest {

   //Chatbot归属的EC ID（客户编码）
   private String  customerNum;

   private String serviceCode;

   //Chatbot ID，包含域名
   private String  chatbotId;
   //归属代理商的EC集团客户编码
   private String agentCustomerNum;
   //Chatbot名称
   private String name;
   //机器人logo图标的url
   private String logo;
   //短信端口号。
   private String sms;
   private String callback;
   private String email;
   private String website;
   private String tcPage;
   private String address;
   private String colour;
   private String backgroundImage;
   //行业类型
   private String[] category;
   //提供者信息。提供者开关的值是1时必填,必须是归属非直签客户名称
   private String provider;
   //提供者开关，默认为1 (1-显示 0-不显示)
   private String providerSwitchCode;
   //描述信息
   private String description;
   //归属省份编号
   private String provinceCode;
   //归属地市编号
   private String cityCode;
   //归属大区
   private String officeCode;
   private String lon;
   private String lat;
   //签名
   private String autograph;
   private String attachment;
   private String createTime;
   private String debugWhiteAddress;
   private String auditPerson;
   private String auditOpinion;
   private String auditTime;
   //实际下发行业
   private String actualIssueIndustry;
}
