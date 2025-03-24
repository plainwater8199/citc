package com.citc.nce.auth.messagetemplate.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yy
 * @date 2024-03-01 11:19:55
 * 蜂动模板状态回调网关传输类
 */
@Data
public class TemplateStatusCallbackReq {
    /**
     * 模板ID
     */
    String id ;
    /**
     * 模板类型
     * RCS 5G消息模板
     * AIM 智能短信模板
     * MMS 视频短信模板
     */
    String type;
    /**
     * 模板状态 PENGDING 审核中
     * SUCCESS 审核成功
     * FAILED 审核失败
     * EXPIRED 过期（MMS类模板有过期状态）
     */
    String  templateStatus ;
    /**
     * 审核结果描述
     */
    String remark;
    /**
     * ⽀持的⼚家或者运营商，AIM消息返回
     * 的是⽀持⼚家，RCS和MMS消息返回
     * 的是⽀持运营商
     */
    List<SupportDetailsReq> supportDetails;
}
