package com.citc.nce.auth.readingLetter.shortUrl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 文件名:ApplyShortUrlResultVo
 * 创建者:zhujinyu
 * 创建时间:2024/7/15 17:32
 * 描述:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyShortUrlResultVo implements Serializable {
    private static final long serialVersionUID = 1L;
//    templateId Long M 模板Id
//    paramList List M 参数对象集合
//    SendParam.custFlag String M 客户标识
//    SendParam.custId String O 客户业务系统内的标识
//    dyncParams Map<String,String> O
//    模板动态参数集合，KEY为参数
//    名，VALUE为参数值
//    SendParam.customUrl String O
//    自定义跳转链接，不超过128个
//    字符.
//    SendParam.aimUrl String M 短链
//    SendParam.aimCode String M 短码
//    SendParam.resultCode String M
//        0：申请成功
//    非0：申请失败
//            错误码参考ResultCode
//    SendParam.errorMessage String O 错误描述
//    SendParam.aimUrlId Long M
//    短链ID，通过这个ID获取短链  详情
    private Long templateId;

    private List<SendParam> paramList;


}
