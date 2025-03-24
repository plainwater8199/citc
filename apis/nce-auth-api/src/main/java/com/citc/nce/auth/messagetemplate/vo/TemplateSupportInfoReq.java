package com.citc.nce.auth.messagetemplate.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件名:TemplateSupportInfoDTO
 * 创建者:zhujinyu
 * 创建时间:2024/7/10 15:32
 * 描述:
 */
@Data
public class TemplateSupportInfoReq implements Serializable {
    private static final long serialVersionUID = 1L;
//    审核状态， PENDING,SUCCESS,FAILED
    String auditStatus;
    String auditRemark;
    String auditDate;
    String support;
}
