package com.citc.nce.authcenter.captch.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/30 15:20
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CaptchaMailDto implements Serializable {
    /**
     * 邮件主题
     */
    private String title;
    /**
     * 邮件文本内容，最多支持10000个字符，中文字符算两个字符
     */
    private String message;
    /**
     * 收件人列表，收件人和抄送人总数不能超过40个
     */
    private String[] receivers;
    /**
     * 抄送人列表，非必填，默认为[]，收件人和抄送人总数不能超过40个
     */
    private String[] ccReceives;
    /**
     * 发件人昵称，可选，默认为“中国联通邮件服务”
     */
    private String sender_name;
    /**
     * 发件人邮箱，可选，若填写则表示由中国联通企业邮箱代发的来自sender_host的邮件
     */
    private String sender_host;
    /**
     * # 邮件文本内容编码方式，可选，默认为“utf-8”
     */
    private String charset;
    /**
     * 附件列表，所有附件的大小之和不能超过10MB
     */
    private List<CaptchaMailFileDto> files;
}
