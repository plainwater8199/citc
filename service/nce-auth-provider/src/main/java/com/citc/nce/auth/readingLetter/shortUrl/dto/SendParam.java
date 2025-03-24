package com.citc.nce.auth.readingLetter.shortUrl.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class SendParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 客户标识
     */
    String custFlag;
    /**
     * 客户业务系统内的标识
     */
    String custId;
    /**
     * 自定义跳转链接，不超过
     * 256个字符.如填写，则需要
     * 提前一个工作日将对应链接
     * 提供给运营商进行报备。
     */
    String customUrl;
    /**
     * 模板动态参数集合，KEY为
     * 参数名，VALUE为参数值
     */
    Map<String,String> dyncParams;
    /**
     * 自定义短码，3到10位的数
     * 字或大小写字母，及“ - ”“ _
     * ”“ . ” 这三种符号；如果带
     * 了自定义短码，则必须同时
     * 指定自定义域名
     */
    String customShortCode;
    /**
     * 短链
     */
    String aimUrl;
    /**
     * 短码
     */
    String aimCode;
    /**
     * 0：申请成功
     * 非0：申请失败
     */
    String resultCode;
    /**
     * 错误描述
     */
    String errorMessage;
    /**
     * 短链ID，通过这个ID获取短链
     * 详情
     */
    String aimUrlId;
}