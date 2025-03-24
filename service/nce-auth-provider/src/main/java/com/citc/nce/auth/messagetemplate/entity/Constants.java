package com.citc.nce.auth.messagetemplate.entity;

/**
 * 模板常用返回描述
 * @author yy
 * @date 2024-03-15 16:03:12
 */
public interface Constants {
    String TEMPLATE_SAVE_AUDIT_FAILED="模板保存成功";
    String TEMPLATE_AUDIT_FAILED="%s通道送审失败";
    String TEMPLATE_SAVE_AUDIT_SUCCESS="模板创建成功，送审中";
    String MATERIAL_NOTEXISTS_SHARE ="消息模板送审通道与素材过审通道不一致";
    String MATERIAL_NOTSUPPORT_ACCOUNT ="模板引用素材与当前送审账号不匹配";
    String MATERIAL_NOTSUPPORT_ACCOUNT_FOR_BATCHSEND ="模板引用素材与当前群发账号不匹配";
    String TEMPLATE_NOT_PROVED ="模板在该渠道未审核通过";
}
