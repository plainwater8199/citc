package com.citc.nce.auth.mobile.req;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChatBotSyncInfo extends BaseRequest {

    private String customerNum;
    //归属代理商的EC集团客户编码,如Chatbot所属客户为非直签客户时，该字段必填
    private String agentCustomerNum;
    private String chatbotId;
    private String officeCode;
    //Chatbot名称，新增时必填。
    private String name;
    //机器人logo图标的url
    private String logo;
    private String sms;
    private Integer isAuth;
    private String authName;
    private String authExpires;
    private String authOrg;
    private String callback;
    private String email;
    private String website;
    private String tcPage;
    private String address;
    private String colour;
    private String backgroundImage;
    //机器人分类，使用字符串数组，每种分类最长50字节。需要携带多个分类时，应携带多个category参数。
    private String[] category;
    private String provider;
    private String description;
    private String menu;
    //Chatbot状态，新增时必填。0 正常 1 调试
    private Integer status;
    //归属省份编号，新增时必填
    private String provinceCode;
    //归属地市编码
    private String cityCode;
    private Double lon;
    private Double lat;
    private Integer version;
    private String genericCssTemplate;
    //签名
    private String autograph;
    private String attachment;
    private Integer concurrent;
    private Integer amount;
    private Integer mAmount;
    private Integer serviceRange;
    private Integer filesizeLimit;
    private String cspToken;
    //实际下发行业，新增时必填
    private String actualIssueIndustry;
    //Chatbot创建时间，新增时必填
    private String createTime;


}
