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
 * @author jiancheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("csp_video_sms_template")
@Accessors(chain = true)
public class CspVideoSmsTemplateDo extends BaseDo<CspVideoSmsTemplateDo> {
    private static final long serialVersionUID = 4264049361711558750L;

    private String customerId;
    /*模板名称*/
    private String templateName;

    /*平台模板ID*/
    private String platformTemplateId;

    /*所属视频短信账号*/
    private String accountId;

    /*关联签名ID*/
    private Long signatureId;

    private Date deletedTime;

    /**
     * 模板类型 1:普通模板，2:个性模板
     */
    private Integer templateType;

    private String creatorOld;

    private String updaterOld;
}
