package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_sms_template")
@Accessors(chain = true)
public class SmsTemplateDo extends BaseDo<SmsTemplateDo> {
    private static final long serialVersionUID = 4264049361711558723L;

    /**
     * 用户Id
     */
//    private String userId; //TODO fq

    private String customerId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 平台模板ID
     */
    private String platformTemplateId;

    /**
     * 所属视频短信账号
     */
    private String accountId;

    /**
     * 关联签名ID
     */
    private Long signatureId;

    /**
     * 删除时间
     */
    private Date deletedTime;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 模板类型(普通模板:1,个性模板:2)
     */
    private Integer templateType;

    private String creatorOld;

    private String updaterOld;
}
